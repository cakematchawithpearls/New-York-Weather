package com.example.newyorkweather

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val weatherApiService by lazy {
        retrofit.create(OpenWeatherMapService::class.java)
    }

    // PASTE YOUR OPENWEATHERMAP API KEY INSIDE THE QUOTES BELOW
    private val apiKey = "94402460ee77bc6eeb87c7b71026ca04"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cityText: TextView = findViewById(R.id.text_city)
        val mainText: TextView = findViewById(R.id.text_main)
        val descriptionText: TextView = findViewById(R.id.text_description)
        val weatherIcon: ImageView = findViewById(R.id.image_weather_icon)

        weatherApiService.getWeather("New York", apiKey)
            .enqueue(object : Callback<OpenWeatherMapResponseData> {

                override fun onResponse(
                    call: Call<OpenWeatherMapResponseData>,
                    response: Response<OpenWeatherMapResponseData>
                ) {
                    if (response.isSuccessful) {
                        val weatherResponse = response.body()
                        if (weatherResponse != null) {
                            cityText.text = weatherResponse.locationName

                            val currentWeather = weatherResponse.weather.firstOrNull()
                            if (currentWeather != null) {
                                mainText.text = currentWeather.main
                                descriptionText.text = currentWeather.description

                                val iconUrl = "https://openweathermap.org/img/wn/${currentWeather.icon}@2x.png"
                                Glide.with(this@MainActivity)
                                    .load(iconUrl)
                                    .into(weatherIcon)
                            }
                        }
                    } else {
                        val errorMsg = "Server error: ${response.code()} - ${response.errorBody()?.string()}"
                        Log.e("MainActivity", errorMsg)
                        cityText.text = "Error: ${response.code()}"
                        Toast.makeText(this@MainActivity, "Failed to get weather (${response.code()})", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<OpenWeatherMapResponseData>, t: Throwable) {
                    Log.e("MainActivity", "Network failure: ${t.message}", t)
                    cityText.text = "Network Error"
                    descriptionText.text = t.localizedMessage ?: "Check your internet connection"
                    Toast.makeText(this@MainActivity, "Failed to connect to server", Toast.LENGTH_LONG).show()
                }
            })
    }
}