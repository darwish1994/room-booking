package com.dac.roombooking.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.dac.roombooking.R
import kotlinx.android.synthetic.main.activity_add_work_space.*

class AddWorkSpace : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_work_space)
        // create group spinner
        // this group will manage workspace url
        // group 1 ==> https://acorp.dac.eu/roombooking_app/  , char "A"
        // group 2 ==> https://bcorp.dac.eu/roombooking_app/  , char "B"

        val groupAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayListOf("group A", "group B"))
        group_spinner.adapter = groupAdapter


    }

    fun addWorkspace(view: View) {
        if (workspace_name.editText!!.text.toString().trim().isEmpty()) {
            workspace_name.error = resources.getString(R.string.error_work_space_title)
            return
        }

        // adda data to intent to set result back to home activity
        val result = Intent()
        result.putExtra("title", workspace_name.editText!!.text.toString())
        if (group_spinner.selectedItemPosition == 0)
            result.putExtra("char", "A")
        else
            result.putExtra("char", "B")

        setResult(Activity.RESULT_OK, result)
        finish()

    }


}
