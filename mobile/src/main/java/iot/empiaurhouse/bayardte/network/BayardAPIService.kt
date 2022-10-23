package iot.empiaurhouse.bayardte.network

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Single
import iot.empiaurhouse.bayardte.model.APIStatus
import iot.empiaurhouse.bayardte.utils.JSONHeaderInterceptor
import iot.empiaurhouse.bayardte.utils.UserPreferenceManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

class BayardAPIService(val context: Context) {
    private var serverUrl: String = "https://api.open-meteo.com"
    private lateinit var userManager: UserPreferenceManager
    private val client = OkHttpClient.Builder().build()
    private var online: Boolean = false

    init {

    }

    var gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val bayardAPI = Retrofit.Builder()
        .baseUrl(serverUrl)
        .addConverterFactory(JacksonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(provideHttpClient())
        .build()
        .create(BayardAPIEndpoints::class.java)


    private fun provideHttpClient(): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(JSONHeaderInterceptor())
        return okHttpClientBuilder.build()
    }

    fun getChironStatus(latitude: String, longitude: String): Single<List<APIStatus>> {
        return bayardAPI.getBayardWeather(latitude, longitude, "true")
    }

}