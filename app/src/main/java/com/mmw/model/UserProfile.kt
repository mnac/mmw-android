package com.mmw.model

/**
 * Created by Mathias on 02/09/2017.
 *
 */
data class UserProfile constructor(
        val user: User?,
        val trips: ArrayList<Trip>)