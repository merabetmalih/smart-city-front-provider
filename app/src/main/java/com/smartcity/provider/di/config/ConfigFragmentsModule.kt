package com.smartcity.provider.di.config

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.provider.fragments.config.ConfigFragmentFactory
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object ConfigFragmentsModule {
    @JvmStatic
    @ConfigScope
    @Provides
    @Named("ConfigFragmentFactory")
    fun provideFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        requestManager: RequestManager
    ): FragmentFactory {
        return ConfigFragmentFactory(
            viewModelFactory,
            requestManager
        )
    }
}