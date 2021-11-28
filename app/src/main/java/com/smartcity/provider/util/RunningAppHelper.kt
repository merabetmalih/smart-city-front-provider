package com.smartcity.provider.util

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context


class RunningAppHelper {
    companion object{
        fun isAppIsInBackground(context: Context): Boolean {
            var isInBackground = true
            val am =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningProcesses =
                am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
            return isInBackground
        }
    }
}