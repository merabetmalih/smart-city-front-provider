package com.smartcity.provider

import android.app.Application
import com.smartcity.provider.di.AppComponent
import com.smartcity.provider.di.DaggerAppComponent
import com.smartcity.provider.di.auth.AuthComponent
import com.smartcity.provider.di.config.ConfigComponent
import com.smartcity.provider.di.main.MainComponent

class BaseApplication : Application(){

    lateinit var appComponent: AppComponent

    private var authComponent: AuthComponent? = null

    private var configComponent: ConfigComponent? = null

    private var mainComponent: MainComponent? = null

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    fun releaseMainComponent(){
        mainComponent = null
    }

    fun mainComponent(): MainComponent {
        if(mainComponent == null){
            mainComponent = appComponent.mainComponent().create()
        }
        return mainComponent as MainComponent
    }

    fun releaseAuthComponent(){
        authComponent = null
    }

    fun authComponent(): AuthComponent {
        if(authComponent == null){
            authComponent = appComponent.authComponent().create()
        }
        return authComponent as AuthComponent
    }

    fun releaseConfigComponent(){
        configComponent = null
    }

    fun configComponent(): ConfigComponent {
        if(configComponent == null){
            configComponent = appComponent.configComponent().create()
        }
        return configComponent as ConfigComponent
    }

    fun initAppComponent(){
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
    }
}