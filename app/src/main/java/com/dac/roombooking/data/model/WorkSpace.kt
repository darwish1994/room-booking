package com.dac.roombooking.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Parcelize it make object parcaple to move between activity
 * */
@Parcelize
open class WorkSpace(

    @PrimaryKey
    @SerializedName("title")
    var name: String? = null,
    @SerializedName("icon")
    var icon: String? = null,
    var link: String? = null
) : RealmObject(), Parcelable