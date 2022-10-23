package iot.empiaurhouse.bayardte.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import io.reactivex.Single
import iot.empiaurhouse.bayardte.model.ChromaProfile


@Dao
interface ChromaProfileDAO {
    @Insert
    fun insertChromaProfile(chromaProfile: ChromaProfile)

    @Update
    fun updateChromaProfile(chromaProfile: ChromaProfile)

    @Delete
    fun deleteChromaProfile(chromaProfile: ChromaProfile)

    @Query("delete from chroma_profile_db")
    fun deleteChromaProfilesDB()

    @Query("select * from chroma_profile_db")
    fun bulkFetchChromaProfiles(): Single<List<ChromaProfile>>

    @Query("select * from chroma_profile_db order by id desc")
    fun bulkFetchChromaProfileByID(): Single<List<ChromaProfile>>

    @Query("select * from chroma_profile_db where id==:id")
    fun fetchChromaProfileByID(id: Int): LiveData<ChromaProfile>

    @Query("select * from chroma_profile_db where alias==:chromaAlias")
    fun fetchChromaProfileByAlias(chromaAlias: String): LiveData<ChromaProfile>
}