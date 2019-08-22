package com.dac.roombooking.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dac.roombooking.R
import com.dac.roombooking.base.BaseActivity
import com.dac.roombooking.model.WorkSpace
import com.dac.roombooking.view.adapter.RoomAdapter
import com.dac.roombooking.viewmodel.WorkSpaceViewModel
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_work_space.*
import timber.log.Timber
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class WorkSpaceActivity : BaseActivity() {
    private lateinit var viewModel: WorkSpaceViewModel
    private lateinit var roomAdapter: RoomAdapter
    private var workSpace: WorkSpace? = null

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_space)

        viewModel = ViewModelProviders.of(this).get(WorkSpaceViewModel::class.java)
        // get available rooms for now
        date_vale.text = "now"


        try {
            workSpace = intent.getParcelableExtra("workspace")

            // call api for get rooms with date "now"
            loading("now")

            // change activity toolbar with workspace name
            title = workSpace?.name

            roomAdapter = RoomAdapter(this, workSpace!!.link!!)
            rooms_rec.adapter = roomAdapter

        } catch (e: NullPointerException) {
            Timber.e(e)
        }



        viewModel.roomResult.observe(this, Observer {
            hideLoading()
            roomAdapter.updateRooms(it, date_vale.text.toString())

        })


    }

    fun loading(date: String) {
        val jsonDate = JsonObject()
        jsonDate.addProperty("date", date)
        showLoading()
        Timber.v(jsonDate.toString())
        viewModel.getRooms(workSpace?.link!!, jsonDate)


    }

    fun loading(date: Long) {
        val jsonDate = JsonObject()
        jsonDate.addProperty("date", date)
        showLoading()
        Timber.v(jsonDate.toString())
        viewModel.getRooms(workSpace?.link!!, jsonDate)


    }

    @SuppressLint("SetTextI18n")
    fun chosenewDate(view: View) {
        val cldr = Calendar.getInstance()

        val day = cldr.get(Calendar.DAY_OF_MONTH)
        val month = cldr.get(Calendar.MONTH)
        val year = cldr.get(Calendar.YEAR)
        // date picker dialog
        val picker = DatePickerDialog(this, { view1, year1, monthOfYear, dayOfMonth ->
            date_vale.text = "$dayOfMonth-${monthOfYear + 1}-$year1"
            val date = dateFormat.parse("$dayOfMonth-${monthOfYear + 1}-$year1")
            if (workSpace != null) {
                loading(Timestamp(date!!.time).time)
            }

        }, year, month, day)
        // force user not to chose day before today
        picker.datePicker.minDate = System.currentTimeMillis() - 1000
        picker.show()

    }


}
