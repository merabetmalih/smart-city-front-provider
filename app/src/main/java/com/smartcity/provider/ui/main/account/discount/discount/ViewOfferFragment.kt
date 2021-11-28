package com.smartcity.provider.ui.main.account.discount.discount

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.models.OfferState
import com.smartcity.provider.models.OfferType
import com.smartcity.provider.ui.AreYouSureCallback
import com.smartcity.provider.ui.main.account.BaseAccountFragment
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountStateEvent
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.*
import com.smartcity.provider.util.*
import com.smartcity.provider.util.DateUtils.Companion.convertStringToStringDate
import kotlinx.android.synthetic.main.fragment_view_offer.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class ViewOfferFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_view_offer,viewModelFactory){

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

        subscribeObservers()
        setInformation()
        editDiscount()
        deleteDiscount()
    }

    private fun deleteDiscount() {
        delete_button.setOnClickListener {
            val callback: AreYouSureCallback = object: AreYouSureCallback {
                override fun proceed() {
                    deleteOffer(
                        viewModel.getSelectedOffer()!!.id!!
                    )
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

    private fun editDiscount() {
        edit_button.setOnClickListener {
            updateOffer()
        }
    }

    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.DELETE_DONE)){
                    findNavController().popBackStack()
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

    @SuppressLint("SetTextI18n")
    private fun setInformation() {
        viewModel.getSelectedOffer()?.apply {
            offer_name.text=this.discountCode

            val topBottom=0
            val rightLeft=25
            when(this.offerState){
                OfferState.EXPIRED  ->{
                    offer_state.text="Expired"
                    offer_state.background= androidx.core.content.res.ResourcesCompat.getDrawable(resources,
                        R.drawable.radius_button_grey,null)
                    offer_state.setPadding(rightLeft,topBottom,rightLeft,topBottom)
                }
                OfferState.ACTIVE ->{
                    offer_state.text="Active"
                    offer_state.background= androidx.core.content.res.ResourcesCompat.getDrawable(resources,
                        R.drawable.radius_button_green,null)
                    offer_state.setPadding(rightLeft,topBottom,rightLeft,topBottom)
                }
                OfferState.PLANNED ->{
                    offer_state.text="Planned"
                    offer_state.background= androidx.core.content.res.ResourcesCompat.getDrawable(resources,
                        R.drawable.radius_button_yellow,null)
                    offer_state.setPadding(rightLeft,topBottom,rightLeft,topBottom)
                }
            }

            when(this.type){
                OfferType.PERCENTAGE->{
                    discount_value.text= "${this.percentage}%"
                }

                OfferType.FIXED->{
                    discount_value.text= "${this.newPrice}${Constants.DOLLAR}"
                }
            }

            discount_product_number.text="${this.products!!.size}"

            discount_date.text="${DateUtils.convertStringToStringDateSimpleFormat(this.startDate!!)} - ${DateUtils.convertStringToStringDateSimpleFormat(
                this.endDate!!
            )}"
        }
    }

    private fun updateOffer() {
        viewModel.getSelectedOffer()?.apply {
            viewModel.setSelectedProductDiscount(this.products!!)
            viewModel.setStartRangeDiscountDate(convertStringToStringDate(this.startDate!!))
            viewModel.setEndRangeDiscountDate(convertStringToStringDate(this.endDate!!))
            viewModel.setDiscountCode(this.discountCode!!)
            viewModel.setOfferType(this.type!!)
            viewModel.setDiscountValuePercentage("%"+this.percentage.toString())
            viewModel.setDiscountValueFixed(this.newPrice.toString())
        }
        findNavController().navigate(R.id.action_viewOfferFragment_to_addDiscountFragment)
    }

    private fun deleteOffer(id:Long){
        viewModel.setStateEvent(
            AccountStateEvent.DeleteOfferEvent(
                id
            )
        )
    }
}