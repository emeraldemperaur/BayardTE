package iot.empiaurhouse.bayardte.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import iot.empiaurhouse.bayardte.model.TEMarker
import iot.empiaurhouse.bayardte.persistence.BayardRepository

class TEMarkerViewModel(app: Application) : AndroidViewModel(app){
    private val bayardRepository = BayardRepository()
    private val teMarkerDisposable = CompositeDisposable()
    val teMarkers = MutableLiveData<List<TEMarker>>()
    val resultError = MutableLiveData<Boolean>()
    val connecting = MutableLiveData<Boolean>()


    fun processTEMarkers(application: Application){
        bayardRepository.BayardRepository(application)
            fetchTEMarkerList()
    }


    fun fetchTEMarkerList(){
        connecting.value = true
        teMarkerDisposable.add(
            bayardRepository.fetchTEMarkersByID().subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<List<TEMarker>>(){
                    override fun onSuccess(reply: List<TEMarker>) {
                        teMarkers.value = reply
                        resultError.value = false
                        connecting.value = false
                        println("\nFound TE Markers DB: \n $reply")

                    }

                    override fun onError(e: Throwable) {
                        resultError.value = true
                        connecting.value = false
                        e.printStackTrace()
                    }

                } )
        )
    }

    fun updateTEMarker(teMarker: TEMarker){
        bayardRepository.editTEMarker(teMarker)
    }

    fun insertTEMarker(teMarker: TEMarker){
        bayardRepository.insertTEMarker(teMarker)
    }

    fun deleteTEMarker(teMarker: TEMarker){
        bayardRepository.deleteTEMarker(teMarker)
    }

    fun killTEMarkerDB(){
        bayardRepository.deleteTEMarkers()
    }


}