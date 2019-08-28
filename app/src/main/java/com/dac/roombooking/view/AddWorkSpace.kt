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

        viewmodel.getMessage().observe(this, Observer {
            workspace_name.error = it
        })
        viewmodel.getLoadingStatus().observe(this, Observer {
            if (it)
                showLoading()
            else
                hideLoading()
        })
        viewmodel.getSuccessStatus().observe(this, Observer {
            save_btn.isEnabled = true
            if (it) {
                setResult(Activity.RESULT_OK)
                finish()
            }

        })

        /** listen for get workspace api result
         * if return code is 404 not found it means that no work space with this link
         *
         * */


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
