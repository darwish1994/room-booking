package com.dac.roombooking.rebo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dac.roombooking.data.model.DeafoultRequest

class WorkSpaceRebo {
    private val requestStatus = MutableLiveData<DeafoultRequest>()


    fun getAddworkSpace(): LiveData<DeafoultRequest> {
        return requestStatus
    }

}