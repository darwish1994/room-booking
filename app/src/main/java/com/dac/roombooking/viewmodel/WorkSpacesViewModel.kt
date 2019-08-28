package com.dac.roombooking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dac.roombooking.data.model.WorkSpace
import io.realm.Realm
import io.realm.RealmResults

class WorkSpacesViewModel : ViewModel() {
    private val realm = Realm.getDefaultInstance()
    private val workSpace: MutableLiveData<List<WorkSpace>> = MutableLiveData()


    init {
        // realm change lister for get changes on table of work spaces
        realm.where(WorkSpace::class.java).findAllAsync().addChangeListener { t: RealmResults<WorkSpace> ->
            workSpace.value = t
        }
    }


    // get live date for workspace parameter which is updated by realm change listener
    fun getWorkSpaceLiveDatra(): LiveData<List<WorkSpace>> {
        return workSpace
    }

    override fun onCleared() {
        super.onCleared()
        realm.removeAllChangeListeners() // remove all realm listen to prevent memory leak
        realm.close() // close realm instance to prevent leak
    }

}