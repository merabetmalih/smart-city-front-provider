package com.smartcity.provider.fragments.config

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.provider.di.config.ConfigScope
import com.smartcity.provider.ui.config.CategoryConfigFragment
import com.smartcity.provider.ui.config.CategoryValueConfigFragment
import com.smartcity.provider.ui.config.ChooseServiceConfigFragment
import com.smartcity.provider.ui.config.CreateStoreConfigFragment
import javax.inject.Inject

@ConfigScope
class ConfigFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            ChooseServiceConfigFragment::class.java.name -> {
                ChooseServiceConfigFragment(viewModelFactory)
            }

            CreateStoreConfigFragment::class.java.name -> {
                CreateStoreConfigFragment(viewModelFactory,requestManager)
            }

            CategoryConfigFragment::class.java.name -> {
                CategoryConfigFragment(viewModelFactory)
            }

            CategoryValueConfigFragment::class.java.name -> {
                CategoryValueConfigFragment(viewModelFactory)
            }

            else -> {
                ChooseServiceConfigFragment(viewModelFactory)
            }
        }
}