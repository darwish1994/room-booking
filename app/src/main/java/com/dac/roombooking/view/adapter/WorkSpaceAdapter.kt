package com.dac.roombooking.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.dac.roombooking.R
import com.dac.roombooking.data.callbacks.WorkspaceSelectListener
import com.dac.roombooking.data.model.GlideApp
import com.dac.roombooking.data.model.WorkSpace
import com.dac.roombooking.viewmodel.WorkSpacesViewModel
import kotlinx.android.synthetic.main.item_workspace_layout.view.*

class WorkSpaceAdapter(
    viewModel: WorkSpacesViewModel,
    lifecycleOwner: LifecycleOwner,
    listener: WorkspaceSelectListener
) : RecyclerView.Adapter<WorkSpaceAdapter.WorkerSpaceViewHolder>() {

    var workspaces: ArrayList<WorkSpace> = arrayListOf()
    private val workspaceSelectListener: WorkspaceSelectListener = listener

    init {
        /** get changes on stored workspaces
         * and update view
         */
        viewModel.getWorkSpaceLiveDatra().observe(lifecycleOwner, Observer {
            workspaces.clear()
            workspaces.addAll(it)
            notifyDataSetChanged()
        })

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkerSpaceViewHolder {
        return WorkerSpaceViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_workspace_layout,
                parent,
                false
            ), workspaceSelectListener
        )
    }

    override fun getItemCount(): Int {
        return workspaces.size
    }


    override fun onBindViewHolder(holder: WorkerSpaceViewHolder, position: Int) {
        if (!workspaces.isNullOrEmpty()) {
            holder.bind(workspaces[position])
        }
    }


    inner class WorkerSpaceViewHolder(
        itemView: View,
        workspaceSelectListener: WorkspaceSelectListener
    ) : RecyclerView.ViewHolder(itemView) {
        val icon = itemView.work_space_char
        val spaceTitle = itemView.work_space_title
        private var workspace: WorkSpace? = null

        init {
            itemView.setOnClickListener {
                if (workspace != null)
                    workspaceSelectListener.onWorkspaceSelect(workspace!!)

            }
        }

        fun bind(item: WorkSpace) {
            workspace = item
            spaceTitle.text = workspace!!.name
            GlideApp.with(itemView.context).setDefaultRequestOptions(
                RequestOptions().placeholder(R.drawable.ic_image_holder)
                    .error(R.drawable.ic_broken_image)
            ).load(workspace!!.link + workspace!!.icon)
                .into(icon)
        }

    }


}