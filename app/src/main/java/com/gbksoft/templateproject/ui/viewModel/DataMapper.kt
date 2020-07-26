package com.gbksoft.templateproject.ui.viewModel

import android.content.Context
import com.gbksoft.templateproject.R
import kotlin.math.roundToInt

class DataMapper(private val context: Context) {

    fun formatTemperature(value: Float): String {
        val temperatureString = value.roundToInt().toString()
        val sign = if (value > 0) "+" else ""
        return context.getString(R.string.temperature, (sign + temperatureString))
    }
}