package com.smartcity.provider.ui.main.custom_category.customCategory


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.models.CustomCategory
import com.smartcity.provider.ui.AreYouSureCallback
import com.smartcity.provider.ui.main.custom_category.BaseCustomCategoryFragment
import com.smartcity.provider.ui.main.custom_category.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryStateEvent
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.provider.ui.main.custom_category.viewmodel.clearProductList
import com.smartcity.provider.ui.main.custom_category.viewmodel.getCustomCategoryFields
import com.smartcity.provider.ui.main.custom_category.viewmodel.setSelectedCustomCategory
import com.smartcity.provider.util.*
import kotlinx.android.synthetic.main.fragment_custom_category.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class CustomCategoryFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCustomCategoryFragment(R.layout.fragment_custom_category,viewModelFactory),
    CustomCategoryAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{
    private lateinit var recyclerAdapter: CustomCategoryAdapter

    private lateinit var alertDialog: AlertDialog

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

    /**
     * !IMPORTANT!
     * Must save ViewState b/c in event of process death the LiveData in ViewModel will be lost
     */
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
        uiCommunicationListener.displayBottomNavigation(true)

        CustomCategoryMain()
        initvRecyclerView()
        subscribeObservers()
        addCustomCategory()
    }

    private fun addCustomCategory() {
        add_custom_category_button.setOnClickListener {
            showCustomDialog("New",null,
                ActionConstants.CREATE,null)
        }
    }

    fun createCustomCategory(name:String){
        viewModel.setStateEvent(
            CustomCategoryStateEvent.CreateCustomCategoryEvent(
                name
            )
        )
    }

    fun updateCustomCategory(id:Long,name:String,provider: Long){
        viewModel.setStateEvent(
            CustomCategoryStateEvent.UpdateCustomCategoryEvent(
                id,
                name,
                provider
            )
        )
    }

    fun deleteCustomCategory(id:Long){
        viewModel.setStateEvent(CustomCategoryStateEvent.DeleteCustomCategoryEvent(id))
    }

    fun showCustomDialog(header:String,inputText:String?,action: Int,item: CustomCategory?) {
        val inflater: LayoutInflater = this.getLayoutInflater()
        val dialogView: View = inflater.inflate(R.layout.dialog_custom_view, null)

        val header_txt = dialogView.findViewById<TextView>(R.id.header)
        header_txt.text = header

        val input: EditText = dialogView.findViewById(R.id.input_name_custom_category)
        inputText?.let {
            input.setText(it)
        }
        val save_button: Button = dialogView.findViewById(R.id.save)
        save_button.setOnClickListener {
            when(action){
                 ActionConstants.UPDATE ->{
                    item?.let {
                        updateCustomCategory(
                            it.pk.toLong(),
                            input.text.toString(),
                            it.provider.toLong()
                        )
                    }
                }

                 ActionConstants.CREATE ->{
                    createCustomCategory(input.text.toString())
                }
            }
        }
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context!!)
        dialogBuilder.setOnDismissListener(object : DialogInterface.OnDismissListener {
            override fun onDismiss(arg0: DialogInterface) {

            }
        })
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create();
        //alertDialog.window!!.getAttributes().windowAnimations = R.style.PauseDialogAnimation
       // alertDialog.window.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()


    }

    fun CustomCategoryMain(){
        viewModel.setStateEvent(CustomCategoryStateEvent.CustomCategoryMainEvent())
    }

    fun initvRecyclerView(){
        custom_category_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@CustomCategoryFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter =
                CustomCategoryAdapter(
                    this@CustomCategoryFragment
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

    fun subscribeObservers(){
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
                    CustomCategoryMain()
                }

                if(stateMessage.response.message.equals(SuccessHandling.CUSTOM_CATEGORY_CREATION_DONE)){
                    alertDialog.dismiss()
                    CustomCategoryMain()
                }

                if(stateMessage.response.message.equals(SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE)){
                    alertDialog.dismiss()
                    CustomCategoryMain()
                }
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer { jobCounter ->//must
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })

        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            recyclerAdapter.submitList(viewModel.getCustomCategoryFields())
        })
    }

    override fun onItemSelected(position: Int, item: CustomCategory, action: Int) {
        when(action){
            ActionConstants.UPDATE ->{
                showCustomDialog("Update",item.name,
                    ActionConstants.UPDATE,item)
            }

            ActionConstants.DELETE ->{
                val callback: AreYouSureCallback = object: AreYouSureCallback {
                    override fun proceed() {
                        deleteCustomCategory(item.pk.toLong())
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

            ActionConstants.SELECTED -> {
                viewModel.setSelectedCustomCategory(item)
                resetUI()
                findNavController().navigate(R.id.action_createBlogFragment_to_productFragment)
            }

        }
    }

    override fun onRefresh() {
        CustomCategoryMain()
        swipe_refresh.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        custom_category_recyclerview.adapter = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.clearProductList()
    }

    private  fun resetUI(){
        custom_category_recyclerview.smoothScrollToPosition(0)
        uiCommunicationListener.hideSoftKeyboard()
        focusable_view_custom_category.requestFocus()
    }
}

















