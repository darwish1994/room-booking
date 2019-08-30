package com.dac.roombooking.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.dac.roombooking.R
import com.github.ybq.android.spinkit.style.Wave
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.loading_layout.view.*

open class BaseActivity : DaggerAppCompatActivity() {
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * why i make dialog here as oop all activity class will inherts from this activity so i can call it on all activity
         * */


        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        val view = LayoutInflater.from(this).inflate(R.layout.loading_layout, null, false)
        val progressBar = view.spin_kit
        builder.setView(view)
        val doubleBounce = Wave()
        progressBar.setIndeterminateDrawable(doubleBounce)
        builder.setCancelable(false)
        alertDialog = builder.create()

    }

    fun showLoading() {
        alertDialog!!.show()
    }

    fun hideLoading() {
        alertDialog!!.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        /**
         * if activity destroy and dialog is showing it will dismiss and clear  it
         * */
        if (alertDialog != null)
            alertDialog!!.dismiss()
        alertDialog = null

    }
}