package com.mmw.data.model

/**
 * Created by Mathias on 24/08/2017.
 *
 */
data class OAuthCredentials @JvmOverloads constructor(
        val userId: String = "",
        val accessToken: String = "",
        val tokenType: String = "")