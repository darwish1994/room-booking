package com.dac.roombooking.viewmodel

import androidx.lifecycle.ViewModel
import com.dac.roombooking.rebo.RoomRebo
import com.google.gson.JsonObject
import io.reactivex.disposables.CompositeDisposable

class WorkSpaceViewModel : ViewModel() {
    private val repo = RoomRebo()
    private val compositdissposable = CompositeDisposable()
    val roomResult = repo.roomLiveData

    fun getRooms(url: String, date: JsonObject) {
        compositdissposable.add(repo.getRooms(url, date))
    }



}