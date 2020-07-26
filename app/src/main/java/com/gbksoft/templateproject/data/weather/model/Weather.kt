package com.gbksoft.templateproject.data.weather.model

data class Weather(private val main: Main) {

    val temp: Float
        get() = main.temp - 273.15f

    class Main(var temp: Float)
}

