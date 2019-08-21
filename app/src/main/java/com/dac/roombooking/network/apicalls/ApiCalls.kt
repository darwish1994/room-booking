package com.dac.roombooking.network.apicalls

import com.dac.roombooking.model.Room
import com.dac.roombooking.model.WorkSpace
import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiCalls {


    @GET("workspace")
    fun getworkspace(): Single<WorkSpace>

    @POST("getrooms")
    fun getRooms(@Body date: JsonObject): Single<List<Room>>


}