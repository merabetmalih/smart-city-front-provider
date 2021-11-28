package com.smartcity.provider.ui

import androidx.annotation.ColorRes
import com.smartcity.provider.util.Response
import com.smartcity.provider.util.StateMessageCallback

interface UICommunicationListener {

    fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    )

    fun displayProgressBar(isLoading: Boolean)

    fun expandAppBar()

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean

    fun displayBottomNavigation(bool: Boolean)

    fun isFineLocationPermissionGranted(): Boolean

    fun isCameraPermissionGranted(): Boolean

    fun updateStatusBarColor(@ColorRes statusBarColor: Int, statusBarTextColor:Boolean)
}