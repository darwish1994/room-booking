package com.dac.roombooking.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.dac.roombooking.R
import com.dac.roombooking.data.callbacks.ParticipateSelection
import com.dac.roombooking.data.model.Participate
import kotlinx.android.synthetic.main.participant_item_layout.view.*

/**
 * adapter for show participate and interact with their data like edit and delete
 *
 * */
class ParticipantAdapter(private val listener: ParticipateSelection) :
    RecyclerView.Adapter<ParticipantAdapter.PassViewHolder>() {
    private var passes: ArrayList<Participate> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassViewHolder {
        return PassViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.participant_item_layout,
                parent,
                false
            ), listener
        )

    }

    override fun getItemCount(): Int {
        return passes.size
    }

    override fun onBindViewHolder(holder: PassViewHolder, position: Int) {
        holder.bind(passes[position])
    }

    fun addPasses(data: Participate) {
        passes.add(data)
        notifyItemInserted(passes.size - 1)
    }


    fun getpasses(): List<Participate>? {
        return passes
    }

    inner class PassViewHolder(itemView: View, listener: ParticipateSelection) : RecyclerView.ViewHolder(itemView) {

        private val name = itemView.name_value
        private val email = itemView.email_value
        private val phone = itemView.phone_value
        private val moreOp = itemView.more_options
        private lateinit var participate: Participate

        init {
            moreOp.setOnClickListener { view ->
                val popupMenu = PopupMenu(itemView.context, view)
                popupMenu.inflate(R.menu.options_menu)
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.delete_item -> {
                            if (::participate.isInitialized) {
                                val indexOfParticipate = passes.indexOf(participate)
                                passes.remove(participate)
                                notifyItemRemoved(indexOfParticipate)
                            }

                        }
                        R.id.edit_item -> {
                            if (::participate.isInitialized)
                                listener.editParticipate(passes.indexOf(participate), participate)
                        }


                    }

                    return@setOnMenuItemClickListener false
                }


            }

        }

        fun bind(item: Participate) {
            participate = item
            name.text = participate.name
            email.text = participate.email
            phone.text = participate.number

        }


    }

}