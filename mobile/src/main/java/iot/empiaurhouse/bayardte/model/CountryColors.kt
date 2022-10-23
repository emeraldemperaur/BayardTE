package iot.empiaurhouse.bayardte.model

import com.google.gson.annotations.SerializedName

data class CountryColors(
    @SerializedName("name")
    val name: String?,
    @SerializedName("colors")
    val colors: ArrayList<String>?
)
