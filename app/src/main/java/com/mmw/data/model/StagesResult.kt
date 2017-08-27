package com.mmw.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Mathias on 27/08/2017.
 *
 */
data class StagesResult constructor(@SerializedName("Items")val Stages: ArrayList<Stage>, val Count: Int, val ScannedCount: Int, val LastEvaluatedKey: PaginationKeyStages)