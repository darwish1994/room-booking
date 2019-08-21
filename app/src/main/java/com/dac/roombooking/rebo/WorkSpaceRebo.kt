package com.dac.roombooking.rebo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dac.roombooking.model.DeafoultRequest
import com.dac.roombooking.model.WorkSpace
import com.dac.roombooking.network.ApiManager
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import timber.log.Timber

class WorkSpaceRebo {
    private val apiManager = ApiManager
    private val requestStatus = MutableLiveData<DeafoultRequest>()

    fun checkWorkSpace(url: String): Disposable {
        return apiManager.getCallsAPI(url).getworkspace()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.newThread())
            .subscribeWith(object : DisposableSingleObserver<WorkSpace>() {
                override fun onSuccess(t: WorkSpace) {
                    requestStatus.postValue(DeafoultRequest(false, 200, "done"))
                    // add workspace to Db
                    val realm = Realm.getDefaultInstance()
                    t.link = url
                    realm.beginTransaction()
                    realm.insertOrUpdate(t)
                    realm.commitTransaction()
                    realm.close()

                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                    when (e) {
                        is HttpException -> {
                            requestStatus.postValue(DeafoultRequest(true, e.code(), e.message()))
                        }

                        else -> {
                            requestStatus.postValue(DeafoultRequest(true, -1, "some thing goes wrong"))
                        }


                    }

                }
            })
    }

    fun getAddworkSpace(): LiveData<DeafoultRequest> {
        return requestStatus
    }

}