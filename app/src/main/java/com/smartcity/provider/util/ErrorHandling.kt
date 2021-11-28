package com.smartcity.provider.util

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class ErrorHandling{

    companion object{

        const val INVALID_PAGE = "Invalid page."

        const val INVALID_CREDENTIALS = "Invalid credentials"
        const val SOMETHING_WRONG_WITH_IMAGE = "Something went wrong with the image."
        const val INVALID_STATE_EVENT = "Invalid state event"
        const val CANNOT_BE_UNDONE = "This can't be undone."
        const val NETWORK_ERROR = "Network error"
        const val NETWORK_ERROR_TIMEOUT = "Network timeout"
        const val CACHE_ERROR_TIMEOUT = "Cache timeout"
        const val UNKNOWN_ERROR = "Unknown error"


        private val TAG: String = "AppDebug"

        const val UNABLE_TO_RESOLVE_HOST = "Unable to resolve host"
        const val UNABLE_TODO_OPERATION_WO_INTERNET = "Can't do that operation without an internet connection"

        const val ERROR_SAVE_ACCOUNT_PROPERTIES = "Error saving account properties.\nTry restarting the app."
        const val ERROR_SAVE_AUTH_TOKEN = "Error saving authentication token.\nTry restarting the app."
        const val ERROR_SOMETHING_WRONG_WITH_IMAGE = "Something went wrong with the image."
        const val ERROR_MUST_SELECT_IMAGE = "You must select an image."
        const val ERROR_INVALID_PHONE_NUMBER_FORMAT = "Invalid phone number format."

        const val ERROR_MUST_SELECT_TWO_DATES = "You must select two dates."

        const val GENERIC_AUTH_ERROR = "Error"
        const val PAGINATION_DONE_ERROR = "Invalid page."
        const val ERROR_CHECK_NETWORK_CONNECTION = "Check network connection."
        const val ERROR_UNKNOWN = "Unknown error"

        const val ERROR_FILL_ALL_INFORMATION = "You must fill all information."

        fun isNetworkError(msg: String): Boolean{
            when{
                msg.contains(UNABLE_TO_RESOLVE_HOST) -> return true
                else-> return false
            }
        }

        fun parseDetailJsonResponse(rawJson: String?): String{
            Log.d(TAG, "parseDetailJsonResponse: ${rawJson}")
            try{
                if(!rawJson.isNullOrBlank()){
                    if(rawJson.equals(ERROR_CHECK_NETWORK_CONNECTION)){
                        return PAGINATION_DONE_ERROR
                    }
                    return JSONObject(rawJson).get("detail") as String
                }
            }catch (e: JSONException){
                Log.e(TAG, "parseDetailJsonResponse: ${e.message}")
            }
            return ""
        }

        fun isPaginationDone(errorResponse: String?): Boolean{
            // if error response = '{"detail":"Invalid page."}' then pagination is finished
            return PAGINATION_DONE_ERROR.equals(parseDetailJsonResponse(errorResponse))
        }
    }

}