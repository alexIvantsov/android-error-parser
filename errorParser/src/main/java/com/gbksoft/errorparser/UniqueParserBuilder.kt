package com.gbksoft.errorparser

class UniqueParserBuilder(private val errorParserHttp: HttpRequestErrorParser) {

    private val innerParsers = HashMap<Int, ((String?) -> Any?)>()
    private val innerHandlers = HashMap<Int, ((Error<in Any>) -> Unit)>()

    fun setParser(code: Int, parser: (String?) -> Any?): UniqueParserBuilder {
        innerParsers[code] = parser
        return this
    }

    fun setHandler(code: Int, handler: (Error<in Any>) -> Unit): UniqueParserBuilder {
        innerHandlers[code] = handler
        return this
    }

    fun parse(throwable: Throwable) = errorParserHttp.parse(throwable, innerParsers, innerHandlers)

}