package com.dac.roombooking.model

import com.google.gson.annotations.SerializedName

data class ResponseError(
    @SerializedName("error")
    var error: DefaultError
)