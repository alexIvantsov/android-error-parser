package com.gbksoft.errorparser

import com.gbksoft.errorparser.connectivityManager.NoConnectivityException
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.io.IOException

class HttpRequestErrorParser(
    private var defaultErrorParser: ((String?) -> Any?)? = null,
    private var defaultErrorHandler: ((Error<in Any>) -> Unit)? = null
) {

    private val errorParsers = HashMap<Int, ((String?) -> Any?)>()
    private val errorHandlers = HashMap<Int, ((Error<in Any>) -> Unit)>()

    fun parse(throwable: Throwable) {
        parse(throwable, null, null)
    }

    internal fun parse(
        throwable: Throwable,
        innerParsers: Map<Int, ((String?) -> Any?)>? = null,
        innerHandlers: Map<Int, ((Error<in Any>) -> Unit)>? = null
    ) {
        when (throwable) {
            is HttpException -> parseHttpException(throwable, innerParsers, innerHandlers)
            is JsonSyntaxException -> parseErrorBody(
                throwable.message,
                NonHttpErrorCode.JSON_SYNTAX_ERROR,
                innerParsers,
                innerHandlers
            )
            is NoConnectivityException -> parseErrorBody(
                throwable.message,
                NonHttpErrorCode.CODE_CONNECTION_ERROR,
                innerParsers,
                innerHandlers
            )
            else -> parseErrorBody(
                throwable.message,
                NonHttpErrorCode.CODE_OTHER_ERROR,
                innerParsers,
                innerHandlers
            )
        }
    }

    fun setParser(code: Int, parser: (String?) -> Any?) {
        errorParsers[code] = parser
    }

    fun setHandler(code: Int, handler: (Error<in Any>) -> Unit) {
        errorHandlers[code] = handler
    }

    fun uniqueParser() = UniqueParserBuilder(this)

    private fun parseHttpException(
        httpException: HttpException,
        innerParsers: Map<Int, ((String?) -> Any?)>? = null,
        innerHandlers: Map<Int, ((Error<in Any>) -> Unit)>? = null
    ) {
        try {
            val response = httpException.response()
            parseErrorBody(
                response?.errorBody()?.string(),
                response?.code() ?: NonHttpErrorCode.CODE_OTHER_ERROR,
                innerParsers,
                innerHandlers
            )
        } catch (err: IOException) {
            parseErrorBody(
                err.message,
                NonHttpErrorCode.CODE_OTHER_ERROR,
                innerParsers,
                innerHandlers
            )
        }
    }

    private fun parseErrorBody(
        errorBody: String?,
        code: Int,
        innerParsers: Map<Int, ((String?) -> Any?)>? = null,
        innerHandlers: Map<Int, ((Error<in Any>) -> Unit)>? = null
    ) {
        val parser =
            innerParsers?.get(code) ?: errorParsers[code] ?: defaultErrorParser ?: return
        val parsedError = parser.invoke(errorBody)
        val error = Error(code, parsedError)

        val handler =
            innerHandlers?.get(code) ?: errorHandlers[code] ?: defaultErrorHandler ?: return
        handler.invoke(error)
    }
}