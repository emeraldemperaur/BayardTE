package iot.empiaurhouse.bayardte.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "te_marker_db")
data class TEMarker(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "alias")
    val alias: String,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "countryCode")
    val countryCode: String,
    @ColumnInfo(name = "province")
    val province: String,
    @ColumnInfo(name = "city")
    val city: String,
    @ColumnInfo(name = "timeZone")
    val timeZone: String,
    @ColumnInfo(name = "timeZoneObj")
    val timeZoneObj: String? = null,
    @ColumnInfo(name = "latitude")
    val latitude: String,
    @ColumnInfo(name = "longitude")
    val longitude: String,
    @ColumnInfo(name = "hexCode1")
    val hexCode1: String? = null,
    @ColumnInfo(name = "hexCode2")
    val hexCode2: String? = null,
    @ColumnInfo(name = "hexCode3")
    val hexCode3: String? = null,
    @ColumnInfo(name = "createdOnTimeStamp")
    val createdOnTimeStamp: String
)
