package com.smartcity.provider.ui.main.account.information

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.smartcity.provider.R
import com.smartcity.provider.ui.main.account.BaseAccountFragment
import com.smartcity.provider.ui.main.account.information.adapters.CategoriesValueAdapter
import com.smartcity.provider.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.account.state.AccountViewState
import com.smartcity.provider.ui.main.account.viewmodel.*
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_category_value.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class CategoryValueFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_category_value,viewModelFactory),
    CategoriesValueAdapter.Interaction{

    private lateinit var categoriesValueAdapter: CategoriesValueAdapter

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
        submitList()
    }

    private fun submitList() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            categoriesValueAdapter.submitList(
                viewModel.getSelectedCategory()!!.subCategorys,
                viewModel.getSelectedCategoriesList()
                    .find { it.id == viewModel.getSelectedCategory()!!.id}
            )
        })
    }

    private fun initRecyclerView() {
        category_value_recyclerview.apply {
            layoutManager = FlexboxLayoutManager(context)
            (layoutManager as FlexboxLayoutManager).justifyContent = JustifyContent.CENTER
            (layoutManager as FlexboxLayoutManager).flexWrap= FlexWrap.WRAP

            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            categoriesValueAdapter =
                CategoriesValueAdapter(
                    this@CategoryValueFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = categoriesValueAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearSelectedProductToSelectVariant()
    }

    override fun onItemSelected(item: String) {
        val category=viewModel.getSelectedCategoriesList().find { it.id == viewModel.getSelectedCategory()!!.id }
        val list=viewModel.getSelectedCategoriesList()

        if (category==null){
            val currentCategory=viewModel.getSelectedCategory()!!.copy()
            currentCategory.subCategorys= listOf(item)
            list.add(currentCategory)
            viewModel.setSelectedCategoriesList(list)
        }else{
            list.remove(category)

            val listt=category.subCategorys.toMutableList()
            listt.add(item)
            category.subCategorys=listt

            list.add(category)
            viewModel.setSelectedCategoriesList(list)
        }
        categoriesValueAdapter.notifyDataSetChanged()
    }

    override fun onItemDeSelected(item: String) {
        val category=viewModel.getSelectedCategoriesList().find { it.id == viewModel.getSelectedCategory()!!.id }!!.copy()

        val valuesOfSelected=category.subCategorys.toMutableList()
        valuesOfSelected.remove(item)

        val list=viewModel.getSelectedCategoriesList().toMutableList()

        if(valuesOfSelected.isEmpty()){
            list.remove(
                viewModel.getSelectedCategoriesList().find { it.id == viewModel.getSelectedCategory()!!.id }!!
            )
            viewModel.setSelectedCategoriesList(list)
        }else{
            list.remove(
                viewModel.getSelectedCategoriesList().find { it.id == viewModel.getSelectedCategory()!!.id }!!
            )
            category.subCategorys=valuesOfSelected
            list.add(category)
            viewModel.setSelectedCategoriesList(list)
        }
        categoriesValueAdapter.notifyDataSetChanged()
    }
}