package com.dac.roombooking.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.dac.roombooking.R
import com.dac.roombooking.data.callbacks.RoomSelectListener
import com.dac.roombooking.data.model.Room
import com.dac.roombooking.viewmodel.WorkSpaceViewModel
import kotlinx.android.synthetic.main.room_item_layout.view.*


/**
 * adapter to show rooms and photo slider and
 * listen for changes
 *
 * */

class RoomAdapter(
    val url: String,
    viewModel: WorkSpaceViewModel,
    lifecycleOwner: LifecycleOwner,
    private val listener: RoomSelectListener
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {
    private var rooms: List<Room>? = null

    init {
        viewModel.getRoomLiveData().observe(lifecycleOwner, Observer {
            rooms = it
            notifyDataSetChanged()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        return RoomViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.room_item_layout, parent, false),
            listener
        )
    }

    override fun getItemCount(): Int {
        if (rooms != null)
            return rooms!!.size
        return 0

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        if (rooms != null) {

            // get room
            holder.bind(rooms!![position])
        }


    }


    fun getrooms(): List<Room>? {
        return rooms
    }

    inner class RoomViewHolder(itemView: View, listener: RoomSelectListener) : RecyclerView.ViewHolder(itemView) {
        private val imageSwitcher: ViewPager = itemView.slider
        private val availavble = itemView.availablity
        private val roomTitle = itemView.room_title
        private val location = itemView.location_value
        private val size = itemView.size_value
        private val capacity = itemView.capacity_value
        private val equipment = itemView.equipment_value
        private val dots_containers = itemView.dot_containerr
        private lateinit var room: Room

        init {
            itemView.setOnClickListener {
                if (::room.isInitialized) {
                    listener.selectRoom(room)
                }
            }
        }


        @SuppressLint("SetTextI18n")
        fun bind(item: Room) {
            room = item

            roomTitle.text = room.name
            capacity.text = "${room.capacity}"
            location.text = room.location

            var equipmentValue = ""
            for (i in room.equipment)
                equipmentValue += "$i, "
            equipment.text = equipmentValue

            size.text = room.size

            if (room.avail.isNullOrEmpty()) {
                availavble.setBackgroundResource(R.drawable.not_availble_bg)
                availavble.text = "NAv"
            } else {
                availavble.setBackgroundResource(R.drawable.availble_bg)
                availavble.text = "Av ${room.avail.size}"
            }

            val pagerAdapter = ViewPagerAdapter(room.images, url)
            imageSwitcher.adapter = pagerAdapter


            createDotes(0)
            imageSwitcher.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    createDotes(position)

                }
            })


        }

        /**
         * create dots for slider
         *
         *
         * */
        fun createDotes(position: Int) {

            dots_containers.removeAllViews()
            for (i in room.images) {
                val image = ImageView(itemView.context)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)

                image.layoutParams = params
                if (position == room.images.indexOf(i))
                    image.setImageResource(R.drawable.active_squire)
                else
                    image.setImageResource(R.drawable.n_active_squire)
                dots_containers.addView(image)
            }


        }
    }


}