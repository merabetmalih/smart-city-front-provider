package com.smartcity.provider.ui.main.account.discount.createDiscount

import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.models.Offer
import com.smartcity.provider.models.OfferType
import com.smartcity.provider.models.OfferTypeObject
import com.smartcity.provider.ui.main.account.BaseAccountFragment
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountStateEvent
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.*
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.provider.util.*
import kotlinx.android.synthetic.main.fragment_add_discount.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class AddDiscountFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_add_discount,viewModelFactory){

    var percentageTextWatcher:TextWatcher?=null

    lateinit var offerResult:Offer

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

        discountValueInputBehavior()
        addProducts()
        setProductNumber()
        setDateRange()

        discount_set_date.setOnClickListener {
            navPickDate()
        }

        setInputCodeChangeListener()

        create_offer_button.setOnClickListener {
            createOffer()
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.CREATION_DONE)){
                    findNavController().popBackStack()
                }

                if(stateMessage.response.message.equals(SuccessHandling.PRODUCT_UPDATE_DONE)){
                    findNavController().navigate(R.id.action_addDiscountFragment_to_discountFragment)
                }

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
    }

    private fun createOffer() {
        val offerFieldsErrors=viewModel.getCurrentViewStateOrNew().discountFields.isValidForCreation()
        if(!offerFieldsErrors.equals(CustomCategoryViewState.ProductFields.CreateProductError.none())){
            showErrorDialog(offerFieldsErrors)
            return
        }

        val productVariantIds:MutableList<Long> = mutableListOf()
        viewModel.getSelectedProductDiscount().map {product->
            product.productVariants.map {variant->
                productVariantIds.add(variant.id!!)
            }
        }

        offerResult=Offer(
            -1,
            viewModel.getDiscountCode(),
            viewModel.getOfferType(),
            if(viewModel.getDiscountValueFixed().isEmpty())0.0 else viewModel.getDiscountValueFixed().toDouble(),
            if(viewModel.getDiscountValuePercentage().replace("%","").isEmpty())0 else viewModel.getDiscountValuePercentage().replace("%","").toInt(),
            viewModel.getRangeDiscountDate().first,
            viewModel.getRangeDiscountDate().second,
            -1,
            productVariantIds,
            listOf(),
            null
        )

        if(viewModel.getSelectedOffer()==null){//create offer
            viewModel.setStateEvent(
                AccountStateEvent.CreateOfferEvent(
                    offerResult
                )
            )
        }else{//update offer
            val selected =viewModel.getSelectedOffer()!!
            offerResult.id=selected.id
            viewModel.setStateEvent(
                AccountStateEvent.UpdateOfferEvent(
                    offerResult
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        dropDownMenuBehavior()
        setProductProperties(
            viewModel.getDiscountCode(),
            viewModel.getDiscountValuePercentage(),
            viewModel.getDiscountValueFixed()
        )
    }

    fun setProductProperties(code: String?,
                             valuePercentage: String,
                             valueFixed: String){
        input_discount_code.setText(code)
        input_discount_percentage.setText(valuePercentage)
        input_discount_fixed.setText(valueFixed)
    }

    private fun setInputCodeChangeListener(){
        input_discount_code.addTextChangedListener {
            viewModel.setDiscountCode(it.toString())
        }

        input_discount_percentage.addTextChangedListener {
            viewModel.setDiscountValuePercentage(
                it.toString()
            )
        }

        input_discount_fixed.addTextChangedListener {
            viewModel.setDiscountValueFixed(
                it.toString()
            )
        }
    }

    fun showErrorDialog(errorMessage: String){
        uiCommunicationListener.onResponseReceived(
            response = Response(
                message = errorMessage,
                uiComponentType = UIComponentType.Dialog(),
                messageType = MessageType.Error()
            ),
            stateMessageCallback = object: StateMessageCallback {
                override fun removeMessageFromStack() {
                    viewModel.clearStateMessage()
                }
            }
        )
    }

    private fun setDateRange() {
        viewModel.getRangeDiscountDate().first?.let {
            start_date.text=it
        }

        viewModel.getRangeDiscountDate().second?.let {
            end_date.text=it
        }
    }

    private fun navPickDate(){
        findNavController().navigate(R.id.action_addDiscountFragment_to_pickDateDiscountFragment)
    }

    private fun setProductNumber() {
        product_number.text=viewModel.getSelectedProductDiscount().size.toString()
    }

    private fun addProducts(){
        discount_add_products.setOnClickListener {
            findNavController().navigate(R.id.action_addDiscountFragment_to_addProductDiscountFragment)
        }
    }

    private fun discountValueInputBehavior(){
        input_offer_type.doOnTextChanged { text, start, before, count ->
            when(text.toString()){
                "Percentage" ->{
                    viewModel.setOfferType(OfferType.PERCENTAGE)
                    input_discount_percentage.visibility=View.VISIBLE
                    input_discount_fixed.visibility=View.GONE
                    initPercentageTextWatchers(
                        viewModel.getDiscountValuePercentage()
                    )
                }

                "Fixed amount" ->{
                    viewModel.setOfferType(OfferType.FIXED)
                    input_discount_fixed.visibility=View.VISIBLE
                    input_discount_percentage.visibility=View.GONE
                    initFixedText(
                        viewModel.getDiscountValueFixed()
                    )
                }
            }
        }
    }

    private fun initFixedText(fixed:String){
        input_discount_fixed.setText(fixed)
    }

    private fun initPercentageTextWatchers(percentage:String){
        input_discount_percentage.setText(percentage)
        Selection.setSelection(input_discount_percentage.text, input_discount_percentage.text.toString().length)
        percentageTextWatcher=object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (!s.toString().startsWith("%")) {
                    input_discount_percentage.setText("%")
                    Selection.setSelection(input_discount_percentage.text, input_discount_percentage.text.toString().length)
                }

                if(s.toString().removePrefix("%").isNotBlank()){
                    if(s.toString().removePrefix("%").toInt()>100){
                        input_discount_percentage.setText("%100")
                        Selection.setSelection(input_discount_percentage.text, input_discount_percentage.text.toString().length)
                    }
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        }
        input_discount_percentage.addTextChangedListener(percentageTextWatcher)
    }

    private fun dropDownMenuBehavior(){
        val arrayAdapter=ArrayAdapter(context!!,R.layout.layout_offer_type_item,OfferTypeObject.OfferTypes.map { it.second })
        when(viewModel.getOfferType()){
            OfferType.PERCENTAGE ->{
                input_offer_type.setText(arrayAdapter.getItem(0)!!.toString(),false)
            }

            OfferType.FIXED ->{
                input_offer_type.setText(arrayAdapter.getItem(1)!!.toString(),false)
            }
        }
        input_offer_type.setAdapter(arrayAdapter)
    }
}