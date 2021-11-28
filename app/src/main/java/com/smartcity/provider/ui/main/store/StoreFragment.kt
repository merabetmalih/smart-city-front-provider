package com.smartcity.provider.ui.main.store

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.*
import com.bumptech.glide.RequestManager
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.smartcity.provider.R
import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.ui.main.store.adapters.ViewCustomCategoryAdapter
import com.smartcity.provider.ui.main.store.adapters.ViewCustomCategoryAdapter.Companion.getSelectedPositions
import com.smartcity.provider.ui.main.store.adapters.ViewCustomCategoryAdapter.Companion.setSelectedPositions
import com.smartcity.provider.ui.main.store.adapters.ViewProductAdapter
import com.smartcity.provider.ui.main.store.state.STORE_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.store.state.StoreStateEvent
import com.smartcity.provider.ui.main.store.state.StoreViewState
import com.smartcity.provider.ui.main.store.viewmodel.*
import com.smartcity.provider.util.*
import kotlinx.android.synthetic.main.fragment_store.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StoreFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseStoreFragment(R.layout.fragment_store,viewModelFactory),
    ViewCustomCategoryAdapter.Interaction,
    ViewCustomCategoryAdapter.InteractionAll,
    ViewProductAdapter.Interaction{

    private lateinit var customCategoryrecyclerAdapter: ViewCustomCategoryAdapter
    private lateinit var productRecyclerAdapter: ViewProductAdapter

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            STORE_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[STORE_VIEW_STATE_BUNDLE_KEY] as StoreViewState?)?.let { viewState ->
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
        uiCommunicationListener.displayBottomNavigation(true)

        initCustomCategoryRecyclerView()
        initProductRecyclerView()
        subscribeObservers()

        if(viewModel.getCustomCategoryRecyclerPosition()==0 && viewModel.getViewProductList().products.isNullOrEmpty()){
            CustomCategoryMain()
            AllProduct()
        }
    }

    private fun initProductRecyclerView() {
        view_product_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@StoreFragment.context,LinearLayoutManager.VERTICAL, false)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productRecyclerAdapter =
                ViewProductAdapter(
                    requestManager,
                    this@StoreFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                }
            })

            productRecyclerAdapter.stateRestorationPolicy= StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = productRecyclerAdapter

        }
        //productRecyclerAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    fun CustomCategoryMain(){
        viewModel.setStateEvent(StoreStateEvent.CustomCategoryMainEvent())
    }

    fun initCustomCategoryRecyclerView(){
        view_custom_category_recyclerview.apply {
            layoutManager = FlexboxLayoutManager(context)
            (layoutManager as FlexboxLayoutManager).justifyContent = JustifyContent.FLEX_START
            (layoutManager as FlexboxLayoutManager).flexWrap= FlexWrap.WRAP

            val rightSpacingDecorator = RightSpacingItemDecoration(16)
            removeItemDecoration(rightSpacingDecorator) // does nothing if not applied already
            addItemDecoration(rightSpacingDecorator)

            customCategoryrecyclerAdapter =
                ViewCustomCategoryAdapter(
                    this@StoreFragment,
                    this@StoreFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                }
            })
            customCategoryrecyclerAdapter.stateRestorationPolicy= StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = customCategoryrecyclerAdapter

        }


    }

    private fun subscribeObservers(){
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
            customCategoryrecyclerAdapter.submitList(viewModel.getViewCustomCategoryFields())
            productRecyclerAdapter.apply {
                    submitList(viewModel.getViewProductList().products)
                }


        })
    }

    fun ProductMain(id:Long){
        viewModel.setStateEvent(
            StoreStateEvent.ProductMainEvent(
                id
            ))
        viewModel.clearViewProductList()
    }

    fun AllProduct(){
        viewModel.setStateEvent(
            StoreStateEvent.AllProductEvent()
        )
        viewModel.clearViewProductList()
    }

    override fun onItemSelected(position: Int, item: CustomCategory) {
        view_custom_category_recyclerview.adapter!!.notifyDataSetChanged()
        ProductMain(item.pk.toLong())
    }

    override fun onItemSelected(item: Product,action:Int) {
        when(action){
            ActionConstants.SELECTED ->{
               viewModel.setViewProductFields(item)
                findNavController().navigate(R.id.action_storeFragment_to_viewProductFragment)
            }
        }

    }

    override fun onItemAddSelected() {
        view_custom_category_recyclerview.adapter!!.notifyDataSetChanged()
        AllProduct()
    }

    override fun onResume() {
        super.onResume()
        setSelectedPositions(viewModel.getCustomCategoryRecyclerPosition())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        viewModel.setCustomCategoryRecyclerPosition(getSelectedPositions())
        customCategoryrecyclerAdapter.resetSelectedPosition()
        view_custom_category_recyclerview.adapter = null
        view_product_recyclerview.adapter=null
    }
}