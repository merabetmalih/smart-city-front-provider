package com.smartcity.provider.ui.main.account.discount.addProductDiscount

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.models.product.ProductVariants
import com.smartcity.provider.ui.main.account.BaseAccountFragment
import com.smartcity.provider.ui.main.account.discount.addProductDiscount.adapters.ProductVariantAdapter
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.*
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_select_variant_discount.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class SelectVariantDiscountFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_select_variant_discount,viewModelFactory),
    ProductVariantAdapter.Interaction{

    private lateinit var recyclerAdapter: ProductVariantAdapter

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            ACCOUNT_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        uiCommunicationListener.expandAppBar()
        uiCommunicationListener.displayBottomNavigation(false)

        initRecyclerView()
        submitList()
    }

    private fun submitList() {
        viewModel.getSelectedProductDiscount()
        recyclerAdapter.submitList(
            viewModel.getSelectedProductToSelectVariant()!!.productVariants,
            viewModel.getSelectedProductDiscount()
                .find { it.id == viewModel.getSelectedProductToSelectVariant()!!.id}
        )
    }

    private fun initRecyclerView() {
        selected_variants_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@SelectVariantDiscountFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter =
                ProductVariantAdapter(
                    requestManager,
                    this@SelectVariantDiscountFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = recyclerAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearSelectedProductToSelectVariant()
    }

    override fun onItemSelected(item: ProductVariants) {
        val product=viewModel.getSelectedProductDiscount().find { it.id == viewModel.getSelectedProductToSelectVariant()!!.id }
        val list=viewModel.getSelectedProductDiscount().toMutableList()

        if (product==null){
            val currentProduct=viewModel.getSelectedProductToSelectVariant()!!.copy()
            currentProduct.productVariants= listOf(item)
            list.add(currentProduct)
            viewModel.setSelectedProductDiscount(list)
        }else{
            list.remove(product)

            val listt=product.productVariants.toMutableList()
            listt.add(item)
            product.productVariants=listt

            list.add(product)
            viewModel.setSelectedProductDiscount(list)
        }
    }

    override fun onItemDeSelected(item: ProductVariants) {
        val product=viewModel.getSelectedProductDiscount().find { it.id == viewModel.getSelectedProductToSelectVariant()!!.id }!!.copy()

        val variantsOfSelected=product.productVariants.toMutableList()
        variantsOfSelected.remove(item)

        val list=viewModel.getSelectedProductDiscount().toMutableList()

        if(variantsOfSelected.isEmpty()){
            list.remove(
                viewModel.getSelectedProductDiscount().find { it.id == viewModel.getSelectedProductToSelectVariant()!!.id }!!
            )
            viewModel.setSelectedProductDiscount(list)
        }else{
            list.remove(
                viewModel.getSelectedProductDiscount().find { it.id == viewModel.getSelectedProductToSelectVariant()!!.id }!!
            )
            product.productVariants=variantsOfSelected
            list.add(product)
            viewModel.setSelectedProductDiscount(list)

        }
    }
}