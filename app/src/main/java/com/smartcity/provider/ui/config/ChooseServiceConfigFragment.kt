package com.smartcity.provider.ui.config

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.smartcity.provider.R
import com.smartcity.provider.ui.config.state.ConfigStateEvent
import com.smartcity.provider.util.StateMessageCallback
import kotlinx.android.synthetic.main.fragment_choose_service_config.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class ChooseServiceConfigFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseConfigFragment(R.layout.fragment_choose_service_config,viewModelFactory){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getStoreCategories()
        subscribeObservers()
        next_choose_service_button.setOnClickListener {
            findNavController().navigate(R.id.action_chooseServiceConfigFragment_to_createStoreConfigFragment)
        }
    }

    private fun getStoreCategories() {
        viewModel.setStateEvent(
            ConfigStateEvent.GetStoreCategoriesEvent()
        )
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
    }
}