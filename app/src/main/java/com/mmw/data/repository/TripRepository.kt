package com.mmw.data.repository

import com.mmw.App
import com.mmw.data.source.remote.api.TripService
import com.mmw.model.*
import io.reactivex.Observable

/**
 * Created by Mathias on 26/08/2017.
 *
 */
class TripRepository private constructor() {

    private val tripService = TripService.create(App.restClient.retrofit)

    private object Holder { val INSTANCE = TripRepository() }

    private lateinit var currentTrip: Trip
    private lateinit var currentStage: Stage

    companion object  {
        val instance: TripRepository by lazy { Holder.INSTANCE }
    }

    fun create(trip: Trip): Observable<TripResult> {
        return tripService.createTrip(trip)
    }

    fun create(stage: Stage): Observable<StageResult> {
        return tripService.createStage(stage)
    }

    fun saveTripState(trip: Trip) {
        currentTrip = trip
    }

    fun getLastTripState() : Trip? {
        return currentTrip
    }

    fun saveStageState(stage: Stage) {
        currentStage = stage
    }

    fun getLastStageState() : Stage? {
        return currentStage
    }

    fun getTimeline(): Observable<Timeline> {
        return tripService.getTimeline()
    }

    fun getStages(tripId: String): Observable<StagesResult> {
        return tripService.getTripStages(tripId)
    }

    fun getUserProfile(): Observable<UserProfile> {
        return tripService.getUserProfile(null, null, null)
    }


    fun getUserStages(tripId: String): Observable<StagesResult> {
        return tripService.getTripStages(tripId)
    }
}