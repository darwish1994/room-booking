package com.dac.roombooking.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.dac.roombooking.R
import com.dac.roombooking.viewmodel.RoomViewModel
import kotlinx.android.synthetic.main.time_item_layout.view.*

/**
 * adapter for view available time for user
 * it listen for changes from view model
 *
 * ***/
class BookTimeAdapter(private val viewModel: RoomViewModel, lifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<BookTimeAdapter.BookTimeViewHolder>() {
    private val times: ArrayList<String> = arrayListOf()

    init {
        viewModel.getFilterdTimes().observe(lifecycleOwner, Observer {
            times.clear()
            times.addAll(it)
            notifyDataSetChanged()
        })
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookTimeViewHolder {
        return BookTimeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.time_item_layout,
                parent,
                false
            ),
            viewModel
        )

    }

    override fun getItemCount(): Int {
        return times.size
    }

    override fun onBindViewHolder(holder: BookTimeViewHolder, position: Int) {
        holder.bind(times[position])
    }


    inner class BookTimeViewHolder(itemView: View, val viewModel: RoomViewModel) : RecyclerView.ViewHolder(itemView) {

        private val checked = itemView.time_check
        private val startTime = itemView.start_time
        private val endTime = itemView.end_time
        private val time_item = itemView.time_item

        private lateinit var time: String

        fun bind(item: String) {
            time = item
            val parts = time.split("-")
            if (!parts.isNullOrEmpty() && parts.size > 1) {
                startTime.text = parts[0]
                endTime.text = parts[1]
            }

            checked.isChecked = viewModel.selectedTimes.equals(time, false)

            time_item.setOnClickListener {
                viewModel.selectedTimes = time
                viewModel.selectTimeChangeLiveData.value = time
                notifyDataSetChanged()
            }


        }


    }

}