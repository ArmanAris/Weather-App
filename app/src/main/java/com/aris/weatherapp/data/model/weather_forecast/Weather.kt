package com.aris.weatherapp.data.model.weather_forecast

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)