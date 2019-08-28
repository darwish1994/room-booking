package com.dac.roombooking.view

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.dac.roombooking.R
import com.dac.roombooking.base.BaseActivity
import com.dac.roombooking.data.model.Passes
import com.dac.roombooking.data.model.ResponseError
import com.dac.roombooking.data.model.Room
import com.dac.roombooking.view.adapter.BookTimeAdapter
import com.dac.roombooking.view.adapter.ParticipantAdapter
import com.dac.roombooking.view.adapter.ViewPagerAdapter
import com.dac.roombooking.view.fragmenr.DoneFragment
import com.dac.roombooking.viewmodel.RoomViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_room_details.*
import timber.log.Timber

class RoomDetailsActivity : BaseActivity() {

    private lateinit var timesAdapter: BookTimeAdapter
    private lateinit var imageAdapter: ViewPagerAdapter
    private lateinit var viewmodel: RoomViewModel
    private lateinit var passesAdapter: ParticipantAdapter
    private val gson = Gson()

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
            /**
             * this fun for filter times
             * it check every times if it has booked before or not
             * and return result in live data response
             * */
            viewmodel.filterTimes(roomData!!.avail)

            /** create image slider adapter for room images
             * user can slide for images
             * images load using glide library
             */
            imageAdapter = ViewPagerAdapter(this, roomData.images, url!!)
            image_slider.adapter = imageAdapter

            /** create dots for slider
             * add layout prams for dots prams is make image wrap content
             * prams is make image margin 8 dp *
             * */
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)
            dot_container.removeAllViews()
            for (i in roomData.images) {
                val image = ImageView(this)
                image.layoutParams = params
                image.setImageResource(R.drawable.n_active_squire)
                dot_container.addView(image)
            }
            // change first dot layout
            if (dot_container.getChildAt(0) != null)
                (dot_container.getChildAt(0) as ImageView).setImageResource(R.drawable.active_squire)

            // add listener of slider paging
            image_slider.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    for (i in 0 until roomData.images.size) {
                        if (dot_container.getChildAt(i) != null)
                            if (i == position) {
                                (dot_container.getChildAt(i) as ImageView).setImageResource(R.drawable.active_squire)
                            } else {
                                (dot_container.getChildAt(i) as ImageView).setImageResource(R.drawable.n_active_squire)
                            }
                    }
                }
            })


        } catch (e: NullPointerException) {
            Timber.e(e)
        }


        // listen for select time changes
        viewmodel.sellectTimeChangeLiveData.observe(this, Observer {
            if (it.isEmpty()) {
                book_btn.visibility = View.GONE
            } else {
                book_btn.visibility = View.VISIBLE

            }

        })

        // listen for booking api response
        viewmodel.bookRoomLiveData.observe(this, Observer {
            hideLoading()
            if (it == null) {
                // show user error
                Snackbar.make(main_view, "some thing go wrong try again", Snackbar.LENGTH_SHORT).show()

            } else {

                // if book success
                if (it.has("success")) {
                    // save event data to db
                    viewmodel.saveEventtoDb()

                }
                // if book fail show message with error
                else if (it.has("error")) {
                    val error = gson.fromJson(it, ResponseError::class.java)
                    Snackbar.make(main_view, error.error.text, Snackbar.LENGTH_SHORT).show()
                }


            }


        })

        /**
         * listen for result of saving event on device
         * helps to show action done for user or not
         */
        viewmodel.saveEvent.observe(this, Observer {
            if (it) {
                val doneFragment = DoneFragment()
                val bundel = Bundle()
                bundel.putString("date", viewmodel.date)
                bundel.putString("time", viewmodel.selectedTimes)
                doneFragment.arguments = bundel
                supportFragmentManager.beginTransaction().replace(R.id.container, doneFragment).commit()
            }


        })

        // listen for times filter
        viewmodel.timesLiveData.observe(this, Observer {

            timesAdapter.updateTimes(it)

        })


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    /**
     * fun that submit booking requist
     * it check for user input like title and description
     * it check user add participant or not
     *
     * */
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

    /**
     * add new participate user for recyclar view
     * check on enter data name,phone, email
     * if phone has no code it will consider as german number
     *
     * */
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
            //Todo check if phone has code or not


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
