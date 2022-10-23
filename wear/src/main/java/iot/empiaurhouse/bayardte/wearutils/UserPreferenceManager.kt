package iot.empiaurhouse.bayardte.wearutils

import android.content.Context
import android.content.SharedPreferences

class UserPreferenceManager(context: Context) {
    private var userPreferences: SharedPreferences = context.getSharedPreferences("bte_user_preferences", 0)
    var preferencesEditor: SharedPreferences.Editor = userPreferences.edit()

    fun storeWearInfoData(uid: String, oem: String? = null, model: String? = null,
                      osv: String? = null, level: Int? = null){
        preferencesEditor.putString("WEAR_UID_KEY", uid)
        preferencesEditor.putString("WEAR_OEM_KEY", oem)
        preferencesEditor.putString("WEAR_MODEL_KEY", model)
        preferencesEditor.putString("WEAR_OSV_KEY", osv)
        preferencesEditor.putInt("WEAR_CHARGE_KEY", 0)
        preferencesEditor.apply()
        preferencesEditor.commit()
    }

    fun storeChromaData(hexCode1: String, hexCode2: String? = null, hexCode3: String? = null){
        preferencesEditor.putString("HEX_CODE1_KEY", hexCode1)
        preferencesEditor.putString("HEX_CODE2_KEY", hexCode2)
        preferencesEditor.putString("HEX_CODE3_KEY", hexCode3)
        preferencesEditor.apply()
        preferencesEditor.commit()
    }

    fun storeChromaProfile(profile: String?){
        preferencesEditor.putString("PROFILE_INFO", profile)
        preferencesEditor.apply()
        preferencesEditor.commit()
    }

    fun storeRing1VelocityData(r1: Int){
        preferencesEditor.putInt("R1_VELOCITY_KEY", r1)
        preferencesEditor.apply()
        preferencesEditor.commit()
    }

    fun storeRing2VelocityData(r2: Int){
        preferencesEditor.putInt("R2_VELOCITY_KEY", r2)
        preferencesEditor.apply()
        preferencesEditor.commit()
    }

    fun storeRing3VelocityData(r3: Int){
        preferencesEditor.putInt("R3_VELOCITY_KEY", r3)
        preferencesEditor.apply()
        preferencesEditor.commit()
    }

    fun storeBayardWeatherData(weather: String){
        preferencesEditor.putString("BTE_WEATHER", weather)
        preferencesEditor.apply()
        preferencesEditor.commit()
    }

    fun getWID(): String? {
        return userPreferences.getString("WEAR_UID_KEY", null)
    }

    fun getOEM(): String? {
        return userPreferences.getString("WEAR_OEM_KEY", null)
    }

    fun getModel(): String? {
        return userPreferences.getString("WEAR_MODEL_KEY", null)
    }

    fun getOS(): String? {
        return userPreferences.getString("WEAR_OSV_KEY", null)
    }

    fun getLevel(): String? {
        return userPreferences.getString("WEAR_OSV_KEY", null)
    }

    fun getHexCode1(): String? {
        return userPreferences.getString("HEX_CODE1_KEY", "#000000")
    }

    fun getHexCode2(): String? {
        return userPreferences.getString("HEX_CODE2_KEY", "#FFFFFF")
    }

    fun getHexCode3(): String? {
        return userPreferences.getString("HEX_CODE3_KEY", "#000000")
    }

    fun getBayardWeatherData(): String? {
        return userPreferences.getString("BTE_WEATHER", "--°C")
    }

    fun getR1Velocity(): Int {
        return userPreferences.getInt("R1_VELOCITY_KEY", 0)
    }

    fun getR2Velocity(): Int {
        return userPreferences.getInt("R2_VELOCITY_KEY", 0)
    }

    fun getR3Velocity(): Int {
        return userPreferences.getInt("R3_VELOCITY_KEY", 0)
    }

    fun getProfileInfo():String? {
        return userPreferences.getString("PROFILE_INFO", "Monochrome")
    }

    fun clearUserData(){
        preferencesEditor.clear()
        preferencesEditor.apply()
        preferencesEditor.commit()
    }

}