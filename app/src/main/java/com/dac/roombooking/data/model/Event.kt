package com.dac.roombooking.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Event(
    @PrimaryKey
    var id: String? = UUID.randomUUID().toString(),
    var room: String? = null,
    var date: String? = null,
    var times: String? = null,
    var workSpace: String? = null
) : RealmObject()