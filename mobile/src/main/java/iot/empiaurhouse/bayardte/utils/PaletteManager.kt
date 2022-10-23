package iot.empiaurhouse.bayardte.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import iot.empiaurhouse.bayardte.model.CountryColors
import java.io.IOException

class PaletteManager(pmContext: Context) {

    private lateinit var hexCode1: String
    private lateinit var hexCode2: String
    private lateinit var hexCode3: String
    private var hexJSON: String?
    private var hexCodes = arrayListOf<String>()
    private var filteredHexCodes = arrayListOf<String>()

    init {
        hexJSON = getJsonDataFromAsset(pmContext)
        println("HEX CODES JSON:\n $hexJSON")
    }

    private fun getJsonDataFromAsset(context: Context): String? {
        try {
            hexJSON = context.assets.open("countrycolors.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return hexJSON
    }

    fun initCountryPalette(country: String): ArrayList<String>{
        hexCode1 = "#FFFFFF"
        hexCode2 = "#FFFFFF"
        hexCode3 = "#000000"

        val gson = Gson()
        val listHexCodeType = object : TypeToken<List<CountryColors>>() {}.type
        val countryHexCodes: List<CountryColors> = gson.fromJson(hexJSON, listHexCodeType)
        for (hexCode in countryHexCodes){
            if (hexCode.name!!.lowercase() == country.lowercase()){
                hexCodes = hexCode.colors!!
            }
        }
        if (hexCodes.size < 2){
            hexCode1 = hexCodes[0]
            hexCode2 = "#FFFFFFF"
            hexCode3 = "#000000"
        }
        if (hexCodes.size == 2){
            hexCode1 = hexCodes[0]
            hexCode2 = hexCodes[1]
            hexCode3 = "#000000"
        }
        if (hexCodes.size >= 3){
            hexCode1 = hexCodes[0]
            hexCode2 = hexCodes[1]
            hexCode3 = hexCodes[2]
        }
        filteredHexCodes.add(hexCode1)
        filteredHexCodes.add(hexCode2)
        filteredHexCodes.add(hexCode3)

        return filteredHexCodes
    }



}