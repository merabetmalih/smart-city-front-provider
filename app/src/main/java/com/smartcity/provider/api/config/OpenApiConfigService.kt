package com.smartcity.provider.api.config

import com.smartcity.provider.api.GenericResponse
import com.smartcity.provider.api.auth.network_responses.StoreResponse
import com.smartcity.provider.api.main.responses.ListGenericResponse
import com.smartcity.provider.di.config.ConfigScope
import com.smartcity.provider.models.Category
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

@ConfigScope
interface OpenApiConfigService {
    @Multipart
    @POST("store/create")
    suspend fun createStore(
        @Part("store")  store: RequestBody,
        @Part image: MultipartBody.Part?
    ): StoreResponse

    @GET("store/category")
    suspend fun getStoreCategories(
        @Query(value = "id") id : Long
    ): ListGenericResponse<Category>

    @POST("store/category")
    suspend fun setStoreCategories(
        @Query(value = "id") id : Long,
        @Query(value = "categories") categories : List<String>
    ): GenericResponse

    @GET("category")
    suspend fun getAllCategories(): ListGenericResponse<Category>
}