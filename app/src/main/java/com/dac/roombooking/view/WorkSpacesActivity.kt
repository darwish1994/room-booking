package com.dac.roombooking.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dac.roombooking.R
import com.dac.roombooking.base.BaseActivity
import com.dac.roombooking.data.callbacks.WorkspaceSelectListener
import com.dac.roombooking.data.model.WorkSpace
import com.dac.roombooking.view.adapter.WorkSpaceAdapter
import com.dac.roombooking.viewmodel.WorkSpacesViewModel
import kotlinx.android.synthetic.main.activity_work_spaces.*

class WorkSpacesActivity : BaseActivity(), WorkspaceSelectListener {


    private val ADD_WORK_SPACE_REQUEST = 100
    private lateinit var workSpacesAdapter: WorkSpaceAdapter
    private lateinit var viewModel: WorkSpacesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_spaces)

        // add view model
        viewModel = ViewModelProviders.of(this).get(WorkSpacesViewModel::class.java)

        // create adapter for show current stored work spaces
        // user can select one of them to work with it
        workSpacesAdapter = WorkSpaceAdapter(viewModel, this, this)

        // create layout manager for recycler view
        // span count will calculated based on screen size
        val layoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        work_spaces_rec.layoutManager = layoutManager

        // add adapter to recycler view
        work_spaces_rec.adapter = workSpacesAdapter




    }

    /**
     * send user to create work space
     * */
    fun addWorkspace(view: View) {
        startActivityForResult(Intent(this, AddWorkSpace::class.java), ADD_WORK_SPACE_REQUEST)
    }


    override fun onWorkspaceSelect(workspace: WorkSpace) {
        startActivity(
            Intent(application, WorkSpaceActivity::class.java).putExtra(
                "workspace",
                workspace
            )
        )
    }

}
