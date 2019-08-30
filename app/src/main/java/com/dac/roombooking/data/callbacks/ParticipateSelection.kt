package com.dac.roombooking.data.callbacks

import com.dac.roombooking.data.model.Participate

interface ParticipateSelection {
    fun editParticipate(position: Int, participate: Participate)

}