package com.gbksoft.templateproject.ui

import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gbksoft.templateproject.R
import com.gbksoft.templateproject.data.weather.network.WeatherRequestManager
import com.gbksoft.templateproject.data.weather.repository.WeatherRepository
import com.gbksoft.templateproject.ui.viewModel.DataMapper
import com.gbksoft.templateproject.ui.viewModel.ErrorParserFactory
import com.gbksoft.templateproject.ui.viewModel.MainViewModel
import com.gbksoft.templateproject.ui.viewModel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val location = Location("fused").apply {
        latitude = 50.426763
        longitude = 30.503340
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModelFactory = ViewModelFactory(
            WeatherRepository(WeatherRequestManager(this)),
            DataMapper(this),
            ErrorParserFactory(this)
        )

        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.temperatureLiveData.observe(this, Observer {
            tvTemperature.text = getString(R.string.outside_temperature, it)
        })

        viewModel.errorLiveData.observe(this, Observer {
            Snackbar.make(root, it.errorBody, Snackbar.LENGTH_LONG).show()
        })

        viewModel.specificObjectErrorLiveData.observe(this, Observer {
            Snackbar.make(root, it.errorBody.message, Snackbar.LENGTH_LONG).show()
        })

        btnValid.setOnClickListener {
            viewModel.validRequest(location)
        }

        btnDefault.setOnClickListener {
            viewModel.requestWithDefaultHandlerAndParser(location)
        }

        btnDefaultParser.setOnClickListener {
            viewModel.requestWithDefaultParserButCustomHandler(location)
        }

        btnCustom.setOnClickListener {
            viewModel.requestWithCustomHandlerAndParser(location)
        }
    }
}
