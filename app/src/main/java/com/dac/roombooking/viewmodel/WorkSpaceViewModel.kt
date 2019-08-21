package com.dac.roombooking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dac.roombooking.model.WorkSpace
import io.realm.Realm
import io.realm.RealmResults

class WorkSpaceViewModel : ViewModel() {
    private val realm = Realm.getDefaultInstance()
    private val workSpace: MutableLiveData<List<WorkSpace>> = MutableLiveData()

    init {
        realm.where(WorkSpace::class.java).findAllAsync().addChangeListener { t: RealmResults<WorkSpace> ->
            workSpace.value = t
        }
    }


    fun addWorkSpace(title: String, group: String) {
        realm.executeTransactionAsync {
            val workspace = WorkSpace()
            workspace.title = title
            workspace.group = group
            it.insertOrUpdate(workspace)
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