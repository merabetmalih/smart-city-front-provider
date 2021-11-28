package com.smartcity.provider.ui.main.account.discount.addProductDiscount

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.ui.main.account.BaseAccountFragment
import com.smartcity.provider.ui.main.account.discount.addProductDiscount.adapters.ProductDiscountAdapter
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountStateEvent
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.*
import com.smartcity.provider.util.StateMessageCallback
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_product_discount.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class ProductDiscountFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_product_discount,viewModelFactory),
    ProductDiscountAdapter.Interaction{

    private lateinit var productRecyclerAdapter: ProductDiscountAdapter

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

        getProducts()
        initProductRecyclerView()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

                uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object: StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer { jobCounter ->//must
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })

        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            productRecyclerAdapter.submitList(viewModel.getProductList(),viewModel.getSelectedProductDiscount())

        })
    }

    private fun initProductRecyclerView() {
        product_discount_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@ProductDiscountFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productRecyclerAdapter =
                ProductDiscountAdapter(
                    requestManager,
                    this@ProductDiscountFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = productRecyclerAdapter
        }
    }

    private fun getProducts() {
        viewModel.setStateEvent(
            AccountStateEvent.GetProductsEvent(
                viewModel.getSelectedCustomCategory()!!.pk.toLong()
            )
        )
    }

    override fun onItemSelected(item: Product) {
        val list=viewModel.getSelectedProductDiscount().toMutableList()
        val product=list.find { it.id ==  item.id}
        list.remove(product)
        list.add(item)
        viewModel.setSelectedProductDiscount(list)
    }

    override fun onItemDeSelected(item: Product) {
        val list=viewModel.getSelectedProductDiscount().toMutableList()
        val product=list.find { it.id ==  item.id}
        list.remove(product)
        viewModel.setSelectedProductDiscount(list)
    }

    override fun onItemSelectedNavigate(item: Product) {
        viewModel.setSelectedProductToSelectVariant(item)
        navSelectVariant()
    }

    private fun navSelectVariant() {
        findNavController().navigate(R.id.action_productDiscountFragment_to_selectVariantDiscountFragment)
    }
}