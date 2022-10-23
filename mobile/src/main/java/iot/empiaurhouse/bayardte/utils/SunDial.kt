package iot.empiaurhouse.bayardte.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SunDial {

    public fun fetchGreeting(): String{
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        var greeting = ""
        println("\t\t OS Time >> $day/$month/$year -- $hour:$minute")
        if (month == 12 && day == 25){
            greeting = "Merry Christmas"
        }
        if (hour in 0..4){
            greeting = "Good Evening"
        }
        if (hour in 5..7){
            greeting = "Dusk"
        }
        if (hour in 8..11){
            greeting = "Good Morning"
        }
        if (hour in 12..17) {
            greeting = "Good Afternoon"
        }
        if (hour in 18..24) {
            greeting = "Good Evening"
        }

        return greeting
    }

     fun fetchFullTime(): String {
        val localDateTime = LocalDateTime.now()

        return localDateTime.format(DateTimeFormatter.ofPattern("h:m a"))
    }

    fun fetchCopyrightYear(): String {
        val localDateTime = LocalDateTime.now()
        val yearTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy"))
        return "©$yearTime M.E."
    }

     fun fetchShortTime(): String {
        val localDateTime = LocalDateTime.now()

        return localDateTime.format(DateTimeFormatter.ofPattern("hh:mm"))
    }

     fun fetchTimePeriod(): String {
        val localDateTime = LocalDateTime.now()

        return localDateTime.format(DateTimeFormatter.ofPattern("a"))
    }

     fun fetchTimeZone(): String {
        val localTimeZone = TimeZone.getDefault()
        return localTimeZone.displayName
    }

     fun fetchTimeZoneObj(): TimeZone {
        return TimeZone.getDefault()
    }

     fun fetchCurrentTimeByZone(timeZone: String): String{
         //val timeZone = TimeZone.getDefault().displayName
         //var tzID = ZoneId.of(timeZone)
         //if (ZoneId.of(timeZone) != null) tzID = ZoneId.of(timeZone)
         //if (tzID == null) tzID = ZoneId.systemDefault()
         //val tz = ZoneId.systemDefault()
         val tzMore = LocalDateTime.now()

         println("Time Zone:\n $timeZone")
         println("System Time Zone Found:\n $tzMore")
         println("Time Zone IDs:\n $tzMore")


         //val offSet = ZoneOffset.of("+2:00")
        //val timeUtc = LocalDateTime.now().atOffset(ZoneOffset.UTC)
        //val zoneTime = timeUtc.withOffsetSameInstant(offSet)
        return tzMore.format(DateTimeFormatter.ofPattern("hh:mm a"))
    }


}