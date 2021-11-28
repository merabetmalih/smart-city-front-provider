package com.smartcity.provider.ui.main.account.flashdeals

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.smartcity.provider.R
import com.smartcity.provider.ui.main.account.BaseAccountFragment
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountStateEvent
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.*
import com.smartcity.provider.util.DateUtils
import com.smartcity.provider.util.StateMessageCallback
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_flash_deals.*
import kotlinx.android.synthetic.main.fragment_search_flash_deal.*
import kotlinx.android.synthetic.main.layout_order_filter.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class SearchFlashDealFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_search_flash_deal, viewModelFactory)
{
    private lateinit var flashRecyclerAdapter: FlashDealsAdapter

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
        subscribeObservers()

        pick_date_search_flash_container.setOnClickListener {
            showDatePicker()
        }

        pick_date_search_flash_button.setOnClickListener {
            showDatePicker()
        }
    }

    @SuppressLint("SetTextI18n")
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
            viewModel.getSearchFlashDealRangeDate().first?.let { start ->
                viewModel.getSearchFlashDealRangeDate().second?.let{end ->
                    search_range_date.text="${DateUtils.convertStringToStringDateSimpleFormatSecond(start)} - ${DateUtils.convertStringToStringDateSimpleFormatSecond(end)}"
                }
            }
            flashRecyclerAdapter.submitList(viewModel.getSearchFlashDealsList())
        })
    }

    private fun showDatePicker(){
        val calendarConstraints= CalendarConstraints.Builder()
        calendarConstraints.setValidator(DateValidatorPointBackward.now())

        val builder= MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText(R.string.select_date)
        builder.setTheme(R.style.CustomThemeOverlay_MaterialCalendar_Fullscreen)
        builder.setCalendarConstraints(calendarConstraints.build())

        if (!viewModel.getSearchFlashDealRangeDate().first.isNullOrEmpty() && !viewModel.getSearchFlashDealRangeDate().second.isNullOrEmpty()){
            builder.setSelection(androidx.core.util.Pair(
                DateUtils.convertDatePickerStringDateToLong(viewModel.getSearchFlashDealRangeDate().first!!),
                DateUtils.convertDatePickerStringDateToLong(viewModel.getSearchFlashDealRangeDate().second!!)
            ))
        }

        val materialDatePicker=builder.build()

        materialDatePicker.addOnPositiveButtonClickListener {
            viewModel.setSearchFlashDealRangeDate(
                Pair(
                    DateUtils.convertLongToStringDate(it.first!!),
                    DateUtils.convertLongToStringDate(it.second!!)
                )
            )

            getFlashDeals()
        }
        //materialDatePicker.isCancelable=false
        /*materialDatePicker.addOnNegativeButtonClickListener {
            findNavController().popBackStack()
        }*/
        materialDatePicker.show(activity!!.supportFragmentManager,"DATE_PICKER")
    }

    private fun getFlashDeals() {
        viewModel.setStateEvent(
            AccountStateEvent.GetSearchFlashDealsEvent()
        )
    }
    private fun initRecyclerView() {
        search_flash_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@SearchFlashDealFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            flashRecyclerAdapter =
                FlashDealsAdapter(

                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = flashRecyclerAdapter
        }
    }
}