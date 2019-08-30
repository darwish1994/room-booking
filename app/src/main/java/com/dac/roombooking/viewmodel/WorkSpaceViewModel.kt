package com.dac.roombooking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dac.roombooking.data.model.Event
import com.dac.roombooking.data.model.ResponseError
import com.dac.roombooking.data.model.Room
import com.dac.roombooking.data.model.WorkSpace
import com.dac.roombooking.network.ApiManager
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class WorkSpaceViewModel : ViewModel() {
    private val apiManager = ApiManager
    private val gson = Gson()
    val groupListType = object : TypeToken<List<Room>>() {
    }.type
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY)


    private val compositeDisposable = CompositeDisposable() // container for rx observable dispose
    lateinit var workSpace: WorkSpace
    private val roomLiveData = MutableLiveData<List<Room>>()
    private val showLoading: MutableLiveData<Boolean> = MutableLiveData()
    private val responseErrorLiveData = MutableLiveData<ResponseError>()


    /**
     * fun set api request with new date
     * this used if date is string
     * create request body for api calls
     *
     * */
    fun loadingRooms(date: String) {
        val jsonDate = JsonObject()

        if (date.equals("now", true) || date.equals("today", true)) {
            jsonDate.addProperty("date", date)
        } else {
            jsonDate.addProperty("date", dateFormat.parse(date)!!.time)
        }

        showLoading.value = true
        Timber.v(jsonDate.toString())
        getRooms(jsonDate, date)
    }


    /**
     * call get rooms api
     * url for different workspaces
     * date for request body which like {"date" : "unix timestamp| now | today"}
     **/
    fun getRooms(json: JsonObject, date: String) {
        if (::workSpace.isInitialized)
            compositeDisposable.add(
                apiManager.getCallsAPI(workSpace.link!!).getRooms(json)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribeWith(object : DisposableSingleObserver<JsonElement>() {
                        override fun onSuccess(t: JsonElement) {
                            showLoading.postValue(false)
                            when (t) {
                                is JsonObject -> {
                                    roomLiveData.postValue(null)
                                    val c = gson.fromJson(t, ResponseError::class.java)
                                    responseErrorLiveData.postValue(c)
                                }

                                is JsonArray -> {

                                    val queryDate = if (date.equals("now", true) || date.equals("today", true)) {
                                        dateFormat.format(Date())
                                    } else {
                                        date
                                    }

                                    /**
                                     * filter result to show available times
                                     * check if this times has been chose before or not
                                     * return new result
                                     *
                                     * */
                                    val c = gson.fromJson<List<Room>>(t, groupListType)

                                    val realm = Realm.getDefaultInstance()
                                    for (room in c) {
                                        val available = arrayListOf<String>()
                                        for (avTime in room.avail) {
                                            val result = realm.where(Event::class.java)
                                                .equalTo("workSpace", workSpace.link).and()
                                                .equalTo("room", room.name).and()
                                                .equalTo("times", avTime).and()
                                                .equalTo("date", queryDate)
                                                .findAll()
                                            if (result.isEmpty()) {
                                                available.add(avTime)
                                            }

                                        }
                                        room.avail = available
                                    }

                                    realm.close()

                                    /***
                                     * close realm instance
                                     * update view with new data
                                     * hide loading
                                     *
                                     *
                                     * */

                                    roomLiveData.postValue(c)
                                    showLoading.postValue(false)

                                }

                            }


                        }

                        override fun onError(e: Throwable) {
                            /**
                             * hide loading
                             * and show message for internal error
                             * */
                            showLoading.postValue(false)
                            roomLiveData.postValue(null)
                            Timber.e(e)

                        }
                    })
            )
    }


    /**
     * take query and check if room name contains this query
     * it also check if user activate available check box or not
     *
     * */
    fun searchFilter(query: String, rooms: List<Room>, availble: Boolean) {
        compositeDisposable.addAll(
            Observable.just(rooms)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .map {
                    val list = arrayListOf<Room>()
                    for (i in it) {
                        /**
                         * check if name of room contains query or not and chick if avail check box is checked to check if it has available times
                         *
                         * */

                        if (i.name.contains(query, true)) {
                            if (availble && i.avail.isNotEmpty()) {
                                list.add(i)

                            } else if (!availble) {
                                list.add(i)
                            }
                        }
                    }
                    return@map list
                }
                .subscribe {
                    roomLiveData.value = it
                }
        )

    }

    /**
     * filter current rooms with available times
     *
     * */
    fun filterAvilable(rooms: List<Room>) {
        compositeDisposable.addAll(
            Observable.just(rooms)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .map {
                    val list = arrayListOf<Room>()

                    /**
                     * check for room with avail list is not empty
                     * */
                    for (i in it) {
                        if (i.avail.isNotEmpty()) {
                            list.add(i)
                        }

                    }
                    return@map list
                }
                .subscribe {
                    roomLiveData.value = it
                }
        )

    }

    fun getRoomLiveData(): LiveData<List<Room>> {
        return roomLiveData
    }

    fun getShowLoadingLiveData(): LiveData<Boolean> {
        return showLoading
    }

    fun getErrorResponseLiveData(): LiveData<ResponseError> {
        return responseErrorLiveData
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose() // dispose all rxjava observable
    }

}