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

        viewmodel = ViewModelProviders.of(this).get(AddWorkSpaceVewModel::class.java)

        viewmodel.getRequestResult().observe(this, Observer {
            hideLoading()
            save_btn.isEnabled = true
            if (it.error) {
                when (it.code) {
                    404 -> workspace_name.error = resources.getString(R.string.work_space_host_not_valid)
                }
            } else {
                setResult(Activity.RESULT_OK)
                finish()
            }


        })


    }

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
