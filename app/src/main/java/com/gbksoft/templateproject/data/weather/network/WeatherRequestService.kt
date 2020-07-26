package com.gbksoft.templateproject.data.weather.network

import com.gbksoft.templateproject.data.weather.model.Weather
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherRequestService {

    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("APPID") appId: String,
        @Query("lat") lat: Float,
        @Query("lon") lng: Float
    ): Weather

}