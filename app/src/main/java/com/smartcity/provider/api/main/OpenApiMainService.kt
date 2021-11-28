package com.smartcity.provider.api.main

import com.smartcity.provider.api.GenericResponse
import com.smartcity.provider.api.main.responses.*
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.models.*
import com.smartcity.provider.models.product.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

@MainScope
interface OpenApiMainService {

    @GET("store/customCategory/all/{id}")
    suspend fun getAllcustomCategory(@Path("id") id: Long?):ListCustomCategoryResponse

    @POST("store/customCategory/create")
    @FormUrlEncoded
    suspend fun createCustomCategory(
        @Field("provider") provider:Long,
        @Field("name") name:String
    ): CustomCategoryResponse

    @DELETE("store/customCategory/delete/{id}")
    suspend fun deleteCustomCategory(@Path("id") id: Long?):GenericResponse

    @PUT("store/customCategory/update")
    @FormUrlEncoded
    suspend fun updateCustomCategory(
        @Field("id") id:Long,
        @Field("name") name:String,
        @Field("provider") provider:Long
    ): CustomCategoryResponse

    @Multipart
    @POST("product/create")
    suspend fun createProduct(
        @Part("product")  product: RequestBody,
        @Part productImagesFile: List<MultipartBody.Part>,
        @Part variantesImagesFile : List<MultipartBody.Part>
    ): Product

    @Multipart
    @PUT("product/update")
    suspend fun updateProduct(
        @Part("product")  product: RequestBody,
        @Part productImagesFile: List<MultipartBody.Part>,
        @Part variantesImagesFile : List<MultipartBody.Part>
    ): Product

    @GET("product/all/category/{id}")
    suspend fun getAllProductByCategory(@Path("id") id: Long?):ListProductResponse

    @GET("product/all/provider/{id}")
    suspend fun getAllProduct(@Path("id") id: Long?):ListProductResponse


    @DELETE("product/delete/{id}")
    suspend fun deleteProduct(@Path("id") id: Long?):GenericResponse

    @GET("order/current-provider-orders")
    suspend fun getAllOrders(
        @Query("id") id: Long?,
        @Query("date") date: String?,
        @Query("amount") amount: String?,
        @Query("step") step: OrderStep?,
        @Query("type") type: String?
    ):ListOrderResponse

    @GET("order/current-provider-today-orders")
    suspend fun getTodayOrders(
        @Query("id") id: Long?,
        @Query("date") date: String?,
        @Query("amount") amount: String?,
        @Query("step") step: OrderStep?,
        @Query("type") type: String?
    ):ListOrderResponse

    @GET("order/current-provider-dates-orders")
    suspend fun getOrdersByDate(
        @Query("id") id: Long?,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
        @Query("date") date: String?,
        @Query("amount") amount: String?,
        @Query("step") step: OrderStep?,
        @Query("type") type: String?
    ):ListOrderResponse

    @POST("policy/create")
    suspend fun createPolicy(
        @Body policy: Policy
    ): GenericResponse

    @POST("store/Information")
    suspend fun setStoreInformation(
        @Body storeInformation: StoreInformation
    ): GenericResponse

    @GET("store/Information/{id}")
    suspend fun getStoreInformation(
        @Path(value = "id") id:Long
    ): StoreInformation

    @PUT("order/current-store/{id}/accept")
    suspend fun setOrderAccepted(
        @Path(value = "id") id:Long
    ): GenericResponse

    @PUT("order/current-store/{id}/reject")
    suspend fun setOrderRejected(
        @Path(value = "id") id:Long
    ): GenericResponse

    @PUT("order/current-store/{id}/ready")
    suspend fun setOrderReady(
        @Path(value = "id") id:Long
    ): GenericResponse

    @PUT("order/current-store/{id}/delivered")
    suspend fun setOrderDelivered(
        @Path(value = "id") id:Long,
        @Query("comment") comment:String?,
        @Query("date") date: String?
    ): GenericResponse

    @PUT("order/current-store/{id}/pickedUp")
    suspend fun setOrderPickedUp(
        @Path(value = "id") id:Long,
        @Query("comment") comment:String?,
        @Query("date") date: String?
    ): GenericResponse

    @POST("offer/create")
    suspend fun createOffer(
        @Body offer:Offer
    ): GenericResponse

    @PUT("offer/update")
    suspend fun updateOffer(
        @Body offer:Offer
    ): GenericResponse

    @GET("offer/current-provider-offers")
    suspend fun getAllOffers(
        @Query("id") id: Long?,
        @Query("status") status: OfferState?
    ):ListGenericResponse<Offer>

    @DELETE("offer/delete/{id}")
    suspend fun deleteOffer(
        @Path("id") id: Long?
    ):GenericResponse

    @GET("category")
    suspend fun getAllCategory(): ListGenericResponse<Category>

    @POST("flashDeal/create")
    suspend fun createFlashDeal(
        @Body flashDeal:FlashDeal
    ): GenericResponse

    @GET("flashDeal/current-provider-flash/{id}")
    suspend fun getFlashDeals(
        @Path("id") id: Long?
    ): ListGenericResponse<FlashDeal>

    @GET("flashDeal/current-provider-search-flash")
    suspend fun getSearchFlashDeals(
        @Query("id") id: Long?,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?
    ): ListGenericResponse<FlashDeal>

    @GET("order/current-provider-search-orders-id")
    suspend fun getOrderById(
        @Query("id") id: Long?,
        @Query("orderId") orderId: Long?
    ):ListOrderResponse

    @GET("order/current-provider-search-orders-receiver")
    suspend fun searchOrderByReceiver(
        @Query("id") id: Long?,
        @Query("firstName") firstName: String?,
        @Query("lastName") lastName: String?
    ):ListOrderResponse

    @GET("order/current-provider-search-orders-date")
    suspend fun searchOrderByDate(
        @Query("id") id: Long?,
        @Query("date") date: String?
    ):ListOrderResponse

    @PUT("order/current-store-note")
    suspend fun setOrderNote(
        @Query("id") id: Long?,
        @Query("note") note: String
    ):GenericResponse

    @GET("order/current-provider-past-orders")
    suspend fun getPastOrders(
        @Query("id") id: Long?
    ):ListOrderResponse

    @PUT("product/move-category")
    suspend fun updateProductsCustomCategory(
        @Query("products") products: List<Long>,
        @Query("category") category: Long
    ): GenericResponse
}









