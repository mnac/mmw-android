package com.mmw.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Mathias on 27/08/2017.
 *
 */
data class Timeline constructor(@SerializedName("Items")val Trips: ArrayList<Trip>, val Count: Int, val ScannedCount: Int, val LastEvaluatedKey: PaginationKeyTimeline)