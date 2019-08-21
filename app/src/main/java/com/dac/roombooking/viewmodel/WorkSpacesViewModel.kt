package com.dac.roombooking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dac.roombooking.model.WorkSpace
import io.realm.Realm
import io.realm.RealmResults

class WorkSpacesViewModel : ViewModel() {
    private val realm = Realm.getDefaultInstance()
    private val workSpace: MutableLiveData<List<WorkSpace>> = MutableLiveData()

    init {
        realm.where(WorkSpace::class.java).findAllAsync().addChangeListener { t: RealmResults<WorkSpace> ->
            workSpace.value = t
        }
    }


    fun getWorkSpaceLiveDatra(): LiveData<List<WorkSpace>> {
        return workSpace
    }

    override fun onCleared() {
        super.onCleared()
        realm.removeAllChangeListeners()
        realm.close()
    }

}