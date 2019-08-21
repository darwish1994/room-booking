package com.dac.roombooking.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dac.roombooking.R
import com.dac.roombooking.model.GlideApp
import com.dac.roombooking.model.WorkSpace
import com.dac.roombooking.view.WorkSpaceActivity
import kotlinx.android.synthetic.main.item_workspace_layout.view.*
import timber.log.Timber

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
            Timber.v("wrkspace name %s", item.name)
            holder.spaceTitle.text = item.name
            GlideApp.with(context).load(item.link + item.icon).into(holder.icon)
            holder.workSpaceItem.setOnClickListener {
                context.startActivity(Intent(context, WorkSpaceActivity::class.java).putExtra("workspace", item))
            }


        }


    }

    fun updateList(list: List<WorkSpace>) {
        workspaces = list
        notifyDataSetChanged()
    }

    inner class WorkerSpaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon = itemView.work_space_char
        val spaceTitle = itemView.work_space_title
        val workSpaceItem = itemView.work_space_item

    }


}