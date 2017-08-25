package com.mmw.data.source.remote.api

import com.mmw.data.model.OAuthCredentials
import com.mmw.data.model.User
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

/**
 * Created by Mathias on 24/08/2017.
 *
 */
interface UserService {

    @POST("/user/register")
    fun register(@Body user: User): Observable<Response<Void>>

    @POST("/connect")
    fun connect(@Header("Authorization") basicToken: String, @Body body: HashMap<String, String>):
            Observable<OAuthCredentials>

    @GET("/user/{userId}")
    fun get(@Path("userId") userId: String): Observable<User>

    companion object factory {
        fun create(client: Retrofit): UserService {
            return client.create(UserService::class.java)
        }
    }
}