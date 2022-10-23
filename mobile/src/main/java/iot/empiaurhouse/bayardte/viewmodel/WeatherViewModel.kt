package iot.empiaurhouse.bayardte.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import iot.empiaurhouse.bayardte.model.APIStatus
import iot.empiaurhouse.bayardte.network.BayardAPIService

class WeatherViewModel(app: Application) : AndroidViewModel(app) {

    private val bayardAPIService = BayardAPIService(app.applicationContext)
    private val weatherDisposable = CompositeDisposable()
    val weatherRecords = MutableLiveData<List<APIStatus>>()
    val weatherError = MutableLiveData<Boolean>()
    val connecting = MutableLiveData<Boolean>()
    private lateinit var lat: String
    private lateinit var long: String

    fun initLocation(latitude: String, longitude: String){
        lat = latitude
        long = longitude
    }

    fun fetchWeatherRecords(){
        connecting.value = true
        weatherDisposable.add(
            bayardAPIService.getChironStatus(lat, long)
                .subscribeOn(io.reactivex.schedulers.Schedulers.newThread())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableSingleObserver<List<APIStatus>>(){
                    override fun onSuccess(reply: List<APIStatus>) {
                        weatherRecords.value = reply
                        weatherError.value = false
                        connecting.value = false
                        // stashRecordsList(reply)
                        println(reply.toString())
                        println("See Weather Records response result: $reply")

                    }

                    override fun onError(e: Throwable) {
                        weatherError.value = true
                        connecting.value = false
                        e.printStackTrace()
                        println("See Weather Records response error: ${e.message}")
                    }

                } )
        )

    }



}