package iot.empiaurhouse.bayardte.network

import io.reactivex.Single
import iot.empiaurhouse.bayardte.model.APIStatus
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface BayardAPIEndpoints {

    @Headers("Accept: application/json")
    @GET("https://api.open-meteo.com/v1/forecast?")
    fun getBayardWeather(
                         @Query("latitude") latitude: String?,
                         @Query("longitude") longitude: String?,
                         @Query("current_weather") current_weather: String?): Single<List<APIStatus>>

}