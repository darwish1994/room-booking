package com.dac.roombooking.rebo

import androidx.lifecycle.MutableLiveData
import com.dac.roombooking.data.model.ResponseError
import com.dac.roombooking.data.model.Room
import com.dac.roombooking.network.ApiManager
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class RoomRebo {
    private val apiManager = ApiManager
    val roomLiveData = MutableLiveData<List<Room>>()
    val responseErrorlive = MutableLiveData<ResponseError>()
    val sendpassLiveData = MutableLiveData<JsonObject>()
    private val gson = Gson()
    val groupListType = object : TypeToken<List<Room>>() {
    }.type


    fun getRooms(url: String, json: JsonObject): Disposable {
        return apiManager.getCallsAPI(url).getRooms(json)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .subscribeWith(object : DisposableSingleObserver<JsonElement>() {
                override fun onSuccess(t: JsonElement) {
                    when (t) {
                        is JsonObject -> {
                            roomLiveData.postValue(null)
                            val c = gson.fromJson(t, ResponseError::class.java)
                            responseErrorlive.postValue(c)
                        }

                        is JsonArray -> {
                            val c = gson.fromJson<List<Room>>(t, groupListType)
                            roomLiveData.postValue(c)
                        }

                    }


                }

                override fun onError(e: Throwable) {
                    roomLiveData.postValue(null)
                    Timber.e(e)

                }
            })

    }

    fun sendpass(url: String, json: JsonObject): Disposable {
        return apiManager.getCallsAPI(url).sendpass(json)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .subscribeWith(object : DisposableSingleObserver<JsonObject>() {
                override fun onSuccess(t: JsonObject) {
                    sendpassLiveData.postValue(t)
                }

                override fun onError(e: Throwable) {
                    sendpassLiveData.postValue(null)
                    Timber.e(e)

                }
            })

    }


}