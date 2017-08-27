package com.mmw.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Mathias on 24/08/2017.
 *
 */
data class OAuthCredentials @JvmOverloads constructor(
        @SerializedName("user_id") val userId: String = "",
        @SerializedName("access_token") val accessToken: String = "",
        @SerializedName("token_type")val tokenType: String = "")