package com.dac.roombooking.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
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

        // init view model for activity
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
            hideLoading()
        }


        // update rooms ui with returned data
        viewModel.roomResult.observe(this, Observer {
            hideLoading()
            roomAdapter.updateRooms(it, date_vale.text.toString())

        })

        // check
        avilable_btn.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                if (!roomAdapter.getrooms().isNullOrEmpty())
                    viewModel.filterAvilable(roomAdapter.getrooms()!!)
            } else {
                reloadData()
            }

        }


    }

    /**
     * fun set api request with new date
     * this used if date is string
     * create request body for api calls
     *
     * */
    fun loading(date: String) {
        val jsonDate = JsonObject()
        jsonDate.addProperty("date", date)
        showLoading()
        Timber.v(jsonDate.toString())
        viewModel.getRooms(workSpace?.link!!, jsonDate)


    }

    /**
     * fun set api request with new date
     * this used if date is timestamp
     * create request body for api calls
     *
     * */
    fun loading(date: Long) {
        val jsonDate = JsonObject()
        jsonDate.addProperty("date", date)
        showLoading()
        Timber.v(jsonDate.toString())
        viewModel.getRooms(workSpace?.link!!, jsonDate)


    }

    /**
     * show calender with start date form today
     * user will select new time
     * and view will updated based on time
     *
     * */
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.filter_menu, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val search = (menu?.findItem(R.id.search)?.actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
        }
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank() && !roomAdapter.getrooms().isNullOrEmpty()) {

                    viewModel.searchFilter(query, roomAdapter.getrooms()!!, avilable_btn.isChecked)

                } else {
                    reloadData()

                }

                return false

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {

                    reloadData()

                }

                return false
            }
        })

        return true
    }

    fun reloadData() {

        if (date_vale.text.toString().equals("now", true) || date_vale.text.toString().equals("today", true)) {

            loading(date_vale.text.toString())
        } else {

            loading(dateFormat.parse(date_vale.text.toString()).time)
        }


    }

}
