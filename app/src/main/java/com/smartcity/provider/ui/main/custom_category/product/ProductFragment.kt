package com.smartcity.provider.ui.main.custom_category.product

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.smartcity.provider.R
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.ui.AreYouSureCallback
import com.smartcity.provider.ui.main.custom_category.BaseCustomCategoryFragment
import com.smartcity.provider.ui.main.custom_category.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryStateEvent
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.provider.ui.main.custom_category.viewmodel.*
import com.smartcity.provider.ui.main.order.order.adapters.OrderFilterAdapter
import com.smartcity.provider.util.*
import kotlinx.android.synthetic.main.fragment_product.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class ProductFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCustomCategoryFragment(R.layout.fragment_product,viewModelFactory),
    SwipeRefreshLayout.OnRefreshListener,
    ProductAdapter.Interaction,
    OrderFilterAdapter.Interaction
{

    private lateinit var productRecyclerAdapter: ProductAdapter

    private var categoryProductAdapter: OrderFilterAdapter? = null
    private lateinit var dialogView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY] as CustomCategoryViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }

    fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        swipe_refresh.setOnRefreshListener(this)
        uiCommunicationListener.expandAppBar()
        uiCommunicationListener.displayBottomNavigation(false)

        addProduct()
        initProductRecyclerView()
        ProductMain()
        subscribeObservers()
        setToolBareText(viewModel.getSelectedCustomCategory()!!.name)
        backProceed()
    }

    private fun backProceed() {
        back_button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setToolBareText(text:String){
        category_name.text=text
    }

    fun ProductMain(){
        viewModel.setStateEvent(
            CustomCategoryStateEvent.ProductMainEvent(
            viewModel.getSelectedCustomCategory()!!.pk.toLong()
        ))
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

                if(stateMessage.response.message.equals(SuccessHandling.DELETE_DONE)){
                    ProductMain()
                }

                if(stateMessage.response.message.equals(SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE)){
                    dialog.dismiss()
                    ProductMain()
                }
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer { jobCounter ->//must
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })

        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
                productRecyclerAdapter.submitList(viewModel.getProductList())
        })
    }

    private fun addProduct() {
        add_product_button.setOnClickListener {
            findNavController().navigate(R.id.action_productFragment_to_createProductFragment)
        }
    }

    fun initProductRecyclerView(){
        product_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@ProductFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productRecyclerAdapter =
                ProductAdapter(
                    requestManager,
                    this@ProductFragment
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

    override fun onRefresh() {
        ProductMain()
        swipe_refresh.isRefreshing = false
    }

    override fun onItemSelected(item: Product,action:Int) {
        when (action){
            ActionConstants.SELECTED->{

                viewModel.setViewProductFields(item)
                findNavController().navigate(R.id.action_productFragment_to_viewProductFragment)
            }

            ActionConstants.DELETE->{
                val callback: AreYouSureCallback = object: AreYouSureCallback {
                    override fun proceed() {
                        deleteProduct(item.id)
                    }
                    override fun cancel() {
                        // ignore
                    }
                }

                uiCommunicationListener.onResponseReceived(
                    response = Response(
                        message = getString(R.string.are_you_sure_delete),
                        uiComponentType = UIComponentType.AreYouSureDialog(callback),
                        messageType = MessageType.Info()
                    ),
                    stateMessageCallback = object: StateMessageCallback{
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )
            }
        }

    }

    var selectedProduct:Product? = null
    override fun onItemMoved(item: Product) {
        selectedProduct=item
        showFilterDialog()
    }

    lateinit var dialog:BottomSheetDialog
    private fun showFilterDialog() {
        dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        dialogView = layoutInflater.inflate(R.layout.dialog_move_product_custom_category, null)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)

        val categoryRecyclerView = dialogView.findViewById<RecyclerView>(R.id.move_product_category)
        categoryProductAdapter = OrderFilterAdapter(this@ProductFragment)
        initCategoryRecyclerView(categoryRecyclerView, categoryProductAdapter!!)

        categoryProductAdapter!!.submitList(
            viewModel.getCustomCategoryFields().map { it.name },
            ""
        )

        dialog.show()
    }

    private fun initCategoryRecyclerView(recyclerView: RecyclerView,recyclerAdapter: OrderFilterAdapter) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ProductFragment.context,LinearLayoutManager.HORIZONTAL,false)

            val rightSpacingDecorator = RightSpacingItemDecoration(0)
            removeItemDecoration(rightSpacingDecorator) // does nothing if not applied already
            addItemDecoration(rightSpacingDecorator)

            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                }
            })
            recyclerAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = recyclerAdapter
        }
    }

    fun deleteProduct(id:Long){
        viewModel.setStateEvent(CustomCategoryStateEvent.DeleteProductEvent(id))
    }

    override fun onResume() {
        super.onResume()
        viewModel.clearViewProductFields()
        viewModel.clearProductFields()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        //viewModel.clearProductList()
        viewModel.clearProductFields()
        product_recyclerview.adapter = null
        setToolBareText("")
    }

    override fun onItemSelected(item: String) {
        val category = viewModel.getCustomCategoryFields().find { it.name == item }
        viewModel.setStateEvent(
            CustomCategoryStateEvent.UpdateProductsCustomCategoryEvent(
                listOf(selectedProduct!!.id),
                category!!.pk.toLong()
            )
        )
    }

    override fun onItemDeSelected(item: String) {

    }
}