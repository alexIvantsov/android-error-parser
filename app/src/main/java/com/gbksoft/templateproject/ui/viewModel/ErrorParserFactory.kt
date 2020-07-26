package com.gbksoft.templateproject.ui.viewModel

import android.content.Context
import android.widget.Toast
import com.gbksoft.errorparser.HttpRequestErrorParser
import com.gbksoft.templateproject.R
import org.json.JSONException
import org.json.JSONObject

/**
 * Provides error parser instance for each viewModel class
 * This class shouldn't be a singleton
 * Default error parsers and handlers might be inserted here
 * Default parsers and handlers will be applied for every request if they will not be overridden there
 * This class is specific for each application and might be included in app module in dagger
 */
class ErrorParserFactory(private val context: Context) {

    fun provideErrorParser(): HttpRequestErrorParser {
        val errorParser = HttpRequestErrorParser(
            defaultErrorParser = { it },
            defaultErrorHandler = {
                Toast.makeText(
                    context,
                    context.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        errorParser.setParser(401) {
            it?.let {
                try {
                    val jsonObject = JSONObject(it)
                    jsonObject.get("message") as String
                } catch (e: JSONException) {
                    null
                }
            }
        }

        errorParser.setHandler(401) {
            Toast.makeText(
                context,
                it.errorBody as String,
                Toast.LENGTH_LONG
            ).show()
        }

        return errorParser
    }
}