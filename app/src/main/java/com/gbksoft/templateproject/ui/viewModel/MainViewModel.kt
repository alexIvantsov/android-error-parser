package com.gbksoft.templateproject.ui.viewModel

import android.location.Location
import androidx.lifecycle.*
import com.gbksoft.errorparser.Error
import com.gbksoft.errorparser.HttpRequestErrorParser
import com.gbksoft.templateproject.data.weather.repository.WeatherRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val weatherRepository: WeatherRepository,
    private val dataMapper: DataMapper,
    private val httpRequestErrorParser: HttpRequestErrorParser
) : ViewModel() {

    private val _temperatureLiveData = MutableLiveData<String>()
    val temperatureLiveData = _temperatureLiveData as LiveData<String>

    private val _errorLiveData = MutableLiveData<Error<in Any>>()
    val errorLiveData = _errorLiveData.map { Error(it.code, it.errorBody as String) }

    private val _specificObjectErrorLiveData = MutableLiveData<Error<in Any>>()
    val specificObjectErrorLiveData =
        _specificObjectErrorLiveData.map { Error(it.code, it.errorBody as Error401) }

    init {
        httpRequestErrorParser.setHandler(400, {
            //Handle 400
        })
    }

    fun validRequest(location: Location) {
        viewModelScope.launch {
            kotlin.runCatching {
                val weather = weatherRepository.getWeather(
                    location.latitude.toFloat(),
                    location.longitude.toFloat()
                )
                _temperatureLiveData.value = dataMapper.formatTemperature(weather.temp)
            }.onFailure { httpRequestErrorParser.parse(it) }
        }
    }

    fun requestWithDefaultHandlerAndParser(location: Location) {
        viewModelScope.launch {
            kotlin.runCatching {
                val weather = weatherRepository.getWeatherInvalidRequest(
                    location.latitude.toFloat(),
                    location.longitude.toFloat()
                )
                _temperatureLiveData.value = dataMapper.formatTemperature(weather.temp)
            }.onFailure { httpRequestErrorParser.parse(it) }
        }
    }

    fun requestWithDefaultParserButCustomHandler(location: Location) {
        viewModelScope.launch {
            kotlin.runCatching {
                val weather = weatherRepository.getWeatherInvalidRequest(
                    location.latitude.toFloat(),
                    location.longitude.toFloat()
                )
                _temperatureLiveData.value = dataMapper.formatTemperature(weather.temp)
            }.onFailure {
                httpRequestErrorParser.uniqueParser()
                    .setHandler(401) { _errorLiveData.value = it }
                    .parse(it)
            }
        }
    }

    fun requestWithCustomHandlerAndParser(location: Location) {
        viewModelScope.launch {
            kotlin.runCatching {
                val weather = weatherRepository.getWeatherInvalidRequest(
                    location.latitude.toFloat(),
                    location.longitude.toFloat()
                )
                _temperatureLiveData.value = dataMapper.formatTemperature(weather.temp)
            }.onFailure {
                httpRequestErrorParser.uniqueParser()
                    .setParser(401) { Error401() }
                    .setHandler(401) { _specificObjectErrorLiveData.value = it }
                    .parse(it)
            }
        }
    }

    class Error401(val message: String = "Some specific object for 401 error")
}
