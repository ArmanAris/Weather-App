package com.aris.weatherapp.data.model.weather_forecast

data class WeatherForecastModel(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<WeatherForecast>,
    val message: Int
)