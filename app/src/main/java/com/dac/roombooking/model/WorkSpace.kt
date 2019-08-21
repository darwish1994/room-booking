package com.dac.roombooking.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class WorkSpace(

    @PrimaryKey
    var id: String? = UUID.randomUUID().toString(),
    var title: String? = null,
    var group: String? = null
) : RealmObject()