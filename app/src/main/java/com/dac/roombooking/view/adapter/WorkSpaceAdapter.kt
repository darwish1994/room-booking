package com.dac.roombooking.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dac.roombooking.R
import com.dac.roombooking.model.WorkSpace
import kotlinx.android.synthetic.main.item_workspace_layout.view.*

class WorkSpaceAdapter(val context: Context) : RecyclerView.Adapter<WorkSpaceAdapter.WorkerSpaceViewHolder>() {

    var workspaces: List<WorkSpace>? = null
    val layoutInflator = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkerSpaceViewHolder {

        return WorkerSpaceViewHolder(layoutInflator.inflate(R.layout.item_workspace_layout, parent, false))
    }

    override fun getItemCount(): Int {
        if (workspaces != null)
            return workspaces!!.size
        return 0
    }


    override fun onBindViewHolder(holder: WorkerSpaceViewHolder, position: Int) {
        if (!workspaces.isNullOrEmpty()) {
            val item = workspaces!![position]
            holder.spaceChar.text = item.group
            holder.spaceTitle.text = item.title


        }


    }

    fun updateList(list: List<WorkSpace>) {
        workspaces = list
        notifyDataSetChanged()
    }

    inner class WorkerSpaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val spaceChar = itemView.work_space_char
        val spaceTitle = itemView.work_space_title
    }


}