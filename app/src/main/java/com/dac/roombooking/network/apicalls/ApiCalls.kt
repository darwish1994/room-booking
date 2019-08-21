package com.dac.roombooking.network.apicalls

import com.dac.roombooking.network.model.Room
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiCalls {


    @GET("workspace")
    fun getworkspace(): Single<W>

    @POST("getrooms")
    fun getRooms(@Body date: String): Single<List<Room>>


}