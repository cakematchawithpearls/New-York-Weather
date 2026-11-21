package com.example.newyorkweather

import com.squareup.moshi.Json

data class OpenWeatherMapResponseData(
    @Json(name = "name") val locationName: String,
    val weather: List<OpenWeatherMapWeatherData>
)

data class OpenWeatherMapWeatherData(
    val main: String,
    val description: String,
    val icon: String
)