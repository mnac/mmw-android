package com.mmw.data.source.remote.Location

import android.content.Context
import android.location.Address
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.annotations.NonNull
import java.io.IOException
import java.net.URL
import java.util.*


/**
 * Created by Mathias on 26/08/2017.
 *
 */
class FallbackKeywordObservable(private val context: Context, locale: Locale,
                                private val keyword: String, maxResults: Int)
    : FallbackReverseGeocodeObservable(locale, maxResults) {

    @Throws(Exception::class)
    override fun subscribe(@NonNull e: ObservableEmitter<List<Address>>) {
        try {
            val addresses = alternativeReverseGeocodeQuery()

            if (addresses == null || addresses.isEmpty()) {
                e.onError(Throwable("No address found"))
            } else {
                e.onNext(addresses)
                e.onComplete()
            }
        } catch (ex: Exception) {
            e.onError(ex)
        }
    }

    override val fallbackUrl: URL @Throws(IOException::class)
    get() = URL("https://maps.googleapis.com/maps/api/geocode/json" +
                "?address=" + keyword + "" +
                "&amp;sensor=true&amp;language=" + locale.language + "" +
                "&amp;key=" + googleFallbackApiKey)

    companion object {
        fun createObservable(context: Context, locale: Locale, keyword: String, maxResults: Int)
                : Observable<List<Address>> {
            return Observable.create(FallbackKeywordObservable(context, locale, keyword, maxResults)
            )
        }
    }
}