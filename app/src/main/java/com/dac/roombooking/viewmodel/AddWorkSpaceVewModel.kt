package com.dac.roombooking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dac.roombooking.data.model.WorkSpace
import com.dac.roombooking.network.ApiManager
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import timber.log.Timber

class AddWorkSpaceVewModel : ViewModel() {
    private var apiManager: ApiManager? = ApiManager
    private val compositDispossable = CompositeDisposable()
    private val loading: MutableLiveData<Boolean> = MutableLiveData()
    private val success: MutableLiveData<Boolean> = MutableLiveData()
    private val message: MutableLiveData<String> = MutableLiveData()

    /**
     * call api get work space
     *if workspace found it will add it to database and when back to workspaces Activity realm change listener will update view
     * */
    fun addWorkSpace(url: String) {
        if (apiManager != null) {
            loading.value = true
            compositDispossable.add(
                apiManager!!.getCallsAPI(url).getworkspace()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribeWith(object : DisposableSingleObserver<WorkSpace>() {
                        override fun onSuccess(t: WorkSpace) {
                            // add workspace to Db

                            val realm = Realm.getDefaultInstance()
                            t.link = url
                            realm.executeTransaction {
                                realm.insertOrUpdate(t)
                            }
                            realm.close()

                            // hide loading dialog
                            loading.postValue(false)
                            // notify that request success
                            success.postValue(true)


                        }

                        override fun onError(e: Throwable) {
                            Timber.e(e)
                            loading.postValue(false)

                            when (e) {
                                is HttpException -> {
                                    message.postValue(
                                        when (e.code()) {
                                            404 -> "workspace not found"
                                            500 -> "internal server error"
                                            else -> e.message()
                                        }
                                    )

                                }
                                else -> {
                                    message.postValue("some thing goes wrong")
                                }

                            }

                        }
                    })
            )
        }
    }

    /**
     * listen for request result
     * and update it with live data
     * */
    fun getMessage(): LiveData<String> {
        return message
    }

    fun getLoadingStatus(): LiveData<Boolean> {
        return loading
    }

    fun getSuccessStatus(): LiveData<Boolean> {
        return success
    }


    /**
     * is view model destroy of life cycle
     * */
    override fun onCleared() {
        super.onCleared()
        // clear disposable of rxjava
        compositDispossable.dispose()
        apiManager = null
    }

}