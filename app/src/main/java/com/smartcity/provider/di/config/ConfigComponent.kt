package com.smartcity.provider.di.config

import com.smartcity.provider.ui.config.ConfigActivity
import dagger.Subcomponent

@ConfigScope
@Subcomponent(
    modules = [
        ConfigModule::class,
        ConfigViewModelModule::class,
        ConfigFragmentsModule::class
    ])
interface ConfigComponent {

    @Subcomponent.Factory
    interface Factory{

        fun create(): ConfigComponent
    }

    fun inject(ConfigActivity: ConfigActivity)
}