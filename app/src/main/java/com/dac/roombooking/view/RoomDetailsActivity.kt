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
import com.dac.roombooking.data.callbacks.ParticipateSelection
import com.dac.roombooking.data.model.Participate
import com.dac.roombooking.data.model.Room
import com.dac.roombooking.view.adapter.BookTimeAdapter
import com.dac.roombooking.view.adapter.ParticipantAdapter
import com.dac.roombooking.view.adapter.ViewPagerAdapter
import com.dac.roombooking.view.fragmenr.DoneFragment
import com.dac.roombooking.viewmodel.RoomViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_room_details.*
import timber.log.Timber

class RoomDetailsActivity : BaseActivity(), ParticipateSelection {


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
        //add participate adapter to participate recycler
        participant_rec.adapter = passesAdapter

        //  available times adapter
        timesAdapter = BookTimeAdapter(viewmodel, this)

        time_rec.adapter = timesAdapter

        val layoutManager = GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        time_rec.layoutManager = layoutManager

        try {
            // get data from intent
            viewmodel.room = intent.getParcelableExtra<Room>("room")
            viewmodel.workspace = intent.getParcelableExtra("workSpace")
            viewmodel.date = intent.getStringExtra("date")


            // change activity toolbar with room name
            title = viewmodel.room?.name

            /**
             * this fun for filter times
             * it check every times if it has booked before or not
             * and return result in live data response
             * */
            viewmodel.filterTimes()

            /** create image slider adapter for room imageList
             * user can slide for imageList
             * imageList load using glide library
             */
            imageAdapter = ViewPagerAdapter(viewmodel.room!!.images, viewmodel.workspace.link!!)
            image_slider.adapter = imageAdapter

            createDotes(0)

            // add listener of slider paging
            image_slider.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    createDotes(position)
                }
            })


        } catch (e: NullPointerException) {
            Timber.e(e)
        }


        // listen for select time changes
        viewmodel.selectTimeChangeLiveData.observe(this, Observer {
            if (it.isEmpty()) {
                book_btn.visibility = View.GONE
            } else {
                book_btn.visibility = View.VISIBLE

            }

        })
//show or hide loading dialog
        viewmodel.getShowLoading().observe(this, Observer {
            if (it)
                showLoading()
            else
                hideLoading()

        })

        // show returned message
        viewmodel.getMessage().observe(this, Observer {
            Snackbar.make(container, it, Snackbar.LENGTH_SHORT).show()
        })


        /**
         * listen for result of saving event on device
         * helps to show action done for user or not
         */
        viewmodel.getBookRoomResult().observe(this, Observer {
            if (it) {
                val doneFragment = DoneFragment()
                val bundle = Bundle()
                bundle.putString("date", viewmodel.date)
                bundle.putString("time", viewmodel.selectedTimes)
                doneFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.container, doneFragment).commit()
            }


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

        viewmodel.bookTimes(
            event_title.editText!!.text.toString().trim(),
            event_description.editText!!.text.toString(),
            passesAdapter.getpasses()!!
        )

    }

    /**
     * add new participate user for recyclar view
     * check on enter data name,phone, email
     * if phone has no code it will consider as german number
     *
     * */
    fun addpass(view: View) {
        addOrUpdateParticipate(0, null)
    }


    override fun editParticipate(position: Int, participate: Participate) {

        addOrUpdateParticipate(position, participate)
    }


    /**
     * create dialog to add or edit participace
     * if participate == null that means it will add new one
     * if not it will update it
     * **/

    fun addOrUpdateParticipate(position: Int, participate: Participate?) {

        //check for number of participate if less than room capacity
        if (participate == null && !viewmodel.checkForParticipateCapcity(passesAdapter.itemCount)) {
            return
        }
        val dialog = AlertDialog.Builder(this)
        val form = LayoutInflater.from(this).inflate(R.layout.add_pass_dialog_form, null, false)
        dialog.setView(form)
        val alert = dialog.create()
        alert.show()
        val add = form.findViewById<Button>(R.id.add_pass)
        val name = form.findViewById<TextInputLayout>(R.id.name_value)
        val email = form.findViewById<TextInputLayout>(R.id.email_value)
        val phone = form.findViewById<TextInputLayout>(R.id.phone_value)
        if (participate != null) {
            name.editText?.text?.append(participate.name)
            email.editText?.text?.append(participate.email)
            phone.editText?.text?.append(participate.number)
            add.setText(R.string.update)
        }



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

            if (participate != null) {
                participate.name = name.editText?.text.toString()
                participate.email = email.editText?.text.toString()
                participate.number = phone.editText?.text.toString()

                passesAdapter.notifyItemChanged(position)

            } else

                passesAdapter.addPasses(
                    Participate(
                        name.editText?.text.toString(),
                        email.editText?.text.toString(),
                        phone.editText?.text.toString()
                    )
                )
            alert.dismiss()

        }

    }

    // create dots for image slider
    fun createDotes(position: Int) {

        dot_container.removeAllViews()
        for (i in viewmodel.room!!.images) {
            val image = ImageView(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(8, 0, 8, 0)

            image.layoutParams = params
            if (position == viewmodel.room!!.images.indexOf(i))
                image.setImageResource(R.drawable.active_squire)
            else
                image.setImageResource(R.drawable.n_active_squire)
            dot_container.addView(image)
        }


    }


}
