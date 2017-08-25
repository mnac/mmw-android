package com.mmw.data.source.remote

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
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
class RestClient(baseUrl: String, token: String) {

    private var bearer: String = ""
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

            chain.proceed(requestBuilder.build())
        }
    }

    fun setToken(token: String) {
        bearer = if (!token.isEmpty()) "bearer " + token else ""
    }

    init {
        setToken(token)

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(getNetworkInterceptor())
                .addInterceptor(getLoggingInterceptor())
                .build()

        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

        retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
    }
}
