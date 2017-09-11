package com.mmw.data.source.remote.Location

import android.location.Address
import com.mmw.AppConstant
import io.reactivex.ObservableOnSubscribe
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


/**
 * Created by Mathias on 26/08/2017.
 *
 */
abstract class FallbackReverseGeocodeObservable(protected var locale: Locale,
                                                         private var maxResults: Int) :
        ObservableOnSubscribe<List<Address>> {
    protected var googleFallbackApiKey: String = AppConstant.LOCATION_FALLBACK_KEY

    /**
     * This function fetches a list of addresses for the set latitude, longitude and maxResults
     * properties from the
     * Google Geocode API (http://maps.googleapis.com/maps/api/geocode).
     *
     * @return List of addresses
     * @throws JSONException In case of problems while parsing the json response from google
     * geocode API servers
     */
    protected fun alternativeReverseGeocodeQuery(): List<Address>? {
        try {
            val addresses = ArrayList<Address>()

            val url = fallbackUrl

            val urlConnection = url.openConnection() as HttpURLConnection

            val stringBuilder = StringBuilder()

            val reader = BufferedReader(InputStreamReader(urlConnection.inputStream, "UTF-8"))

            var line: String?
            var next = true
            while (next) {
                line = reader.readLine()
                next = line != null
                stringBuilder.append(line)
            }

            val jsonRootObject = JSONObject(stringBuilder.toString())

            // No results status
            if ("ZERO_RESULTS".equals(jsonRootObject.getString("status"), ignoreCase = true)) {
                return Collections.emptyList()
            }

            // Other non-OK responses status
            if (!"OK".equals(jsonRootObject.getString("status"), ignoreCase = true)) {
                throw RuntimeException("Wrong API response")
            }

            val results = jsonRootObject.getJSONArray("results")
            if (results != null) {
                var i = 0
                while (i < results.length() && i < maxResults) {
                    addresses.add(UtilsGeoCoding.parseAddress(results.getJSONObject(0))!!)
                    i++
                }
            }

            urlConnection.disconnect()
            return Collections.unmodifiableList(addresses)
        } catch (e: Exception) {
            return null
        }

    }

    protected abstract val fallbackUrl: URL
}