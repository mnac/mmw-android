package com.mmw.data.source.remote.Location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.annotations.NonNull
import java.io.IOException
import java.util.*


/**
 * Created by Mathias on 26/08/2017.
 *
 */
class ReverseGeocodeObservable private constructor(private val context: Context,
                                                   private val locale: Locale,
                                                   private val latitude: Double,
                                                   private val longitude: Double,
                                                   private val maxResults: Int)
    : ObservableOnSubscribe<List<Address>> {

    @Throws(Exception::class)
    override fun subscribe(@NonNull emitter: ObservableEmitter<List<Address>>) {
        val geoCoder = Geocoder(context, locale)
        try {
            val addresses = geoCoder.getFromLocation(latitude, longitude, maxResults)
            if (addresses != null && !addresses.isEmpty()) {
                emitter.onNext(addresses)
                emitter.onComplete()
            } else {
                emitter.onError(Throwable("No address found"))
            }
        } catch (e: IOException) {
            emitter.onError(e)
        }

    }

    companion object {
        fun createObservable(ctx: Context, locale: Locale,
                             latitude: Double, longitude: Double, maxResults: Int)
                : Observable<List<Address>> {
            return Observable.create(ReverseGeocodeObservable(ctx, locale,
                    latitude, longitude, maxResults))
        }
    }
}