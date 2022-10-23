package iot.empiaurhouse.bayardte.wearutils

import android.os.BatteryManager
import android.os.Build

class SynopsisManager {

    fun getOSVersion(): String{
        println("Detected OS Version: ${Build.VERSION.RELEASE}")
        return Build.VERSION.RELEASE
    }

    fun getOEM():String{
        var oemStr = ""
        if (!Build.MANUFACTURER.isNullOrEmpty()) oemStr = Build.MANUFACTURER
        else if (Build.MANUFACTURER.isNullOrEmpty()) oemStr = Build.BRAND
        println("Detected OEM: $oemStr")
        return oemStr
    }

    fun getModel():String{
        println("Detected MODEL: ${Build.MODEL}")
        return Build.MODEL
    }

    fun getBatteryLevel():String{
        println("Detected BATTERY level: ${BatteryManager.BATTERY_PROPERTY_CAPACITY}")
        return "${BatteryManager.BATTERY_PROPERTY_CAPACITY}%"
    }

    fun isBatteryLow(): Boolean{
        var isLow = false
        if (BatteryManager.BATTERY_PROPERTY_CAPACITY <= 35) isLow = true
        println("Detected BATTERY level low: $isLow")
        return isLow
    }

    fun getChargeStatus():ArrayList<Boolean>{
        var isLow = false
        var isMid = false
        var isHigh = false
        val chargeStatus = arrayListOf<Boolean>()
        if (BatteryManager.BATTERY_PROPERTY_CAPACITY <= 35) isLow = true
        if (BatteryManager.BATTERY_PROPERTY_CAPACITY in 36..69) isMid = true
        if (BatteryManager.BATTERY_PROPERTY_CAPACITY > 70) isHigh = true
        chargeStatus.add(isLow)
        chargeStatus.add(isMid)
        chargeStatus.add(isHigh)
        println("Detected BATTERY level low: $isLow")
        println("Detected BATTERY level mid: $isMid")
        println("Detected BATTERY level high: $isHigh")
        return chargeStatus
    }

}