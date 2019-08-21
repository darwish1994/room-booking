package com.dac.roombooking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dac.roombooking.model.DeafoultRequest
import com.dac.roombooking.rebo.WorkSpaceRebo
import io.reactivex.disposables.CompositeDisposable

class AddWorkSpaceVewModel : ViewModel() {
    private val workSpaceRepo = WorkSpaceRebo()
    private val compositDispossable = CompositeDisposable()

    fun addWorkSpace(url: String) {
        compositDispossable.add(workSpaceRepo.checkWorkSpace(url))
    }

    fun getRequestResult(): LiveData<DeafoultRequest> {
        return workSpaceRepo.getAddworkSpace()
    }


    override fun onCleared() {
        super.onCleared()
        compositDispossable.dispose()
    }

}