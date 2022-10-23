package iot.empiaurhouse.bayardte.persistence

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import iot.empiaurhouse.bayardte.model.ChromaProfile
import iot.empiaurhouse.bayardte.model.TEMarker
import iot.empiaurhouse.bayardte.utils.subscribeOnBackground
import java.time.LocalDate
import java.time.ZoneId


@Database(entities = [ChromaProfile::class, TEMarker::class], version = 1, exportSchema = false)
abstract class BayardDatabase: RoomDatabase() {

    abstract fun teMarkerDAO(): TEMarkerDAO
    abstract fun chromaProfileDAO(): ChromaProfileDAO

    companion object{
        @Volatile private var dbInstance: BayardDatabase? = null
        private val CODEX = Any()

        operator fun invoke(context: Context)= dbInstance ?: synchronized(CODEX){
            dbInstance ?: buildBayardDatabase(context).also { dbInstance = it}
        }

        private fun buildBayardDatabase(context: Context) = Room.databaseBuilder(context,
            BayardDatabase::class.java, "bayard.db")
            .fallbackToDestructiveMigration()
            .addCallback(bootyCall)
            .build()


        private val bootyCall = object : Callback(){
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                initDB(dbInstance!!)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun initDB(db: BayardDatabase){
            val teMarkerDAO = db.teMarkerDAO()
            val chromaProfileDAO = db.chromaProfileDAO()
            val tz = ZoneId.systemDefault()



            subscribeOnBackground {
                val initTEMarker = TEMarker(alias = "V Island", country = "Nigeria", countryCode = "NGA",
                    province = "Lagos", city = "Lagos", timeZone = "West Africa Standard Time", timeZoneObj = "Europe/Paris",
                    latitude = "6.465422",
                    longitude = "3.406448", hexCode1="#013220", hexCode2="#FFFFFF", hexCode3="#013220",
                    createdOnTimeStamp = LocalDate.now().toString())
                teMarkerDAO.insertTEMarker(initTEMarker)
                println("Initialized initTEMarker DB:\n\t $initTEMarker")
                val initTEMarkerII = TEMarker(alias = "DT VanCity", country = "Canada", countryCode = "CAN",
                    province = "British Columbia", city = "Vancouver", timeZone = "Pacific Standard Time", timeZoneObj = "America/Los_Angeles",
                    latitude = "49.246292", hexCode1="#800020", hexCode2="#FFFFFF", hexCode3="#800020",
                    longitude = "-123.116226", createdOnTimeStamp = LocalDate.now().toString())
                teMarkerDAO.insertTEMarker(initTEMarkerII)
                println("Initialized initTEMarker DB:\n\t $initTEMarkerII")
                val initChromaProfile = ChromaProfile(alias = LocalDate.now().toString(), hexCode1 = "#FFD100", hexCode2 = "#3A243B",
                    hexCode3 = "#EE7600", createdOnTimeStamp = LocalDate.now().toString())
                chromaProfileDAO.insertChromaProfile(initChromaProfile)
                println("Initialized initChromaProfile DB:\n\t $initChromaProfile")
            }

        }


    }


}