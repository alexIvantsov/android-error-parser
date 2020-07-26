package com.gbksoft.templateproject.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gbksoft.errorparser.HttpRequestErrorParser
import com.gbksoft.templateproject.data.weather.repository.WeatherRepository

class ViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val dataMapper: DataMapper,
    private val errorParserFactory: ErrorParserFactory
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val constructor = modelClass.getConstructor(
            WeatherRepository::class.java,
            DataMapper::class.java,
            HttpRequestErrorParser::class.java
        )
        return constructor.newInstance(
            weatherRepository,
            dataMapper,
            errorParserFactory.provideErrorParser()
        )
    }
}