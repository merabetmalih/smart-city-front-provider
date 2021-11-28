package com.smartcity.provider.di

import android.app.Application
import com.smartcity.provider.di.auth.AuthComponent
import com.smartcity.provider.di.config.ConfigComponent
import com.smartcity.provider.di.main.MainComponent
import com.smartcity.provider.session.SessionManager
import com.smartcity.provider.ui.BaseActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        SubComponentsModule::class
    ]
)
interface AppComponent  {

    val sessionManager: SessionManager

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(baseActivity: BaseActivity)

    fun authComponent(): AuthComponent.Factory

    fun configComponent(): ConfigComponent.Factory

    fun mainComponent(): MainComponent.Factory
}








