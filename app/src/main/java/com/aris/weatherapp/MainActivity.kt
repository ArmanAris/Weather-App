package com.aris.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.aris.weatherapp.data.model.current_weather.WeatherModel
import com.aris.weatherapp.data.model.weather_forecast.WeatherForecast
import com.aris.weatherapp.ui.theme.Cloudy
import com.aris.weatherapp.ui.theme.Rainy
import com.aris.weatherapp.ui.theme.Sunny
import com.aris.weatherapp.ui.theme.Yellow
import com.aris.weatherapp.viewmodel.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToInt


@AndroidEntryPoint
@Suppress("OPT_IN_IS_NOT_ENABLED")
class MainActivity : ComponentActivity() {

    private var stateLocation = MutableStateFlow(false)
    private val requestPermission = registerForActivityResult(RequestPermission()) {
        stateLocation.value = it

    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
            requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            val viewModel by viewModels<MainViewModel>()


            val accessLocation by stateLocation.collectAsState()
            if (accessLocation) {

                val weather by viewModel.weather.collectAsState(null)
                val forecast by viewModel.forecast.collectAsState(null)

                if (weather == null && forecast == null) {
                    Column(modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(modifier = Modifier.size(50.dp))
                        Text(text = "در حال دریافت اطلاعات",
                            modifier = Modifier.padding(30.dp),
                            fontSize = 20.sp)
                    }
                }

                if (weather != null && forecast != null) {

                    val gr = forecast!!.list.groupBy {
                        Calendar.getInstance().apply {
                            time = Date(it.dt * 1000)
                        }.get(Calendar.DAY_OF_MONTH)
                    }.filter { (key, value) ->
                        key != today()
                    }

                    View(weather!!, gr)
                }
            } else {
                Column(modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "تا زمانی که اجازه ندهید ...")
                }
            }


            /*
            val systemUiController = rememberSystemUiController()
             weather?.let {
                 systemUiController.setSystemBarsColor(it.backgroundColor())
             }
             */


        }

    }

}

@Composable
fun View(weather: WeatherModel, forecast: Map<Int, List<WeatherForecast>>) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(weather.backgroundColor())) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(top = 15.dp)) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 15.dp), contentAlignment = Alignment.TopCenter
                ) {
                    Box(modifier = Modifier
                        .size(250.dp)
                        .clip(CircleShape)
                        .background(Yellow)
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(top = 25.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(text = weather.main.temp.roundToInt().toString() + "°",
                        fontSize = 48.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold)
                    Text(text = weather.weather.first().main,
                        fontSize = 35.sp,
                        color = Color.White)
                    Text(text = weather.name,
                        fontSize = 20.sp,
                        color = Color.White, maxLines = 1)
                    Image(painter = painterResource(id = iconImage(weather.weather.first().main)),
                        contentDescription = "")

                }
            }
        }
        Temp(weather = weather)
        Divider(color = Color.White)
        ForecastWeather(forecast = forecast)

    }

}

@Composable
fun Temp(weather: WeatherModel) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "${weather.main.temp_min.roundToInt()}°",
                color = Color.White,
                fontSize = 20.sp)
            Text(text = "Min", color = Color.White, fontSize = 20.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "${weather.main.temp.roundToInt()}°",
                color = Color.White,
                fontSize = 20.sp)
            Text(text = "Current", color = Color.White, fontSize = 20.sp)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "${weather.main.temp_max.roundToInt()}°",
                color = Color.White,
                fontSize = 20.sp)
            Text(text = "Max", color = Color.White, fontSize = 20.sp)
        }
    }
}

@Composable
fun ForecastWeather(forecast: Map<Int, List<WeatherForecast>>) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        forecast.forEach { (key, value) ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier
                    .weight(0.3f),
                    horizontalAlignment = Alignment.Start) {
                    //left
                    if (key == tomorrow()) {
                        Text(text = "Tomorrow",
                            fontSize = 20.sp,
                            color = Color.White)
                    } else {
                        Text(text = Date(value[0].dt * 1000).toString().split(" ")[0],
                            fontSize = 20.sp,
                            color = Color.White)
                    }
                }
                Column(modifier = Modifier
                    .weight(0.4f),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    //center
                    Image(painter = painterResource(id = iconImage(value[0].weather.first().main)),
                        contentDescription = "",
                        modifier = Modifier.size(55.dp))
                }
                Column(modifier = Modifier
                    .weight(0.3f),
                    horizontalAlignment = Alignment.End) {
                    //right
                    Row() {
                        val listMax = mutableListOf<Int>()
                        val listMin = mutableListOf<Int>()
                        value.forEach {
                            listMax.add(it.main.temp_max.roundToInt())
                            listMin.add(it.main.temp_min.roundToInt())
                        }
                        Text(text = listMin.minOf { it }.toString() + "°",
                            fontSize = 20.sp,
                            color = Color.White)
                        Text(text = "/",
                            fontSize = 20.sp,
                            color = Color.White)
                        Text(text = listMax.maxOf { it }.toString() + "°",
                            fontSize = 20.sp,
                            color = Color.White)
                    }
                }
            }
        }
    }
}

fun WeatherModel.background(): Int {
    val conditions = weather.first().main
    return when {
        conditions.contains("cloud", ignoreCase = true) -> R.drawable.cloudy
        conditions.contains("rain", ignoreCase = true) -> R.drawable.rain
        conditions.contains("snow", ignoreCase = true) -> R.drawable.snow
        conditions.contains("clear", ignoreCase = true) -> R.drawable.sunny
        else -> R.drawable.ic_launcher_background
    }
}

fun WeatherModel.backgroundColor(): Color {
    val conditions = weather.first().main
    return when {
        conditions.contains("cloud", ignoreCase = true) -> Cloudy
        conditions.contains("rain", ignoreCase = true) -> Rainy
        conditions.contains("snow", ignoreCase = true) -> Cloudy
        else -> Sunny
    }
}

fun iconImage(name: String): Int {
    return when {
        name.contains("cloud", ignoreCase = true) -> R.drawable.icon_cloudy
        name.contains("rain", ignoreCase = true) -> R.drawable.icon_rain
        name.contains("snow", ignoreCase = true) -> R.drawable.icon_snow
        name.contains("clear", ignoreCase = true) -> R.drawable.icon_sun
        else -> R.drawable.ic_launcher_background
    }
}

fun today(): Int {
    val today = Date().toString().groupBy {
        Calendar.getInstance().apply {}.get(Calendar.DAY_OF_MONTH)
    }.keys.first()
    return today
}

fun tomorrow(): Int {
    val today = Date().toString().groupBy {
        Calendar.getInstance().apply {}.get(Calendar.DAY_OF_MONTH)
    }.keys.first()
    return today + 1
}



