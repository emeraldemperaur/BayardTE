package iot.empiaurhouse.bayardte.persistence

import android.app.Application
import io.reactivex.Single
import iot.empiaurhouse.bayardte.model.ChromaProfile
import iot.empiaurhouse.bayardte.model.TEMarker

class BayardRepository {
    private lateinit var chromaProfileDAO: ChromaProfileDAO
    private lateinit var teMarkerDAO: TEMarkerDAO

    fun BayardRepository(application: Application) {
        val bayardDatabase = BayardDatabase(application.baseContext)
        chromaProfileDAO = bayardDatabase.chromaProfileDAO()
        teMarkerDAO = bayardDatabase.teMarkerDAO()

    }

    fun fetchChromaProfiles(): Single<List<ChromaProfile>> {
        return chromaProfileDAO.bulkFetchChromaProfiles()
    }

    fun fetchChromaProfilesByID(): Single<List<ChromaProfile>> {
        return chromaProfileDAO.bulkFetchChromaProfileByID()
    }

    fun fetchTEMarkers(): Single<List<TEMarker>> {
        return teMarkerDAO.bulkFetchTEMarkers()
    }

    fun fetchTEMarkersByID(): Single<List<TEMarker>> {
        return teMarkerDAO.bulkFetchTEMarkersByID()
    }

    fun insertTEMarker(teMarker: TEMarker): TEMarker{
        teMarkerDAO.insertTEMarker(teMarker)
        return teMarker
    }

    fun editTEMarker(teMarker: TEMarker): TEMarker{
        teMarkerDAO.updateTEMarker(teMarker)
        return teMarker
    }

    fun deleteTEMarkers(){
        teMarkerDAO.deleteTEMarkersDB()
    }

    fun deleteTEMarker(teMarker: TEMarker) {
        teMarkerDAO.deleteTEMarker(teMarker)
    }

    fun insertChromaProfile(chromaProfile: ChromaProfile): ChromaProfile{
        chromaProfileDAO.insertChromaProfile(chromaProfile)
        return chromaProfile
    }

    fun editChromaProfile(chromaProfile: ChromaProfile): ChromaProfile{
        chromaProfileDAO.updateChromaProfile(chromaProfile)
        return chromaProfile
    }

    fun deleteChromaProfiles(){
        chromaProfileDAO.deleteChromaProfilesDB()
    }

    fun deleteChromaProfile(chromaProfile: ChromaProfile) {
        chromaProfileDAO.deleteChromaProfile(chromaProfile)
    }

}