package com.smartcity.provider.di.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smartcity.provider.di.config.keys.ConfigViewModelKey
import com.smartcity.provider.ui.config.viewmodel.ConfigViewModel
import com.smartcity.provider.viewmodels.ConfigViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ConfigViewModelModule {

    @ConfigScope
    @Binds
    abstract fun bindViewModelFactory(factory: ConfigViewModelFactory): ViewModelProvider.Factory

    @ConfigScope
    @Binds
    @IntoMap
    @ConfigViewModelKey(ConfigViewModel::class)
    abstract fun bindConfigViewModel(configViewModel: ConfigViewModel): ViewModel
}