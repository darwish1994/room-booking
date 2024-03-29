package com.dac.roombooking.data.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Parcelize it make object parcaple to move between activity
 * */
@Parcelize
data class Room(
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("location")
    @Expose
    var location: String,
    @SerializedName("equipment")
    @Expose
    var equipment: List<String>,
    @SerializedName("size")
    @Expose
    var size: String,
    @SerializedName("capacity")
    @Expose
    var capacity: Int,
    @SerializedName("avail")
    @Expose
    var avail: List<String>,
    @SerializedName("images")
    @Expose
    var images: List<String>

) : Parcelable