package com.aris.weatherapp.data.network

import com.aris.weatherapp.data.model.current_weather.WeatherModel
import com.aris.weatherapp.data.model.weather_forecast.WeatherForecastModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {

    @GET("weather?units=metric")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") Long: Double,
        @Query("appid") appid: String,
        @Query("lang") lang: String = "fa"
    ): Response<WeatherModel>

    @GET("forecast?units=metric")
    suspend fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") Long: Double,
        @Query("appid") appid: String,
    ): Response<WeatherForecastModel>

}

/*
// add farsi
 @GET("forecast?units=metric&cnt=1")
suspend fun getCurrentWeather(
    @Query("lat") lat: Double,
    @Query("lon") Long: Double,
    @Query("appid") appid: String,
    @Query("lang") lang: String = "fa",
): Response<WeatherModel>
*/

// @Query("lang") lang: String = "fa"    زبان فارسی
// units=metric  واحد اندازه گیری
// cnt=1 تعداد زمان های آب و هوا درخواستی