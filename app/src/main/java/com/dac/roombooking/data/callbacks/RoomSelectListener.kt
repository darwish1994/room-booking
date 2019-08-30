package com.dac.roombooking.data.callbacks

import com.dac.roombooking.data.model.Room

interface RoomSelectListener {

    fun selectRoom(room: Room)
}