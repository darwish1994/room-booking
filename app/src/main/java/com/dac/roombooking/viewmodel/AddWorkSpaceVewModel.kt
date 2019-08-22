package com.dac.roombooking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dac.roombooking.model.DeafoultRequest
import com.dac.roombooking.rebo.WorkSpaceRebo
import io.reactivex.disposables.CompositeDisposable

class AddWorkSpaceVewModel : ViewModel() {
    private val workSpaceRepo = WorkSpaceRebo()
    private val compositDispossable = CompositeDisposable()

    /**
     * call api get work space
     *
     * */
    fun addWorkSpace(url: String) {
        compositDispossable.add(workSpaceRepo.checkWorkSpace(url))
    }

    /**
     * listen for request result
     * and update it with live data
     * */
    fun getRequestResult(): LiveData<DeafoultRequest> {
        return workSpaceRepo.getAddworkSpace()
    }


    /**
     * is view model destroy of life cycle
     * */
    override fun onCleared() {
        super.onCleared()
        // clear disposable of rxjava
        compositDispossable.dispose()
    }

}