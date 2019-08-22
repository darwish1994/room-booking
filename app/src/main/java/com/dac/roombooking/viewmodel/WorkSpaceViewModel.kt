package com.dac.roombooking.viewmodel

import androidx.lifecycle.ViewModel
import com.dac.roombooking.model.Room
import com.dac.roombooking.rebo.RoomRebo
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WorkSpaceViewModel : ViewModel() {
    private val repo = RoomRebo() // room repo for all api calls and result
    private val compositdissposable = CompositeDisposable() // container for rx observable dispose

    val roomResult = repo.roomLiveData // listener for result of api calls and update filters
    val errorLiveData = repo.responseErrorlive
    /**
     * call get rooms api
     * url for different workspaces
     * date for request body which like {"date" : "unix timestamp| now | today"}
     **/
    fun getRooms(url: String, date: JsonObject) {
        compositdissposable.add(repo.getRooms(url, date))
    }


    /**
     * take query and check if room name contains this query
     * it also check if user activate available check box or not
     *
     * */
    fun searchFilter(query: String, rooms: List<Room>, availble: Boolean) {
        compositdissposable.addAll(
            Observable.just(rooms)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .map {
                    val list = arrayListOf<Room>()
                    for (i in it) {
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
                    roomResult.value = it
                }
        )

    }

    /**
     * filter current rooms with available times
     *
     * */
    fun filterAvilable(rooms: List<Room>) {
        compositdissposable.addAll(
            Observable.just(rooms)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .map {
                    val list = arrayListOf<Room>()
                    for (i in it) {
                        if (i.avail.isNotEmpty()) {
                            list.add(i)
                        }

                    }
                    return@map list
                }
                .subscribe {
                    roomResult.value = it
                }
        )

    }


    override fun onCleared() {
        super.onCleared()
        compositdissposable.dispose() // dispose all rxjava observable
    }

}