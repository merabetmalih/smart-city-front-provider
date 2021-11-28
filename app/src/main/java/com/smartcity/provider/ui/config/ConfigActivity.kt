package com.smartcity.provider.ui.config

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.smartcity.provider.BaseApplication
import com.smartcity.provider.R
import com.smartcity.provider.fragments.config.ConfigNavHostFragment
import com.smartcity.provider.ui.BaseActivity
import com.smartcity.provider.ui.config.viewmodel.ConfigViewModel
import com.smartcity.provider.ui.config.viewmodel.getStoreCategories
import com.smartcity.provider.ui.main.MainActivity
import com.smartcity.provider.util.PreferenceKeys
import com.smartcity.provider.util.StateMessageCallback
import com.smartcity.provider.util.SuccessHandling
import kotlinx.android.synthetic.main.activity_config.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
import javax.inject.Named

@FlowPreview
@ExperimentalCoroutinesApi
class ConfigActivity: BaseActivity()
{
    @Inject
    @Named("ConfigFragmentFactory")
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    @Inject
    @Named("GetSharedPreferences")
    lateinit var sharedPreferences: SharedPreferences

    val viewModel: ConfigViewModel by viewModels {
        providerFactory
    }

    override fun inject() {
        (application as BaseApplication).configComponent()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        subscribeObservers()
        onRestoreInstanceState()
    }

    override fun displayProgressBar(bool: Boolean) {
        if(bool){
            progress_bar.visibility = View.VISIBLE
        }
        else{
            progress_bar.visibility = View.GONE
        }
    }

    override fun displayFragmentContainerView() {
        onFinishCheckPreviousAuthUser()
    }

    override fun expandAppBar() {
        // ignore
    }

    override fun displayBottomNavigation(bool: Boolean) {
        // ignore
    }

    override fun updateStatusBarColor(statusBarColor: Int, statusBarTextColor: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            ContextCompat.getColor(this, statusBarColor)
            window.statusBarColor = ContextCompat.getColor(this, statusBarColor)
            if(statusBarTextColor){
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }else{
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    fun onRestoreInstanceState(){
        val host = supportFragmentManager.findFragmentById(R.id.config_fragments_container)
        host?.let {
            // do nothing
        } ?: createNavHost()
    }

    private fun createNavHost(){
        val navHost = ConfigNavHostFragment.create(
            R.navigation.config_nav_graph
        )
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.config_fragments_container,
                navHost,
                getString(R.string.ConfigNavHost)
            )
            .setPrimaryNavigationFragment(navHost)
            .commit()
    }

    private fun subscribeObservers(){
        val interestCenter=sharedPreferences.getBoolean(PreferenceKeys.CREATE_STORE_FLAG, false)
        if(interestCenter){
            navMainActivity()
        }

        viewModel.stateMessage.observe(this, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.STORE_CATEGORIES_DONE)){//after save interest center navigate to main
                    onResponseReceived(
                        response = it.response,
                        stateMessageCallback = object: StateMessageCallback {
                            override fun removeMessageFromStack() {
                                viewModel.clearStateMessage()
                            }
                        }
                    )
                    navMainActivity()
                }
            }
        })

        viewModel.numActiveJobs.observe(this, Observer { jobCounter ->//must
            displayProgressBar(viewModel.areAnyJobsActive())
        })

        viewModel.viewState.observe(this, Observer{ viewState ->
            viewModel.getStoreCategories()?.let {
                if(it.isNotEmpty()){
                    saveCreateStoreFlag(sharedPreferences)
                    navMainActivity()
                }else{
                    onFinishCheckPreviousAuthUser()
                }
            }
        })
    }

    private fun saveCreateStoreFlag(sharedPreferences: SharedPreferences) {
        val editor=sharedPreferences.edit()
        editor.putBoolean(PreferenceKeys.CREATE_STORE_FLAG,true)
        editor.apply()
    }

    private fun onFinishCheckPreviousAuthUser(){
        fragment_container.visibility = View.VISIBLE
    }

    fun navMainActivity(){
        Log.d(TAG, "navMainActivity: called.")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        (application as BaseApplication).releaseConfigComponent()
    }
}