package com.gbksoft.templateproject.data.weather.repository

import com.gbksoft.templateproject.data.weather.network.WeatherRequestManager

class WeatherRepository(private val requestManager: WeatherRequestManager) {

    private val appId = "b4857a85703ea21a4922732b725ed141"

    suspend fun getWeather(lat: Float, lng: Float) =
        requestManager.service.getWeather(appId, lat, lng)

    suspend fun getWeatherInvalidRequest(lat: Float, lng: Float) =
        requestManager.service.getWeather(appId + "Suffix", lat, lng)
}
