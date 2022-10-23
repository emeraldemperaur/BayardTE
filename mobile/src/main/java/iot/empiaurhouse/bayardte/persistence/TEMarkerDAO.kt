package iot.empiaurhouse.bayardte.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Single
import iot.empiaurhouse.bayardte.model.TEMarker


@Dao
interface TEMarkerDAO {
    @Insert
    fun insertTEMarker(teMarker: TEMarker)

    @Update
    fun updateTEMarker(teMarker: TEMarker)

    @Delete
    fun deleteTEMarker(teMarker: TEMarker)

    @Query("delete from te_marker_db")
    fun deleteTEMarkersDB()

    @Query("select * from te_marker_db")
    fun bulkFetchTEMarkers(): Single<List<TEMarker>>

    @Query("select * from te_marker_db order by id desc")
    fun bulkFetchTEMarkersByID(): Single<List<TEMarker>>

    @Query("select * from te_marker_db where id==:id")
    fun fetchTEMarkerByID(id: Int): LiveData<TEMarker>

    @Query("select * from te_marker_db where alias==:markerAlias")
    fun fetchTEMarkerByAlias(markerAlias: String): LiveData<TEMarker>
}