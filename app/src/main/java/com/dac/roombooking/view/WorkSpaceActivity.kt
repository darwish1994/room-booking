package com.dac.roombooking.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import com.dac.roombooking.R
import com.dac.roombooking.base.BaseActivity
import com.dac.roombooking.data.callbacks.RoomSelectListener
import com.dac.roombooking.data.model.Room
import com.dac.roombooking.data.model.WorkSpace
import com.dac.roombooking.view.adapter.RoomAdapter
import com.dac.roombooking.viewmodel.WorkSpaceViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_work_space.*
import timber.log.Timber
import java.util.*

class WorkSpaceActivity : BaseActivity(), RoomSelectListener {


    private lateinit var viewModel: WorkSpaceViewModel
    private lateinit var roomAdapter: RoomAdapter
    private var workSpace: WorkSpace? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_space)

        // init view model for activity
        viewModel = ViewModelProviders.of(this).get(WorkSpaceViewModel::class.java)
        // get available rooms for now
        date_vale.text = "now"


        try {
            workSpace = intent.getParcelableExtra("workspace")

            viewModel.workSpace = workSpace!!
            // call api for get rooms with date "now"
            viewModel.loadingRooms("now")

            // change activity toolbar with workspace name
            title = workSpace?.name

            roomAdapter = RoomAdapter(workSpace!!.link!!, viewModel, this, this)
            rooms_rec.adapter = roomAdapter

        } catch (e: NullPointerException) {
            Timber.e(e)
            hideLoading()
        }


        /**
         * check box listener for available
         * */
        avilable_btn.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                if (!roomAdapter.getrooms().isNullOrEmpty())
                    viewModel.filterAvilable(roomAdapter.getrooms()!!)
            } else {
                viewModel.loadingRooms(date_vale.text.toString())
            }

        }


        viewModel.getShowLoadingLiveData().observe(this, androidx.lifecycle.Observer {
            if (it)
                showLoading()
            else
                hideLoading()
        })

        viewModel.getErrorResponseLiveData().observe(this, androidx.lifecycle.Observer {

            Snackbar.make(main_view, it.error.text, Snackbar.LENGTH_SHORT).show()

        })


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
            if (workSpace != null) {
                viewModel.loadingRooms(date_vale.text.toString())
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
                    viewModel.loadingRooms(date_vale.text.toString())

                }

                return false

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    viewModel.loadingRooms(date_vale.text.toString())

                }

                return false
            }
        })

        return true
    }


    override fun selectRoom(room: Room) {
        val intent = Intent(applicationContext, RoomDetailsActivity::class.java)
        intent.putExtra("room", room)
        intent.putExtra("workSpace", viewModel.workSpace)
        intent.putExtra("date", date_vale.text.toString())
        startActivity(intent)
    }


    override fun onResume() {
        super.onResume()
        // update view after finish or back from  room Activity
        viewModel.loadingRooms(date_vale.text.toString())
    }
}
