package com.dac.roombooking.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dac.roombooking.R
import com.dac.roombooking.data.model.Passes
import kotlinx.android.synthetic.main.participant_item_layout.view.*

class ParticipantAdapter(val context: Context) :
    RecyclerView.Adapter<ParticipantAdapter.PassViewHolder>() {
    private var passes: ArrayList<Passes>? = null
    private val inflator = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassViewHolder {
        return PassViewHolder(inflator.inflate(R.layout.participant_item_layout, parent, false))

    }

    override fun getItemCount(): Int {
        if (passes != null)
            return passes!!.size
        return 0

    }

    override fun onBindViewHolder(holder: PassViewHolder, position: Int) {
        if (passes != null) {
            val item = passes!![position]
            holder.name.text = item.name
            holder.email.text = item.email
            holder.phone.text = item.number


        }

    }

    fun addPasses(data: Passes) {
        if (passes != null) {
            passes!!.add(data)
            notifyItemInserted(passes!!.size - 1)
        } else {
            passes = arrayListOf(data)
            notifyDataSetChanged()

        }
    }

    fun getpasses(): List<Passes>? {
        return passes
    }

    inner class PassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name = itemView.name_value
        val email = itemView.email_value
        val phone = itemView.phone_value


    }

}