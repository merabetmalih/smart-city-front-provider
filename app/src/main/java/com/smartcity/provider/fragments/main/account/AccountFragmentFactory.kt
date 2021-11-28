package com.smartcity.provider.fragments.main.account

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.provider.di.main.MainScope
import com.smartcity.provider.ui.main.account.*
import com.smartcity.provider.ui.main.account.discount.addProductDiscount.AddProductDiscountFragment
import com.smartcity.provider.ui.main.account.discount.addProductDiscount.CustomCategoryDiscountFragment
import com.smartcity.provider.ui.main.account.discount.addProductDiscount.ProductDiscountFragment
import com.smartcity.provider.ui.main.account.discount.addProductDiscount.SelectVariantDiscountFragment
import com.smartcity.provider.ui.main.account.discount.createDiscount.AddDiscountFragment
import com.smartcity.provider.ui.main.account.discount.discount.DiscountFragment
import com.smartcity.provider.ui.main.account.discount.discount.ViewOfferFragment
import com.smartcity.provider.ui.main.account.discount.pickDate.PickDateDiscountFragment
import com.smartcity.provider.ui.main.account.flashdeals.CreateFlashDealFragment
import com.smartcity.provider.ui.main.account.flashdeals.FlashDealsFragment
import com.smartcity.provider.ui.main.account.flashdeals.SearchFlashDealFragment
import com.smartcity.provider.ui.main.account.information.CategoryFragment
import com.smartcity.provider.ui.main.account.information.CategoryValueFragment
import com.smartcity.provider.ui.main.account.information.InformationFragment
import com.smartcity.provider.ui.main.account.notification.NotificationFragment
import com.smartcity.provider.ui.main.account.policy.PolicyFormFragment
import com.smartcity.provider.ui.main.account.policy.PolicyFormOptionFragment
import com.smartcity.provider.ui.main.account.policy.PolicyFragment
import javax.inject.Inject

@MainScope
class AccountFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            AccountFragment::class.java.name -> {
                AccountFragment(
                    viewModelFactory,
                    requestManager)
            }

            NotificationFragment::class.java.name -> {
                NotificationFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            PolicyFragment::class.java.name -> {
                PolicyFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            PolicyFormFragment::class.java.name -> {
                PolicyFormFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            PolicyFormOptionFragment::class.java.name -> {
                PolicyFormOptionFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            InformationFragment::class.java.name -> {
                InformationFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            DiscountFragment::class.java.name -> {
                DiscountFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            AddDiscountFragment::class.java.name -> {
                AddDiscountFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            AddProductDiscountFragment::class.java.name -> {
                AddProductDiscountFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            CustomCategoryDiscountFragment::class.java.name -> {
                CustomCategoryDiscountFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            ProductDiscountFragment::class.java.name -> {
                ProductDiscountFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            SelectVariantDiscountFragment::class.java.name -> {
                SelectVariantDiscountFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            PickDateDiscountFragment::class.java.name -> {
                PickDateDiscountFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            ViewOfferFragment::class.java.name -> {
                ViewOfferFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            CategoryFragment::class.java.name -> {
                CategoryFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            CategoryValueFragment::class.java.name -> {
                CategoryValueFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            FlashDealsFragment::class.java.name -> {
                FlashDealsFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            CreateFlashDealFragment::class.java.name -> {
                CreateFlashDealFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            SearchFlashDealFragment::class.java.name -> {
                SearchFlashDealFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            else -> {
                AccountFragment(
                    viewModelFactory,
                    requestManager)
            }
        }
}