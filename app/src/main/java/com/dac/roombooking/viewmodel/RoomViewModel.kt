package com.dac.roombooking.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dac.roombooking.model.Event
import com.dac.roombooking.model.Passes
import com.dac.roombooking.model.Room
import com.dac.roombooking.rebo.RoomRebo
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class RoomViewModel : ViewModel() {

    var selectedTimes = ""
    val mapChanges: MutableLiveData<String> = MutableLiveData()
    val dateTimeformate = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.GERMANY)
    val dateformate = SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY)


    private val composite = CompositeDisposable()
    private val roomrepo = RoomRebo()
    var date: String? = null
    var url: String? = null
    var room: Room? = null


    val bookRoomLiveData = roomrepo.sendpassLiveData
    val saveEvent = MutableLiveData<Boolean>()
    val timesLiveData = MutableLiveData<List<String>>()


    fun bookTimes(title: String, description: String, pasess: List<Passes>): Boolean {

        if (date != null && url != null && room != null && selectedTimes.isNotEmpty()) {
            val booking = JsonObject()
            booking.addProperty("title", title)
            booking.addProperty("description", description)
            booking.addProperty("room", room!!.name)

            val timepartes = selectedTimes.split("-")

            if (date.equals("now", true) || date.equals("today", true)) {
                booking.addProperty("date", date)
                if (timepartes.size > 1) {
                    val todayDate = dateformate.format(Date())
                    Timber.v("today date %s", todayDate)
                    booking.addProperty(
                        "time_start",
                        dateTimeformate.parse("$todayDate ${timepartes[0].trim()}")!!.time
                    )
                    booking.addProperty("time_end", dateTimeformate.parse("$todayDate ${timepartes[1].trim()}")!!.time)
                }


            } else {
                booking.addProperty("title", dateformate.parse(date)!!.time)
                booking.addProperty("time_start", dateTimeformate.parse("$date ${timepartes[0].trim()}")!!.time)
                booking.addProperty("time_end", dateTimeformate.parse("$date ${timepartes[1].trim()}")!!.time)
            }

            val passesJson = Gson().toJsonTree(pasess)

            val bodyJson = JsonObject()
            bodyJson.add("booking", booking)
            bodyJson.add("passes", passesJson)

            Timber.v("body json %s", bodyJson)

            composite.add(roomrepo.sendpass(url!!, bodyJson))
            return true

        } else {
            return false
        }


    }


    fun saveEventtoDb() {
        if (date != null && room != null && selectedTimes.isNotEmpty()) {
            val event = Event()
            event.room = room!!.name
            event.times = selectedTimes
            if (date.equals("now", true) || date.equals("today", true)) {
                event.date = dateformate.format(Date())
            } else {
                event.date = date
            }

            val realm = Realm.getDefaultInstance()
            realm.executeTransactionAsync({
                it.insertOrUpdate(event)
            }, {
                saveEvent.value = true
            }, {
                Timber.e(it)
                saveEvent.value = false
            })


        }
    }

    fun filterTimes(times: List<String>) {
        composite.add(Observable.just(times)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .map {
                val realm = Realm.getDefaultInstance()
                var queryDate = if (date.equals("now", true) || date.equals("today", true)) {
                    dateformate.format(Date())
                } else {
                    date!!
                }
                val list = arrayListOf<String>()

                for (i in it) {
                    val result =
                        realm.where(Event::class.java).equalTo("room", room!!.name).and().equalTo("times", i).and()
                            .equalTo("date", queryDate).findAll()

                    if (result.isNullOrEmpty()) {
                        list.add(i)
                    }
                }

                return@map list
            }
            .subscribe {
                timesLiveData.value = it
            }

        )


    }

    override fun onCleared() {
        super.onCleared()
        composite.dispose()
    }

}