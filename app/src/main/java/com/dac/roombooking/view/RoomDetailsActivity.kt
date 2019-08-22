package com.dac.roombooking.view

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dac.roombooking.R
import com.dac.roombooking.base.BaseActivity
import com.dac.roombooking.model.Passes
import com.dac.roombooking.model.Room
import com.dac.roombooking.view.adapter.BookTimeAdapter
import com.dac.roombooking.view.adapter.ParticipantAdapter
import com.dac.roombooking.view.adapter.ViewPagerAdapter
import com.dac.roombooking.view.fragmenr.DoneFragment
import com.dac.roombooking.viewmodel.RoomViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_room_details.*
import timber.log.Timber

class RoomDetailsActivity : BaseActivity() {

    private lateinit var timesAdapter: BookTimeAdapter
    private lateinit var imageAdapter: ViewPagerAdapter
    private lateinit var viewmodel: RoomViewModel
    private lateinit var passesAdapter: ParticipantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_details)

        // init view model
        viewmodel = ViewModelProviders.of(this).get(RoomViewModel::class.java)


        // adapter for view participate user
        passesAdapter = ParticipantAdapter(this)
        //add participate adapter to participate recyclar
        participant_rec.adapter = passesAdapter

        //  available times adapter
        timesAdapter = BookTimeAdapter(this, viewmodel)

        time_rec.adapter = timesAdapter

        val layoutmanager = GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        time_rec.layoutManager = layoutmanager

        try {
            // get data from intent
            val url = intent.getStringExtra("url")
            val roomData = intent.getParcelableExtra<Room>("room")
            val date = intent.getStringExtra("date")
            //set data to view model
            viewmodel.date = date
            viewmodel.url = url
            viewmodel.room = roomData


            // change activity toolbar with room name
            title = roomData.name
            viewmodel.filterTimes(roomData!!.avail)

            imageAdapter = ViewPagerAdapter(this, roomData.images, url!!)
            image_slider.adapter = imageAdapter


        } catch (e: NullPointerException) {
            Timber.e(e)
        }


        viewmodel.mapChanges.observe(this, Observer {
            if (it.isEmpty()) {
                book_btn.visibility = View.GONE
            } else {
                book_btn.visibility = View.VISIBLE

            }

        })


        viewmodel.bookRoomLiveData.observe(this, Observer {
            hideLoading()
            if (it == null) {
                Timber.e("error in booking")

            } else {
                if (it.has("success")) {
                    // save event data to db
                    viewmodel.saveEventtoDb()

                } else if (it.has("error")) {

                    //TODO show message for user enable him to solve proplem

                }


            }


        })

        viewmodel.saveEvent.observe(this, Observer {
            if (it) {
                supportFragmentManager.beginTransaction().replace(R.id.container, DoneFragment()).commit()
            }


        })

        viewmodel.timesLiveData.observe(this, Observer {

            timesAdapter.updateTimes(it)


        })
    }


    fun book(view: View) {


        // hide keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)


        // check fields
        if (event_title.editText!!.text.toString().trim().isEmpty()) {
            event_title.error = "required"
            return
        }
        if (event_description.editText!!.text.toString().trim().isEmpty()) {
            event_description.error = "required"
            return
        }
        if (viewmodel.selectedTimes.isEmpty()) {
            Snackbar.make(main_view, "please select one time", Snackbar.LENGTH_SHORT).show()
            return
        }
        if (passesAdapter.getpasses().isNullOrEmpty()) {

            val action = Snackbar.make(main_view, "you need at least one participant", Snackbar.LENGTH_SHORT)
                .setAction("add") {
                    addpass(it)

                }
            action.show()
            return
        }

        // show loading
        showLoading()
        if (!viewmodel.bookTimes(
                event_title.editText!!.text.toString().trim(),
                event_description.editText!!.text.toString(),
                passesAdapter.getpasses()!!
            )
        ) {
            hideLoading()
        }

    }

    fun addpass(view: View) {
        val dialog = AlertDialog.Builder(this)
        val form = LayoutInflater.from(this).inflate(R.layout.add_pass_dialog_form, null, false)
        dialog.setView(form)
        val alert = dialog.create()
        alert.show()
        val add = form.findViewById<Button>(R.id.add_pass)
        val name = form.findViewById<TextInputLayout>(R.id.name_value)
        val email = form.findViewById<TextInputLayout>(R.id.email_value)
        val phone = form.findViewById<TextInputLayout>(R.id.phone_value)
        add.setOnClickListener {
            if (name.editText?.text.toString().isEmpty()) {
                name.error = "enter name"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email.editText?.text.toString()).matches()) {
                email.error = "enter valid email"
                return@setOnClickListener
            }
            if (!Patterns.PHONE.matcher(phone.editText?.text.toString()).matches()) {
                phone.error = "enter valid phone"
                return@setOnClickListener
            }



            passesAdapter.addPasses(
                Passes(
                    name.editText?.text.toString(),
                    email.editText?.text.toString(),
                    phone.editText?.text.toString()
                )
            )
            alert.dismiss()


        }


    }

}
