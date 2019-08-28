package com.dac.roombooking.data.callbacks

import com.dac.roombooking.data.model.WorkSpace

interface WorkspaceSelectListener {

    fun onWorkspaceSelect(workspace: WorkSpace)

}