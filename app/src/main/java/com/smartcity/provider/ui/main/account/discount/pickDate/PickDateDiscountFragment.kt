package com.smartcity.provider.ui.main.account.discount.pickDate

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.smartcity.provider.R
import com.smartcity.provider.ui.main.account.BaseAccountFragment
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.getRangeDiscountDate
import com.smartcity.provider.ui.main.account.viewmodel.setEndRangeDiscountDate
import com.smartcity.provider.ui.main.account.viewmodel.setStartRangeDiscountDate
import com.smartcity.provider.util.DateUtils
import kotlinx.android.synthetic.main.fragment_pick_date_discount.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.*
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class PickDateDiscountFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_pick_date_discount,viewModelFactory){

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
        pick_start_date.setOnClickListener {
            showDatePicker("start")
        }

        pick_end_date.setOnClickListener {
            showDatePicker("end")
        }
    }

    private fun showDatePicker(step:String){
        val calendarConstraints= CalendarConstraints.Builder()
        calendarConstraints.setValidator(DateValidatorPointForward.now())

        val builder= MaterialDatePicker.Builder.datePicker()
        builder.setTitleText(R.string.select_date)
        builder.setTheme(R.style.CustomThemeOverlay_MaterialCalendar)
        builder.setCalendarConstraints(calendarConstraints.build())

        when(step){

            "start" ->{
                viewModel.getRangeDiscountDate().first?.let {
                    builder.setSelection(
                        DateUtils.convertDatePickerStringDateToLong(
                            it
                        )
                    )
                }
            }

            "end" ->{
                viewModel.getRangeDiscountDate().second?.let {
                    builder.setSelection(
                        DateUtils.convertDatePickerStringDateToLong(
                            it
                        )
                    )
                }
            }

        }


        val materialDatePicker=builder.build()
        materialDatePicker.addOnPositiveButtonClickListener {
            when(step){
                "start" ->
                {
                    viewModel.setStartRangeDiscountDate(
                        DateUtils.convertLongToStringDate(it)
                    )
                }

                "end" ->{
                    viewModel.setEndRangeDiscountDate(
                        DateUtils.convertLongToStringDate(it)
                    )
                }
            }
            showTimePicker(step)
        }
        materialDatePicker.show(activity!!.supportFragmentManager,"DATE_PICKER")
    }

    private fun showTimePicker(step:String){
        val cldr = Calendar.getInstance()
        val hour = cldr[Calendar.HOUR_OF_DAY]
        val minutes = cldr[Calendar.MINUTE]
        // time picker dialog
        val picker = TimePickerDialog(
            context,
            OnTimeSetListener { tp, sHour, sMinute ->
                var start = ""
                viewModel.getRangeDiscountDate().first?.let {
                    start=it
                }

                var end=""
                viewModel.getRangeDiscountDate().second?.let {
                    end=it
                }

                when(step){
                    "start" ->
                    {
                        viewModel.setStartRangeDiscountDate(
                            "$start $sHour:$sMinute"
                        )
                    }

                    "end" ->{
                        viewModel.setEndRangeDiscountDate(
                            "$end $sHour:$sMinute"
                        )
                    }
                }
            },
            hour,
            minutes,
            true
        )
        picker.setCancelable(false)
        picker.setButton(DialogInterface.BUTTON_NEGATIVE,"cancel",DialogInterface.OnClickListener { dialogInterface, i ->
            showDatePicker(step)
        })
        picker.show()
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
           viewModel.getRangeDiscountDate().also {
               it.first?.let {
                   start_date_text.text=it
               }

               it.second?.let {
                   end_date_text.text=it
               }
           }

        })
    }
}