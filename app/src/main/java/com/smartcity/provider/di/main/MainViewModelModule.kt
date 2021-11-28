package com.smartcity.provider.di.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smartcity.provider.di.auth.keys.MainViewModelKey
import com.smartcity.provider.ui.main.account.viewmodel.AccountViewModel
import com.smartcity.provider.ui.main.store.viewmodel.StoreViewModel
import com.smartcity.provider.ui.main.order.viewmodel.OrderViewModel
import com.smartcity.provider.ui.main.custom_category.viewmodel.CustomCategoryViewModel

import com.smartcity.provider.viewmodels.MainViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: MainViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @MainViewModelKey(StoreViewModel::class)
    abstract fun bindStoreViewModel(storeViewModel: StoreViewModel): ViewModel

    @Binds
    @IntoMap
    @MainViewModelKey(OrderViewModel::class)
    abstract fun bindBlogViewModel(orderViewModel: OrderViewModel): ViewModel

    @Binds
    @IntoMap
    @MainViewModelKey(CustomCategoryViewModel::class)
    abstract fun bindCreateBlogViewModel(customCategoryViewModel: CustomCategoryViewModel): ViewModel

    @Binds
    @IntoMap
    @MainViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel
}








