package iot.empiaurhouse.bayardte.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

data class  APIStatus (
    @JsonProperty("latitude")
    @SerializedName("latitude")
    val latitude: Double?,
    @JsonProperty("longitude")
    @SerializedName("longitude")
    val longitude: Double?,
    @JsonProperty("generationtime_ms")
    @SerializedName("generationtime_ms")
    val generationtime_ms: Double?,
    @JsonProperty("utc_offset_seconds")
    @SerializedName("utc_offset_seconds")
    val utc_offset_seconds: Integer?,
    @JsonProperty("timezone")
    @SerializedName("timezone")
    val timezone: String?,
    @JsonProperty("timezone_abbreviation")
    @SerializedName("timezone_abbreviation")
    val timezone_abbreviation: String?,
    @JsonProperty("elevation")
    @SerializedName("elevation")
    val elevation: Double?,
    @JsonProperty("current_weather")
    @SerializedName("current_weather")
    val current_weather: CurrentWeather
        )