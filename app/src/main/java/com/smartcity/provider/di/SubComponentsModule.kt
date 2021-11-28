package com.smartcity.provider.di

import com.smartcity.provider.di.auth.AuthComponent
import com.smartcity.provider.di.config.ConfigComponent
import com.smartcity.provider.di.main.MainComponent
import dagger.Module

@Module(
    subcomponents = [
        AuthComponent::class,
        ConfigComponent::class,
        MainComponent::class
    ]
)
class SubComponentsModule