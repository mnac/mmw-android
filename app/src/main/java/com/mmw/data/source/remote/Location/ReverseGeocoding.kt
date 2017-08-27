package com.mmw.data.source.remote.Location

import android.content.Context
import android.location.Address
import android.location.Location
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*


/**
 * Created by Mathias on 26/08/2017.
 *
 */
class ReverseGeocoding(private val context: Context, private val listener: Listener) {

    interface Listener {
        fun onAddresses(addresses: List<Address>)
        fun onNoAddressFound(e: Throwable)
    }

    private val disposables: CompositeDisposable = CompositeDisposable()

    fun getAddressFromLocation(location: Location) {
        disposables.add(getReverseGeocodeObservable(location.latitude, location.longitude, 1)
                .onErrorResumeNext(Function { throwable ->
                    sendError("Geocoder failed -> Trying with Google API fallback...", throwable)
                    getFallbackObservable(location, MAX_RESULTS)
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<List<Address>>() {
                    override fun onNext(addresses: List<Address>) {
                        addresses[0].latitude = location.latitude
                        addresses[0].longitude = location.longitude
                        listener.onAddresses(addresses)
                    }

                    override fun onError(e: Throwable) {
                        sendError("Google API Fallback failed -> Can't get address", e)
                    }

                    override fun onComplete() {

                    }
                }))
    }

    fun cancelObservable() {
        if (disposables.size() > 0) disposables.clear()
    }

    fun getFallbackAddressFromKeyword(keyword: String) {
        getFallbackAddress(keyword, MAX_RESULTS)
    }

    private fun getFallbackAddress(`object`: Any, maxResults: Int) {
        disposables.add(getFallbackObservable(`object`, maxResults)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<List<Address>>() {
                    override fun onNext(addresses: List<Address>) {
                        if (addresses.isEmpty()) {
                            Log.e("ReverseGeocoding", "Reverse geocoding addresses is empty")
                            return
                        }
                        listener.onAddresses(addresses)
                    }

                    override fun onError(e: Throwable) {
                        listener.onNoAddressFound(e)
                        sendError("Google API Fallback failed -> Can't get address", e)
                    }

                    override fun onComplete() {

                    }
                }))
    }

    private fun sendError(message: String, e: Throwable) {
        Log.e("ReverseGeocoding", message + " " + e.message)
    }

    private fun getFallbackObservable(`object`: Any, maxResults: Int): Observable<List<Address>> {
        return when (`object`) {
            is String -> getFallbackObservable(`object`, maxResults)
            is Location -> getFallbackObservable(`object`, maxResults)
            else -> throw IllegalArgumentException("Object must be either a string or a Location")
        }
    }

    private fun getFallbackObservable(keyword: String, maxResults: Int)
            : Observable<List<Address>> {
        return getFallbackReverseGeocodeObservable(keyword, maxResults)
    }

    private fun getFallbackObservable(location: Location, maxResults: Int)
            : Observable<List<Address>> {
        return getFallbackReverseGeocodeObservable(
                location.latitude,
                location.longitude,
                maxResults)
    }

    /**
     * Creates observable that translates latitude and longitude to list of possible addresses using
     * included Geocoder class. In case geocoder fails with IOException("Service not Available")
     * fallback
     * decoder is used using google web api. You should subscribe for this observable on I/O thread.
     * The stream finishes after address list is available.
     *
     * @param lat        latitude
     * @param lng        longitude
     * @param maxResults maximal number of results you are interested in
     * @return observable that serves list of address based on location
     */
    private fun getReverseGeocodeObservable(lat: Double, lng: Double, maxResults: Int)
            : Observable<List<Address>> {
        return ReverseGeocodeObservable.createObservable(context, Locale.FRANCE, lat, lng,
                maxResults)
    }

    private fun getFallbackReverseGeocodeObservable(lat: Double, lng: Double,
                                                    maxResults: Int): Observable<List<Address>> {
        return FallbackLocationObservable.createObservable(context, Locale.FRANCE, lat,
                lng, maxResults)
    }

    private fun getFallbackReverseGeocodeObservable(keyword: String, maxResults: Int)
            : Observable<List<Address>> {
        return FallbackKeywordObservable.createObservable(context, Locale.FRANCE,
                keyword, maxResults)
    }

    companion object {
        private val MAX_RESULTS = 10
    }
}