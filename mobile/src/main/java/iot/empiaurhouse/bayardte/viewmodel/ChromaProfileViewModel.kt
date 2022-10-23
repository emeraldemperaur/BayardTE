package iot.empiaurhouse.bayardte.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import iot.empiaurhouse.bayardte.model.ChromaProfile
import iot.empiaurhouse.bayardte.persistence.BayardRepository

class ChromaProfileViewModel(app: Application) : AndroidViewModel(app) {
    private val bayardRepository = BayardRepository()
    private val chromaProfileDisposable = CompositeDisposable()
    val chromaProfiles = MutableLiveData<List<ChromaProfile>>()
    val resultError = MutableLiveData<Boolean>()
    val connecting = MutableLiveData<Boolean>()


    fun processChromaProfiles(application: Application){
        bayardRepository.BayardRepository(application)
        fetchChromaProfileList()
    }

    fun fetchChromaProfileList(){
        connecting.value = true
        chromaProfileDisposable.add(
            bayardRepository.fetchChromaProfilesByID().subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<List<ChromaProfile>>(){
                    override fun onSuccess(reply: List<ChromaProfile>) {
                        chromaProfiles.value = reply
                        resultError.value = false
                        connecting.value = false
                        println("\nFound Chroma Profile DB: \n $reply")

                    }

                    override fun onError(e: Throwable) {
                        resultError.value = true
                        connecting.value = false
                        e.printStackTrace()
                    }

                } )
        )
    }


    fun updateChromaProfile(chromaProfile: ChromaProfile){
        bayardRepository.editChromaProfile(chromaProfile)
    }

    fun insertChromaProfile(chromaProfile: ChromaProfile){
        bayardRepository.insertChromaProfile(chromaProfile)
    }

    fun deleteChromaProfile(chromaProfile: ChromaProfile){
        bayardRepository.deleteChromaProfile(chromaProfile)
    }

    fun killChromaProfileDB(){
        bayardRepository.deleteChromaProfiles()
    }


}