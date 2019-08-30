package com.dac.roombooking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dac.roombooking.data.model.*
import com.dac.roombooking.network.ApiManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class RoomViewModel : ViewModel() {


    val selectTimeChangeLiveData: MutableLiveData<String> =
        MutableLiveData() // live data for select item listen for changes
    var selectedTimes = "" // select times value if empty it means user has not select one yet  if not he select
    private val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.GERMANY)  // date format date and time
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY) // date format date only
    private val apiManager = ApiManager

    private val composite = CompositeDisposable() // disposable container

    private val gson = Gson()

    var date: String? = null
    var room: Room? = null
    lateinit var workspace: WorkSpace


    private val bookRoomResult = MutableLiveData<Boolean>()
    private val showLoading = MutableLiveData<Boolean>()
    private val showMessage = MutableLiveData<String>()
    private val availableTimesLiveData = MutableLiveData<List<String>>() // filter times live data

    /**
     * calls booking api
     * create body object for api to send it as body of request
     * it check all required files
     * */
    fun bookTimes(title: String, description: String, pasesses: List<Participate>) {

        if (date != null && ::workspace.isInitialized && workspace.link != null && room != null && selectedTimes.isNotEmpty()) {
            showLoading.value = true
            val booking = JsonObject()
            booking.addProperty("title", title)
            booking.addProperty("description", description)
            booking.addProperty("room", room!!.name)

            val timeParts = selectedTimes.split("-")

            if (date.equals("now", true) || date.equals("today", true)) {
                booking.addProperty("date", date)
                if (timeParts.size > 1) {
                    val todayDate = dateFormat.format(Date())
                    Timber.v("today date %s", todayDate)
                    booking.addProperty(
                        "time_start",
                        dateTimeFormat.parse("$todayDate ${timeParts[0].trim()}")!!.time
                    )
                    booking.addProperty("time_end", dateTimeFormat.parse("$todayDate ${timeParts[1].trim()}")!!.time)
                }

            } else {
                booking.addProperty("date", dateFormat.parse(date)!!.time)
                booking.addProperty("time_start", dateTimeFormat.parse("$date ${timeParts[0].trim()}")!!.time)
                booking.addProperty("time_end", dateTimeFormat.parse("$date ${timeParts[1].trim()}")!!.time)
            }

            val passesJson = Gson().toJsonTree(pasesses)

            val bodyJson = JsonObject()
            bodyJson.add("booking", booking)
            bodyJson.add("passes", passesJson)

            Timber.v("body json %s", bodyJson)

            composite.add(
                apiManager.getCallsAPI(workspace.link!!).sendpass(bodyJson)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribeWith(object : DisposableSingleObserver<JsonObject>() {
                        override fun onSuccess(t: JsonObject) {
                            // check for return result
                            // if it null it means some thing wrong happen
                            // if it has error it will show error message

                            when {
                                t.has("success") -> saveEventToDataBase()
                                t.has("error") -> {
                                    showLoading.postValue(false)
                                    val error = gson.fromJson(t, ResponseError::class.java)
                                    showMessage.postValue(error.error.text)
                                }
                                else -> {
                                    showLoading.postValue(false)
                                    showMessage.postValue("some thing wrong try later")

                                }
                            }


                        }

                        override fun onError(e: Throwable) {
                            showLoading.postValue(false)
                            showMessage.postValue("some thing go wrong try later")
                            Timber.e(e)

                        }
                    })
            )
        }

    }

    /**
     * save event to database
     * for filter times in next time
     * */
    fun saveEventToDataBase() {
        if (date != null && room != null && selectedTimes.isNotEmpty()) {
            val event = Event()

            event.room = room!!.name
            event.times = selectedTimes
            /**
             * check for time that user select
             * if time is now or today it will convert it to this format dd-MM-yyyy
             * save event will take like of workspace && date && room name && times
             *
             *
             * */
            if (date.equals("now", true) || date.equals("today", true)) {
                event.date = dateFormat.format(Date())
            } else {
                event.date = date
            }
            event.workSpace = workspace.link

            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                it.insertOrUpdate(event)
            }
            realm.close()
            // hide loading
            showLoading.postValue(false)
            bookRoomResult.postValue(true)

        }
    }

    // filter available dates from taken before
    fun filterTimes() {
        if (room != null)
            composite.add(Observable.just(room!!.avail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .map {
                    val realm = Realm.getDefaultInstance()
                    val queryDate = if (date.equals("now", true) || date.equals("today", true)) {
                        dateFormat.format(Date())
                    } else {
                        date!!
                    }
                    val list = arrayListOf<String>()
                    /**
                     *check for all available time if it saved on database or not to prevent conflient
                     * it use workspace LINK && rom name && times && date
                     *
                     * */
                    for (i in it) {
                        val result =
                            realm.where(Event::class.java).equalTo("workSpace", workspace.link).and()
                                .equalTo("room", room!!.name).and().equalTo("times", i).and().equalTo("date", queryDate)
                                .findAll()

                        if (result.isNullOrEmpty()) {
                            list.add(i)
                        }
                    }

                    realm.close()

                    return@map list
                }
                .subscribe {
                    availableTimesLiveData.value = it
                }

            )


    }

    fun getBookRoomResult(): LiveData<Boolean> {
        return bookRoomResult
    }

    fun getShowLoading(): LiveData<Boolean> {
        return showLoading
    }

    fun getMessage(): LiveData<String> {
        return showMessage
    }

    fun getFilterdTimes(): LiveData<List<String>> {
        return availableTimesLiveData
    }

    fun checkForParticipateCapcity(size: Int): Boolean {
        if (room != null)
            return size < room!!.capacity

        showMessage.value = "please check room capacity"
        return false
    }

    override fun onCleared() {
        super.onCleared()
        composite.dispose() // dispose all rxjave observable to prevent memory leak
    }

}