package com.dac.roombooking.rebo

import androidx.lifecycle.MutableLiveData
import com.dac.roombooking.model.Room
import com.dac.roombooking.network.ApiManager
import com.google.gson.JsonObject
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RoomRebo {
    private val apiManager = ApiManager
    val roomLiveData = MutableLiveData<List<Room>>()
    fun getRooms(url: String, json: JsonObject): Disposable {
        return apiManager.getCallsAPI(url).getRooms(json)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .subscribeWith(object : DisposableSingleObserver<List<Room>>() {
                override fun onSuccess(t: List<Room>) {
                    roomLiveData.postValue(t)
                }

                override fun onError(e: Throwable) {
                    roomLiveData.postValue(null)
                    Timber.e(e)

                }
            })

    }


}