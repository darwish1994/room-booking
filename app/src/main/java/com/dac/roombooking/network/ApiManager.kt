package com.dac.roombooking.network

import com.dac.roombooking.network.apicalls.ApiCalls
import com.google.gson.GsonBuilder

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * api manager which handel get instance from retrofit and create request for api
 *
 * */
object ApiManager {
    private var retrofit: Retrofit? = null
    private val REQUEST_TIMEOUT = 60
    private var okHttpClient: OkHttpClient? = null

    fun getClient(url: String): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        if (okHttpClient == null)
            initOkHttp()
//        if (retrofit == null) {
        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient!!)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
//        }
        return retrofit!!
    }

    private fun initOkHttp() {
        val httpClient = OkHttpClient().newBuilder()
            .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(interceptor)
        okHttpClient = httpClient.build()
    }

    fun getCallsAPI(URL: String): ApiCalls {
        return getClient(URL).create(ApiCalls::class.java)
    }


}