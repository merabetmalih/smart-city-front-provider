package com.smartcity.provider.ui.main.account.policy

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.models.Policy
import com.smartcity.provider.models.SelfPickUpOptions
import com.smartcity.provider.models.TaxRange
import com.smartcity.provider.ui.main.account.BaseAccountFragment
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountStateEvent
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.*
import com.smartcity.provider.util.StateMessageCallback
import com.smartcity.provider.util.SuccessHandling
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_policy_form_option.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class PolicyFormOptionFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_policy_form_option,viewModelFactory),
    DepositRangeAdapter.Interaction{

    private lateinit var recyclerAdapter: DepositRangeAdapter

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

        initPicker()
        initUi()
        subscribeObservers()
        initRecyclerView()
        createDepositRange()
        save_policy_button.setOnClickListener {
            setDurationAndTax()
            savePolicy()
        }
    }

    private fun createDepositRange() {
        add_deposit_range_button.setOnClickListener {
            showTaxRangeDialog()
        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {viewState->
            recyclerAdapter.submitList(viewModel.getPolicyConfigurationTaxRanges().toSet())
        })

        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.CREATION_DONE)){
                    navPolicy()
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

    private fun navPolicy(){
        findNavController().navigate(R.id.action_policyFormOptionFragment_to_policyFragment)
    }

    private fun savePolicy() {
        viewModel.setStateEvent(
            AccountStateEvent.SavePolicyEvent(
                Policy(viewModel.getPolicyConfigurationDelivery()!!,
                    viewModel.getPolicyConfigurationSelfPickUpOption()!!,
                    viewModel.getPolicyConfigurationValidDuration()!!,
                    viewModel.getPolicyConfigurationTax()!!,
                    -1,
                    viewModel.getPolicyConfigurationTaxRanges()
                )
            )
        )
    }

    private fun setDurationAndTax() {
        viewModel.setPolicyConfigurationValidDuration(picker_validation_time.value.toLong())
        viewModel.setPolicyConfigurationTax(picker_percent.value)
    }

    private fun initPicker() {
        picker_validation_time.minValue=1
        picker_validation_time.maxValue=1000
        picker_validation_time.wrapSelectorWheel = false

        picker_percent.minValue=1
        picker_percent.maxValue=99
    }

    fun initRecyclerView(){
        ViewCompat.setNestedScrollingEnabled(deposit_ranges_recyclerview, false);
        //deposit_ranges_recyclerview.setNestedScrollingEnabled(false)
        deposit_ranges_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@PolicyFormOptionFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)
            recyclerAdapter =
                DepositRangeAdapter(
                    this@PolicyFormOptionFragment
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

    private fun showTaxRangeDialog() {
        activity?.let {
            val inflater: LayoutInflater = this.getLayoutInflater()
            val dialogView: View = inflater.inflate(R.layout.dialog_deposit_range, null)
            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context!!)
            dialogBuilder.setView(dialogView)
            val alertDialog = dialogBuilder.create()

            val save=dialogView.findViewById<Button>(R.id.save)


            save.setOnClickListener {
                val start=dialogView.findViewById<EditText>(R.id.input_start_range).text.toString()
                val end=dialogView.findViewById<EditText>(R.id.input_end_range).text.toString()
                val amount= dialogView.findViewById<EditText>(R.id.input_amount).text.toString()
                if(start.isEmpty().not() && end.isEmpty().not() && amount.isEmpty().not()){
                    val taxRange=TaxRange(
                        start.toDouble(),
                        end.toDouble(),
                        amount.toDouble()
                    )
                    saveTaxRange(taxRange)
                    alertDialog.dismiss()
                }

            }

            alertDialog.show()
        }
    }

    fun saveTaxRange(taxRange: TaxRange){
        val taxRanges=viewModel.getPolicyConfigurationTaxRanges().toMutableList()
        taxRanges.add(taxRange)
        viewModel.setPolicyConfigurationTaxRanges(taxRanges)
    }

    override fun onItemSelected(position: Int, item: TaxRange) {
        deleteTaxRange(item)
    }

    private fun deleteTaxRange(item: TaxRange){
        val list=viewModel.getPolicyConfigurationTaxRanges().toMutableList()
        list?.let {
            it.remove(item)
            viewModel.setPolicyConfigurationTaxRanges(it)
        }
    }

    private fun initUi(){
        when(viewModel.getPolicyConfigurationSelfPickUpOption()){
            SelfPickUpOptions.SELF_PICK_UP_TOTAL,SelfPickUpOptions.SELF_PICK_UP  ->{
                setUpUi(percentage = false, range = false)
            }
            SelfPickUpOptions.SELF_PICK_UP_PART_PERCENTAGE ->{
                setUpUi(percentage = true, range = false)
            }
            SelfPickUpOptions.SELF_PICK_UP_PART_RANGE->{
                setUpUi(percentage = false, range = true)
            }
        }
    }

    private fun setUpUi(percentage:Boolean,range:Boolean){
        if(percentage){
            container_percentage.visibility=View.VISIBLE
        }else{
            container_percentage.visibility=View.GONE
        }

        if(range){
            container_range.visibility=View.VISIBLE
        }else{
            container_range.visibility=View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        deposit_ranges_recyclerview?.let {
            it.adapter=null
        }
    }
}