package com.mmw.data.source.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by Mathias on 24/08/2017.
 *
 */
class RestClient(baseUrl: String, token: String, userId: String) {

    private var bearer: String = ""
    private var uuid: String = ""
    val retrofit: Retrofit

    private fun getLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    private fun getNetworkInterceptor(): Interceptor {
        return Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()

            if (!bearer.isEmpty() && "/connect" != chain.request().url().url().path) {
                requestBuilder.header("Authorization", bearer)
            }

            if (!uuid.isEmpty()) {
                requestBuilder.header("userId", uuid)
            }

            chain.proceed(requestBuilder.build())
        }
    }

    fun setHeaders(token: String, userId: String) {
        bearer = if (!token.isEmpty()) "Bearer " + token else ""
        uuid = userId
    }

    init {
        setHeaders(token, userId)

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(getNetworkInterceptor())
                .addInterceptor(getLoggingInterceptor())
                .build()

        retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
    }
}
