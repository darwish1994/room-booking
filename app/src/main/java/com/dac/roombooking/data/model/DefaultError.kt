package com.dac.roombooking.data.model

import com.google.gson.annotations.SerializedName

data class DefaultError(
    @SerializedName("text")
    var text: String,
    @SerializedName("code")
    var code: Int
)