package com.smartcity.provider.di.main

import android.content.SharedPreferences
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.provider.fragments.main.account.AccountFragmentFactory
import com.smartcity.provider.fragments.main.store.StoreFragmentFactory
import com.smartcity.provider.fragments.main.order.OrderFragmentFactory
import com.smartcity.provider.fragments.main.custom_category.CustomCategoryFragmentFactory


import dagger.Module
import dagger.Provides
import javax.inject.Named
@Module
object MainFragmentsModule {

    @JvmStatic
    @MainScope
    @Provides
    @Named("StoreFragmentFactory")
    fun provideStoreFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        requestManager: RequestManager
    ): FragmentFactory {
        return StoreFragmentFactory(
            viewModelFactory,
            requestManager
        )
    }

    @JvmStatic
    @MainScope
    @Provides
    @Named("BlogFragmentFactory")
    fun provideBlogFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        requestManager: RequestManager
    ): FragmentFactory {
        return OrderFragmentFactory(
            viewModelFactory,
            requestManager
        )
    }

    @JvmStatic
    @MainScope
    @Provides
    @Named("CreateBlogFragmentFactory")
    fun provideCreateBlogFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        requestManager: RequestManager
    ): FragmentFactory {
        return CustomCategoryFragmentFactory(
            viewModelFactory,
            requestManager
        )
    }

    @JvmStatic
    @MainScope
    @Provides
    @Named("AccountFragmentFactory")
    fun provideAccountFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        requestManager: RequestManager
    ): FragmentFactory {
        return AccountFragmentFactory(
            viewModelFactory,
            requestManager
        )
    }

    @JvmStatic
    @MainScope
    @Provides
    @Named("GetSharedPreferences")
    fun provideSharedPreferences(
         sharedPreferences: SharedPreferences
    ): SharedPreferences {
        return sharedPreferences
    }
}