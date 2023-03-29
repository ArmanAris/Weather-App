package com.aris.weatherapp.repository

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Application
import android.content.Context
import android.location.Location
import android.os.Looper
import android.provider.ContactsContract
import android.provider.ContactsContract.RawContacts.Data
import androidx.annotation.RequiresPermission
import com.aris.weatherapp.data.model.current_weather.WeatherModel
import com.aris.weatherapp.data.model.weather_forecast.WeatherForecastModel
import com.aris.weatherapp.data.network.OpenWeatherService
import com.aris.weatherapp.util.Constants.api_key
import com.google.android.gms.location.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.Calendar
import java.util.Date
import java.util.concurrent.locks.Condition
import javax.inject.Inject



class WeatherRepository @Inject constructor(
    private val application: Application,
    private val api: OpenWeatherService,
) {


    @ExperimentalCoroutinesApi
    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun currentLocationWeather(): Flow<WeatherModel?> {
        return locationFlow().map {
            api.getCurrentWeather(it.latitude, it.longitude, api_key)
                .body()
        }
    }

    @ExperimentalCoroutinesApi
    @RequiresPermission(ACCESS_FINE_LOCATION)
    fun weatherForecast(): Flow<WeatherForecastModel?> {
        return locationFlow().map { its ->
            api.getWeatherForecast(its.latitude, its.longitude, api_key)
                .body()
        }
    }

    @ExperimentalCoroutinesApi
    @RequiresPermission(ACCESS_FINE_LOCATION)
    private fun locationFlow() = channelFlow<Location> {

        val client = LocationServices.getFusedLocationProviderClient(application)

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                trySend(result.lastLocation!!)
            }
        }

        val request = LocationRequest.create()
            .setInterval(10_000)
            .setFastestInterval(5_000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setSmallestDisplacement(170f)

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }

}