package com.aris.weatherapp.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aris.weatherapp.data.model.current_weather.WeatherModel
import com.aris.weatherapp.data.model.weather_forecast.WeatherForecastModel
import com.aris.weatherapp.data.network.OpenWeatherService
import com.aris.weatherapp.repository.WeatherRepository
import com.aris.weatherapp.util.Constants.api_key
import com.google.android.gms.location.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("MissingPermission")
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val api: OpenWeatherService,
    private val application: Application,
) : ViewModel() {

    //1
/*    private var lastLocation: Location? = null

    fun weather(): MutableSharedFlow<WeatherModel?> {

        val weatherStateFlow = MutableSharedFlow<WeatherModel?>()

        val client = LocationServices.getFusedLocationProviderClient(application)

        val request = LocationRequest.create()
            .setInterval(10_000)
            .setFastestInterval(5_000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setSmallestDisplacement(170f)

        client.requestLocationUpdates(request, object : LocationCallback() {

            override fun onLocationResult(result: LocationResult) {
                lastLocation = result.lastLocation

                viewModelScope.launch {
                    val response = api.getCurrentWeather(
                        lastLocation?.latitude ?: 0.0,
                        lastLocation?.longitude ?: 0.0,
                        api_key
                    )
                    weatherStateFlow.emit(response.body())
                    // response == Response<WeatherModel>
                    // response.body() == WeatherModel
                    // response.code()
                    // lastLocation
                }
            }
            //   override fun onLocationAvailability(availability: LocationAvailability) = Unit
        }, Looper.getMainLooper())
        return weatherStateFlow
    }

    fun forecast(): MutableSharedFlow<WeatherForecastModel?> {

        val weatherForecastStateFlow = MutableSharedFlow<WeatherForecastModel?>()

        viewModelScope.launch {
            val response = api.getWeatherForecast(
                lastLocation?.latitude ?: 0.0,
                lastLocation?.longitude ?: 0.0,
                api_key
            )
            weatherForecastStateFlow.emit(response.body())
            // response == Response<WeatherModel>
            // response.body() == WeatherModel
            // response.code()
            // lastLocation
        }
        return weatherForecastStateFlow
    }*/

    //2
    @ExperimentalCoroutinesApi
    val weather: Flow<WeatherModel?> = repository.currentLocationWeather()


    @ExperimentalCoroutinesApi
    val forecast: Flow<WeatherForecastModel?> = repository.weatherForecast()


}
