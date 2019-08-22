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
    private val repo = RoomRebo()
    private val compositdissposable = CompositeDisposable()
    val roomResult = repo.roomLiveData

    fun getRooms(url: String, date: JsonObject) {
        compositdissposable.add(repo.getRooms(url, date))
    }

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
        compositdissposable.dispose()
    }

}