package com.mmw.data.repository

import com.mmw.App
import com.mmw.data.model.Stage
import com.mmw.data.model.StageResult
import com.mmw.data.model.Trip
import com.mmw.data.model.TripResult
import com.mmw.data.source.remote.api.TripService
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
}