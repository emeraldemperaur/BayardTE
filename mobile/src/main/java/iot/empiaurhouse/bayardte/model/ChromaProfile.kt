package iot.empiaurhouse.bayardte.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "chroma_profile_db")
data class ChromaProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo(name = "alias")
    val alias: String,
    @ColumnInfo(name = "hexCode1")
    val hexCode1: String,
    @ColumnInfo(name = "hexCode2")
    val hexCode2: String,
    @ColumnInfo(name = "hexCode3")
    val hexCode3: String,
    @ColumnInfo(name = "createdOnTimeStamp")
    val createdOnTimeStamp: String
)
