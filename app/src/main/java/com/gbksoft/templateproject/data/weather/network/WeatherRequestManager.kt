package com.gbksoft.templateproject.data.weather.network

import android.content.Context
import com.gbksoft.errorparser.connectivityManager.ConnectivityInterceptor
import com.gbksoft.errorparser.connectivityManager.ConnectivityManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRequestManager(context: Context) {
    val service: WeatherRequestService

    private val url = "https://api.openweathermap.org/"

    init {
        val connectivityManager = ConnectivityManager(context)
        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor(HttpLoggingInterceptor())
        httpClient.addInterceptor(ConnectivityInterceptor(connectivityManager))

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        service = retrofit.create(WeatherRequestService::class.java)
    }
}