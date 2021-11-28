package com.smartcity.provider.ui.main.order.order

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.smartcity.provider.R
import com.smartcity.provider.models.OrderStep
import com.smartcity.provider.models.product.Order
import com.smartcity.provider.models.product.OrderType
import com.smartcity.provider.ui.AreYouSureCallback
import com.smartcity.provider.ui.main.order.BaseOrderFragment
import com.smartcity.provider.ui.main.order.order.adapters.OrderProductAdapter
import com.smartcity.provider.ui.main.order.state.ORDER_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.order.state.OrderStateEvent
import com.smartcity.provider.ui.main.order.state.OrderViewState
import com.smartcity.provider.ui.main.order.viewmodel.getOrderList
import com.smartcity.provider.ui.main.order.viewmodel.getSelectedOrder
import com.smartcity.provider.ui.main.order.viewmodel.setOrderListData
import com.smartcity.provider.util.*
import com.smartcity.provider.util.DateUtils.Companion.convertLongToStringDateTime
import kotlinx.android.synthetic.main.fragment_view_order.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class ViewOrderFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseOrderFragment(R.layout.fragment_view_order,viewModelFactory)
{
    private lateinit var orderProductRecyclerAdapter: OrderProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ORDER_VIEW_STATE_BUNDLE_KEY] as OrderViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    /**
     * !IMPORTANT!
     * Must save ViewState b/c in event of process death the LiveData in ViewModel will be lost
     */
    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.
       // viewState?.orderFields?.orderList = ArrayList()

        outState.putParcelable(
            ORDER_VIEW_STATE_BUNDLE_KEY,
            viewState
        )
        super.onSaveInstanceState(outState)
    }

    fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        uiCommunicationListener.displayBottomNavigation(false)

        setOrderNote()
        setOrderInformation()
        initProductsRecyclerView()
        setProductsList()
        setBillInformation()
        subscribeObservers()
        acceptRejectOrder()
        readyOrder()
        deliveredPickedUpOrder()
        setButtonsUi()
        backProceed()
    }

    private fun backProceed() {
        back_button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setOrderNote() {
        viewModel.getSelectedOrder()?.let { order ->
            if(!order.providerNote.isNullOrEmpty()){
                view_order_note.text = order.providerNote
            }else{
                view_order_note.text = "Add note"
            }
        }

        add_note_container.setOnClickListener {
            findNavController().navigate(R.id.action_viewOrderFragment_to_addOrderNoteFragment)
        }
    }

    private fun getStepFromOrder(order:Order):OrderStep?{
        if(order.orderState.newOrder){
            return OrderStep.NEW_ORDER
        }

        if(order.orderState.accepted &&
            !order.orderState.ready &&
            !order.orderState.delivered &&
            !order.orderState.pickedUp &&
            !order.orderState.received){
            return OrderStep.ACCEPT_ORDER
        }

        if (order.orderState.accepted &&
            order.orderState.ready &&
            !order.orderState.delivered &&
            !order.orderState.pickedUp &&
            !order.orderState.received){
            return OrderStep.READY_ORDER
        }
        return null
    }

    private fun setButtonsUi() {
        getStepFromOrder(viewModel.getSelectedOrder()!!)?.let {
            when(it){
                OrderStep.NEW_ORDER ->{
                    view_order_NEW_ORDER_buttons.visibility=View.VISIBLE
                }

                OrderStep.ACCEPT_ORDER ->{
                    view_order_ACCEPT_ORDER_buttons.visibility=View.VISIBLE
                }

                OrderStep.READY_ORDER ->{
                    view_order_READY_ORDER_buttons.visibility=View.VISIBLE
                }
            }
        }
    }

    private fun acceptRejectOrder() {
        view_order_accept.setOnClickListener {
            val callback: AreYouSureCallback = object: AreYouSureCallback {
                override fun proceed() {
                    viewModel.setStateEvent(
                        OrderStateEvent.SetOrderAcceptedEvent(
                            viewModel.getSelectedOrder()!!.id
                        )
                    )
                }
                override fun cancel() {
                    // ignore
                }
            }

            uiCommunicationListener.onResponseReceived(
                response = Response(
                    message = getString(R.string.are_you_sure_accept),
                    uiComponentType = UIComponentType.AreYouSureDialog(callback),
                    messageType = MessageType.Info()
                ),
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.clearStateMessage()
                    }
                }
            )
        }

        view_order_reject.setOnClickListener {
            val callback: AreYouSureCallback = object: AreYouSureCallback {
                override fun proceed() {
                    viewModel.setStateEvent(
                        OrderStateEvent.SetOrderRejectedEvent(
                            viewModel.getSelectedOrder()!!.id
                        )
                    )
                }
                override fun cancel() {
                    // ignore
                }
            }

            uiCommunicationListener.onResponseReceived(
                response = Response(
                    message = getString(R.string.are_you_sure_reject),
                    uiComponentType = UIComponentType.AreYouSureDialog(callback),
                    messageType = MessageType.Info()
                ),
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.clearStateMessage()
                    }
                }
            )
        }
    }

    private fun readyOrder() {
        view_order_ready.setOnClickListener {
            val callback: AreYouSureCallback = object: AreYouSureCallback {
                override fun proceed() {
                    viewModel.setStateEvent(
                        OrderStateEvent.SetOrderReadyEvent(
                            viewModel.getSelectedOrder()!!.id
                        )
                    )
                }
                override fun cancel() {
                    // ignore
                }
            }

            uiCommunicationListener.onResponseReceived(
                response = Response(
                    message = getString(R.string.are_you_sure_ready),
                    uiComponentType = UIComponentType.AreYouSureDialog(callback),
                    messageType = MessageType.Info()
                ),
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.clearStateMessage()
                    }
                }
            )
        }
    }

    private fun deliveredPickedUpOrder() {
        when(viewModel.getSelectedOrder()!!.orderType){
            OrderType.DELIVERY ->{
                view_order_delivered.visibility=View.VISIBLE
            }

            OrderType.SELFPICKUP ->{
                view_order_picked_up.visibility=View.VISIBLE
            }
        }

        view_order_picked_up.setOnClickListener {
            showOrderConfirmationDialog(viewModel.getSelectedOrder()!!)
        }

        view_order_delivered.setOnClickListener {
            showOrderConfirmationDialog(viewModel.getSelectedOrder()!!)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showOrderConfirmationDialog(order: Order){
        val dialog = BottomSheetDialog(context!!,R.style.BottomSheetDialogTheme)
        dialog.behavior.state= BottomSheetBehavior.STATE_EXPANDED
        val dialogView = layoutInflater.inflate(R.layout.dialog_order_confirmation_delivery_pick_up, null)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)

        val confirmPickedUp=dialogView.findViewById<TextView>(R.id.confirm_picked_up)
        val confirmDelivered=dialogView.findViewById<TextView>(R.id.confirm_delivered)

        when(order.orderType){
            OrderType.DELIVERY ->{
                confirmDelivered.visibility=View.VISIBLE
            }

            OrderType.SELFPICKUP ->{
                confirmPickedUp.visibility=View.VISIBLE
            }
        }

        val singleDateAndTimePicker=dialogView.findViewById<SingleDateAndTimePicker>(R.id.single_day_picker)
        singleDateAndTimePicker.setIsAmPm(false)

        val backButton=dialogView.findViewById<Button>(R.id.back_order)
        backButton.setOnClickListener {
            dialog.dismiss()
        }

        val comment=dialogView.findViewById<EditText>(R.id.input_comment)

        val confirmButton=dialogView.findViewById<Button>(R.id.confirm_order_button)
        confirmButton.setOnClickListener {
            when(order.orderType){

                OrderType.DELIVERY ->{
                    viewModel.setStateEvent(
                        OrderStateEvent.SetOrderDeliveredEvent(
                            viewModel.getSelectedOrder()!!.id,
                            comment.text.toString(),
                            convertLongToStringDateTime(singleDateAndTimePicker.date.time)
                        )
                    )
                }

                OrderType.SELFPICKUP ->{
                    viewModel.setStateEvent(
                        OrderStateEvent.SetOrderPickedUpEvent(
                            viewModel.getSelectedOrder()!!.id,
                            comment.text.toString(),
                            convertLongToStringDateTime(singleDateAndTimePicker.date.time)
                        )
                    )
                }

            }
            dialog.dismiss()
        }
        dialog.show()
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

                if(stateMessage.response.message.equals(SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE)){
                    updateOrderList()
                }
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer { jobCounter ->//must
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })
    }

    private fun updateOrderList() {
        val list=viewModel.getOrderList().toMutableList()
        list.remove(viewModel.getSelectedOrder())
        viewModel.setOrderListData(list)
        findNavController().popBackStack()
    }

    @SuppressLint("SetTextI18n")
    private fun setOrderInformation() {
        viewModel.getSelectedOrder()?.let {order ->
            view_order_id.text=order.id.toString()

            view_order_time.text= DateUtils.convertStringToStringDate(order.createAt)

            view_order_type_delivery.visibility=View.GONE
            view_order_type_self_pickup.visibility=View.GONE
            when(order.orderType){
                OrderType.DELIVERY ->{
                    view_order_type.text="Delivery"
                    view_order_type_delivery.visibility=View.VISIBLE
                }

                OrderType.SELFPICKUP ->{
                    view_order_type.text="Self pickup"
                    view_order_type_self_pickup.visibility=View.VISIBLE
                    view_order_delivery_address_container.visibility=View.GONE
                }
            }

            view_order_receiver.text="${order.firstName} ${order.lastName} born in ${order.birthDay}"

            order.address?.let {item->
                view_order_delivery_address.text="${item.city}, ${item.street}, ${item.houseNumber.toString()}, ${item.zipCode.toString()}"
            }
        }
    }
    
    fun initProductsRecyclerView(){
        view_order_products_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            orderProductRecyclerAdapter =
                OrderProductAdapter(
                    requestManager
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = orderProductRecyclerAdapter
        }

    }

    private fun setProductsList() {
        viewModel.getSelectedOrder()?.let { order ->
            orderProductRecyclerAdapter?.let {
                it.submitList(
                    order.orderProductVariants.sortedBy { it.productVariant.price }
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setBillInformation() {
        viewModel.getSelectedOrder()?.let { order ->
            view_order_product_quantity.text= order.orderProductVariants.size.toString()

            view_order_product_total.text=order.bill!!.total.toString()+ Constants.DOLLAR
            view_order_product_paid.text=order.bill!!.alreadyPaid.toString()+ Constants.DOLLAR
            view_order_product_rest.text=(order.bill!!.total-order.bill!!.alreadyPaid).toString()+ Constants.DOLLAR
        }
    }
}