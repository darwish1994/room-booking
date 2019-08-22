package com.dac.roombooking.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dac.roombooking.R
import com.dac.roombooking.viewmodel.RoomViewModel
import kotlinx.android.synthetic.main.time_item_layout.view.*

class BookTimeAdapter(val context: Context, val viewmodel: RoomViewModel) :
    RecyclerView.Adapter<BookTimeAdapter.BookTimeViewHolder>() {
    private var times: List<String>? = null
    private val inflator = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookTimeViewHolder {
        return BookTimeViewHolder(inflator.inflate(R.layout.time_item_layout, parent, false))

    }

    override fun getItemCount(): Int {
        if (times != null)
            return times!!.size
        return 0

    }

    override fun onBindViewHolder(holder: BookTimeViewHolder, position: Int) {
        if (times != null) {
            val time = times!![position]
            val parts = time.split("-")
            if (!parts.isNullOrEmpty() && parts.size > 1) {
                holder.startTime.text = parts[0]
                holder.endTime.text = parts[1]
            }

            holder.checked.isChecked = viewmodel.selectedTimes.equals(time, false)

            holder.time_item.setOnClickListener {
                viewmodel.selectedTimes = time
                viewmodel.sellectTimeChangeLiveData.value = time
                notifyDataSetChanged()
            }


        }

    }

    fun updateTimes(data: List<String>?) {
        times = data
        notifyDataSetChanged()
    }

    inner class BookTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val checked = itemView.time_check
        val startTime = itemView.start_time
        val endTime = itemView.end_time
        val time_item = itemView.time_item


    }

}