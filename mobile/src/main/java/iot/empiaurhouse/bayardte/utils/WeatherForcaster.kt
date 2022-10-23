package iot.empiaurhouse.bayardte.utils

import android.content.Context
import com.beust.klaxon.Klaxon
import iot.empiaurhouse.bayardte.model.APIStatus
import okhttp3.*
import java.io.IOException

class WeatherForcaster(private val latitude: String, private val longitude: String, context: Context) {
    private var url: String = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current_weather=true"
    private var temp: String = "--"
    private var userManager: UserPreferenceManager = UserPreferenceManager(context)


    fun processWeather(): String{
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        println(Thread.currentThread())
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    System.err.println("Response not successful")
                    return
                }
                val json = response.body!!.string()
                val myData = Klaxon().parse<APIStatus>(json)
                temp = myData!!.current_weather.temperature!!.toInt().toString()
                userManager.storeBayardWeatherData("$temp°C")
                println("Weather JSON Data:\n  $myData")
                println("Temp Found:\n $temp")
                println(Thread.currentThread())            }

        })
        // Shutdown the executor as soon as the request is handled
        client.dispatcher.executorService.shutdown()
        return userManager.getBayardWeatherData().toString()
    }

}