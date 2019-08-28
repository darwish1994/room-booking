package com.dac.roombooking.data.model

import com.google.gson.annotations.SerializedName

data class ResponseError(
    @SerializedName("error")
    var error: DefaultError
)