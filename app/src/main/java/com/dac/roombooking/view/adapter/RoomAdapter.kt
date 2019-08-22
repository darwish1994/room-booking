package com.dac.roombooking.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.dac.roombooking.R
import com.dac.roombooking.model.Room
import com.dac.roombooking.view.RoomDetailsActivity
import kotlinx.android.synthetic.main.room_item_layout.view.*
import timber.log.Timber


class RoomAdapter(val context: Context, val url: String) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {
    private val inflator = LayoutInflater.from(context)
    private var rooms: List<Room>? = null
    private var date: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        return RoomViewHolder(inflator.inflate(R.layout.room_item_layout, parent, false))
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
            val room = rooms!![position]
            holder.roomTitle.text = room.name
            holder.capacity.text = "${room.capacity}"
            holder.location.text = room.location

            var equipment = ""
            for (i in room.equipment)
                equipment += "$i, "
            holder.equipment.text = equipment

            holder.size.text = room.size

            if (room.avail.isNullOrEmpty()) {
                holder.availavble.setBackgroundResource(R.drawable.not_availble_bg)
                holder.availavble.text = "NAv"
            } else {
                holder.availavble.setBackgroundResource(R.drawable.availble_bg)
                holder.availavble.text = "Av ${room.avail.size}"
            }

            val pagerAdapter = ViewPagerAdapter(context, room.images, url)
            holder.imageSwitcher.adapter = pagerAdapter

            holder.dots_containers.removeAllViews()
            for (i in room.images) {
                val image = ImageView(context)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0)

                image.layoutParams = params
                image.setImageResource(R.drawable.n_active_squire)
                holder.dots_containers.addView(image)
            }

            holder.imageSwitcher.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    Timber.v("scrole  postion %s", position)
                    for (i in 0 until room.images.size) {
                        if (holder.dots_containers.getChildAt(i) != null)
                            if (i == position) {
                                (holder.dots_containers.getChildAt(i) as ImageView).setImageResource(R.drawable.active_squire)
                            } else {
                                (holder.dots_containers.getChildAt(i) as ImageView).setImageResource(R.drawable.n_active_squire)

                            }
                    }

                }
            })
            holder.room_item.setOnClickListener {
                // check for user select date
                if (date != null) {
                    val intent = Intent(context, RoomDetailsActivity::class.java)
                    intent.putExtra("room", room)
                    intent.putExtra("url", url)
                    intent.putExtra("date", date)
                    context.startActivity(intent)
                }
            }

        }


    }

    fun updateRooms(data: List<Room>?, date: String) {
        rooms = data
        this.date = date
        notifyDataSetChanged()
    }

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageSwitcher = itemView.slider
        val availavble = itemView.availablity
        val roomTitle = itemView.room_title
        val location = itemView.location_value
        val size = itemView.size_value
        val capacity = itemView.capacity_value
        val equipment = itemView.equipment_value
        val dots_containers = itemView.dot_containerr
        val room_item = itemView.room_item


    }
}