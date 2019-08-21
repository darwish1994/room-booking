package com.dac.roombooking.network.model

import com.google.gson.annotations.SerializedName

data class Workspace(
    @SerializedName("name")
    var name: String,
    @SerializedName("icon")
    var icon: String

)