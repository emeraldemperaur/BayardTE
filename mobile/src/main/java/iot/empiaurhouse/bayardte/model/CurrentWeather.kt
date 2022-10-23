package iot.empiaurhouse.bayardte.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

data class CurrentWeather(
    @JsonProperty("temperature")
    @SerializedName("temperature")
    val temperature: Double?,
    @JsonProperty("windspeed")
    @SerializedName("windspeed")
    val windspeed: Double?,
    @JsonProperty("winddirection")
    @SerializedName("winddirection")
    val winddirection: Double?,
    @JsonProperty("weathercode")
    @SerializedName("weathercode")
    val weathercode: Double?,
    @JsonProperty("time")
    @SerializedName("time")
    val time: String?
)
