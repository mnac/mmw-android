package com.mmw.data.source.remote.Location

import android.location.Address
import org.json.JSONException
import org.json.JSONObject
import java.util.*


/**
 * Created by Mathias on 26/08/2017.
 *
 */
object UtilsGeoCoding {

    val SUCCESS_RESULT = 0
    val FAILURE_RESULT = 1

    val PACKAGE_NAME = "com.stootie"
    val RECEIVER_PARAM = PACKAGE_NAME + ".RECEIVER"
    val RESULT_ADDRESSES_PARAM = "addresses"
    val RESULT_ERROR_PARAM = "error"
    val NAME_PARAM = "name"
    val KEY_PARAM = "key"

    val DEFAULT_FRANCE_LAT = 46.52863469527167
    val DEFAULT_FRANCE_LNG = 2.43896484375

    fun getAddressLine(address: Address?): String {
        if (address != null) {
            val addressLineBuilder = StringBuilder()
            if (address.thoroughfare != null) {
                addressLineBuilder.append(address.thoroughfare)

                if (address.locality != null) {
                    if (addressLineBuilder.isNotEmpty()) addressLineBuilder.append(", ")
                    addressLineBuilder.append(address.locality)
                }

                // it is possible that locality returned is postal code
                // concatenate postal code only if it is NOT the case
                if (address.postalCode != null && address.locality != null
                        && address.locality != address.postalCode) {
                    if (addressLineBuilder.isNotEmpty()) addressLineBuilder.append(", ")
                    addressLineBuilder.append(address.postalCode)
                }
            } else {
                // some phone do not split address - in this case,
                // build first address line from multiple address lines
                if (address.getAddressLine(0) != null) {
                    //remove numbers at the beginning
                    var countNumeric = 0
                    run {
                        var i = 0
                        val len = address.getAddressLine(0).length
                        while (i < len) {
                            if (Character.isDigit(address.getAddressLine(0)[i])) {
                                countNumeric++
                            } else {
                                break
                            }
                            i++
                        }
                    }
                    addressLineBuilder.append(address.getAddressLine(0))
                    addressLineBuilder.delete(0, countNumeric)
                    trimSubstring(addressLineBuilder)

                    // remove bis, ter, and quater from beginning
                    if (addressLineBuilder.length > 5 &&
                            (addressLineBuilder.substring(0, 4) == "bis "
                            || addressLineBuilder.substring(0, 4) == "ter ")) {
                        addressLineBuilder.delete(0, 4)
                    }

                    if (addressLineBuilder.length > 8
                            && addressLineBuilder.substring(0, 7) == "quater ") {
                        addressLineBuilder.delete(0, 7)
                    }

                    trimSubstring(addressLineBuilder)

                    // remove if only one letter from beginning
                    if (addressLineBuilder.length > 2
                            && addressLineBuilder.substring(0, 2).contains(" ")) {
                        addressLineBuilder.delete(0, 2)
                    }

                    // remove "-numeric" from beginning"
                    if (addressLineBuilder.length > 2 && addressLineBuilder.substring(0, 1)
                            .contains("-")) {
                        var countDash = 1
                        val addressTemp = addressLineBuilder.toString()
                        var i = 1
                        val len = addressTemp.length
                        while (i < len) {
                            if (Character.isDigit(addressTemp[i])) {
                                countDash++
                            } else {
                                break
                            }
                            i++
                        }
                        addressLineBuilder.delete(0, countDash)
                        trimSubstring(addressLineBuilder)
                    }
                }

                if (address.getAddressLine(1) != null) {
                    if (addressLineBuilder.isNotEmpty()) addressLineBuilder.append(", ")
                    addressLineBuilder.append(address.getAddressLine(1))
                }
            }
            return addressLineBuilder.toString()
        } else {
            return ""
        }
    }

    private fun trimSubstring(sb: StringBuilder) {
        var first = 0
        var last: Int = sb.length

        while (first < sb.length) {
            if (!Character.isWhitespace(sb[first])) break
            first++
        }

        while (last > first) {
            if (!Character.isWhitespace(sb[last - 1])) break
            last--
        }

        sb.delete(last, sb.length)
        sb.delete(0, first)
    }

    @Throws(JSONException::class)
    fun parseAddress(sourceResult: JSONObject): Address? {
        // get formatted address
        var formattedAddress: String? = null
        var street: String? = null
        var city: String? = null
        var country: String? = null
        var postalCode: String? = null

        if (sourceResult.has("address_components")) {
            val addressComponents = sourceResult.getJSONArray("address_components")
            if (addressComponents != null) {
                for (i in 0 until addressComponents.length()) {
                    val component = addressComponents.getJSONObject(i)
                    if (component.has("types")) {
                        val types = component.getJSONArray("types")
                        (0 until types.length())
                                .map { types.get(it) as String }
                                .forEach {
                                    when (it) {
                                        "route" -> street = component.getString("long_name")
                                        "locality" -> city = component.getString("long_name")
                                        "country" -> country = component.getString("long_name")
                                        "postal_code" -> postalCode = component.getString("long_name")
                                        else -> {
                                        }
                                    }
                                }
                    }
                }
            }

            if (sourceResult.has("formatted_address"))
                formattedAddress = sourceResult.getString("formatted_address")

            // get latitude/longitude
            var latitude = 0.0
            var longitude = 0.0
            if (sourceResult.has("geometry")) {
                val jsonGeometryObject = sourceResult.getJSONObject("geometry")
                if (jsonGeometryObject.has("location")) {
                    val jsonLocationObject = jsonGeometryObject.getJSONObject("location")
                    if (jsonLocationObject.has("lat")) {
                        latitude = jsonLocationObject.getDouble("lat")
                    }
                    if (jsonLocationObject.has("lng")) {
                        longitude = jsonLocationObject.getDouble("lng")
                    }
                }
            }

            val address = Address(Locale.getDefault())
            address.latitude = latitude
            address.longitude = longitude
            address.thoroughfare = street
            address.countryName = country

            // sometimes, city is not available (put postal code in this case)
            if (city != null)
                address.locality = city
            else if (postalCode != null) address.locality = postalCode

            address.postalCode = postalCode
            address.setAddressLine(0, formattedAddress)
            return address
        }
        return null
    }
}