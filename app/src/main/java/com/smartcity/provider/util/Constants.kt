package com.smartcity.provider.util

class Constants {

    companion object{

        const val BASE_URL = "https://smart-city-backend.herokuapp.com/api/"
        const val PRODUCT_IMAGE_URL=BASE_URL+"product/image/"

        const val PASSWORD_RESET_URL: String = "https://open-api.xyz/password_reset/"

        const val LOCAL_STORAGE_DIRECTORY="ImagePicker"

        const val NETWORK_TIMEOUT = 30000L
        const val CACHE_TIMEOUT = 2000L
        const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing

        const val PAGINATION_PAGE_SIZE = 10

        const val GALLERY_REQUEST_CODE = 201
        const val PERMISSIONS_REQUEST_READ_STORAGE: Int = 301
        const val PERMISSIONS_REQUEST_FINE_LOCATION: Int = 601
        const val PERMISSIONS_REQUEST_CAMERA: Int = 701
        const val PERMISSIONS_REQUEST_SYSTEM_ALERT_WINDOW: Int = 501
        const val CROP_IMAGE_INTENT_CODE: Int = 401

        const val DOLLAR="$"
    }
}