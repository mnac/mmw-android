package com.mmw.data.source.remote.api

import com.mmw.data.model.*
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.http.*

/**
 * Created by Mathias on 24/08/2017.
 *
 */
interface TripService {

    @POST("/trip")
    fun createTrip(@Body trip: Trip): Observable<TripResult>

    @PUT("/trip")
    fun updateTrip(@Body trip: Trip): Observable<TripResult>

    @GET("/trip/user/{uuid}")
    fun getUserTrips(@Path("uuid") uuid: String, @Query("id") id: String, @Query("userId") userId: String, @Query("creationDate") date: String): Observable<UserTrips>

    @POST("/stage")
    fun createStage(@Body stage: Stage): Observable<StageResult>

    @GET("/stage/trip/{uuid}")
    fun getTripStages(@Path("uuid") tripId: String): Observable<StagesResult>

    @GET("/timeline")
    fun getTimeline(@Path("uuid") tripId: String): Observable<Timeline>

    companion object factory {
        fun create(client: Retrofit): TripService {
            return client.create(TripService::class.java)
        }
    }
}