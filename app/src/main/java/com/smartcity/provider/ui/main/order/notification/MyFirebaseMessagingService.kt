package com.smartcity.provider.ui.main.order.notification

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.smartcity.provider.util.NotificationSettings
import com.smartcity.provider.util.NotificationSettings.Companion.ORDERS_NOTIFICATION
import com.smartcity.provider.util.NotificationSettings.Companion.SOUND_NOTIFICATION
import com.smartcity.provider.util.NotificationSettings.Companion.VIBRATION_NOTIFICATION
import com.smartcity.provider.util.PreferenceKeys
import com.smartcity.provider.util.RunningAppHelper.Companion.isAppIsInBackground


object Events {
    val serviceEvent: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}

class MyFirebaseMessagingService : FirebaseMessagingService() {
    val TAG = "AppDebug"
    private var notificationUtils: NotificationUtils? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("FCM Token", token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Events.serviceEvent.postValue("new")


            handleCustomDataMessage(
                title = remoteMessage.data.get("title")!!,
                message = remoteMessage.data.get("message")!!
            )

    }

    private fun handleCustomDataMessage(title: String, message: String) {
        val preferences = baseContext.getSharedPreferences(
            PreferenceKeys.APP_PREFERENCES,
            Context.MODE_PRIVATE
        )
        val settings=preferences.getStringSet(PreferenceKeys.NOTIFICATION_SETTINGS,null)
        settings?.let {
            if(ORDERS_NOTIFICATION in it){

                showNotificationMessage(
                    this,
                    title,
                    message,
                    SOUND_NOTIFICATION in it,
                    VIBRATION_NOTIFICATION in it
                )

                if(NotificationSettings.REMINDER_NOTIFICATION in it){
                    startReminder()
                }
            }
        }

    }

    private fun startReminder(){
        if(isAppIsInBackground(baseContext)){
            val serviceIntent = Intent(this, NotificationAlarmService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }
    }

    private fun showNotificationMessage(
        context: Context,
        title: String,
        message: String,
        shouldSound:Boolean,
        shouldVibrate:Boolean
    ) {
        notificationUtils = NotificationUtils(context)
        notificationUtils?.showNotificationMessage(title, message,shouldSound,shouldVibrate,true)


    }
}