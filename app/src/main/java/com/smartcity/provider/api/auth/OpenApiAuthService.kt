package com.smartcity.provider.api.auth

import com.smartcity.provider.api.auth.network_responses.LoginResponse
import com.smartcity.provider.api.auth.network_responses.RegistrationResponse
import com.smartcity.provider.di.auth.AuthScope
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

@AuthScope
interface OpenApiAuthService {

    @POST("provider/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("provider/register")
    @FormUrlEncoded
    suspend fun register(
        @Field("email") email: String,
        @Field("userName") username: String,
        @Field("passWord") password: String,
        @Field("passWord2") password2: String
    ): RegistrationResponse
}
