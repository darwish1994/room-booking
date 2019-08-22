package com.dac.roombooking.view

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dac.roombooking.R
import com.dac.roombooking.base.BaseActivity
import com.dac.roombooking.viewmodel.AddWorkSpaceVewModel
import kotlinx.android.synthetic.main.activity_add_work_space.*
import timber.log.Timber

class AddWorkSpace : BaseActivity() {

    private lateinit var viewmodel: AddWorkSpaceVewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_work_space)

        // create view model for add work space activity
        viewmodel = ViewModelProviders.of(this).get(AddWorkSpaceVewModel::class.java)

        /** listen for get workspace api result
         * if return code is 404 not found it means that no work space with this link
         *
         * */
        viewmodel.getRequestResult().observe(this, Observer {
            hideLoading()
            save_btn.isEnabled = true
            if (it.error) {
                when (it.code) {
                    404 -> workspace_name.error = resources.getString(R.string.work_space_host_not_valid)
                    500 -> workspace_name.error = resources.getString(R.string.serer_proble)
                    -1 -> workspace_name.error = resources.getString(R.string.network_error)
                }
            } else {
                setResult(Activity.RESULT_OK)
                finish()
            }


        })


    }

    /**
     * fun for call get workspace with url entered by user
     * check if user add url with "https" or not
     * if not it add it for url
     * check if url last char is "/" for prevent retrofit url error
     * if request success it will save it in repo
     * realm save data in background
     * to prevent leak ui
     *
     * */
    fun addWorkspace(view: View) {
        var url = workspace_name.editText!!.text.toString().trim()
        if (url.isEmpty()) {
            workspace_name.error = resources.getString(R.string.error_work_space_title)
            return
        }
        if (!url.contains("https://", true)) {
            url = "https://$url"
        }

        if (url.last() != '/') {
            url = "$url/"
            Timber.v(url)
        }
        view.isEnabled = false
        showLoading()
        viewmodel.addWorkSpace(url)

    }


}
