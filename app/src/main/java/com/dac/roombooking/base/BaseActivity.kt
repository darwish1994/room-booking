package com.dac.roombooking.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dac.roombooking.R
import com.github.ybq.android.spinkit.style.Wave
import kotlinx.android.synthetic.main.loading_layout.view.*

open class BaseActivity : AppCompatActivity() {
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        if (alertDialog != null)
            alertDialog!!.dismiss()

    }
}