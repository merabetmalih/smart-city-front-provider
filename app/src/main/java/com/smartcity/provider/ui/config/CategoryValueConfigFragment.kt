package com.smartcity.provider.ui.config

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.smartcity.provider.R
import com.smartcity.provider.ui.config.viewmodel.getSelectedCategory
import com.smartcity.provider.ui.config.viewmodel.getSelectedStoreCategories
import com.smartcity.provider.ui.config.viewmodel.setSelectedStoreCategories
import com.smartcity.provider.ui.main.account.information.adapters.CategoriesValueAdapter
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_category_value_config.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class CategoryValueConfigFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseConfigFragment(R.layout.fragment_category_value_config,viewModelFactory),
    CategoriesValueAdapter.Interaction{

    private lateinit var categoriesValueAdapter: CategoriesValueAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        submitList()
    }

    private fun submitList() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            categoriesValueAdapter.submitList(
                viewModel.getSelectedCategory()!!.subCategorys,
                viewModel.getSelectedStoreCategories()
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
                    this@CategoryValueConfigFragment
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

    override fun onItemSelected(item: String) {
        val category=viewModel.getSelectedStoreCategories().find { it.id == viewModel.getSelectedCategory()!!.id }
        val list=viewModel.getSelectedStoreCategories()

        if (category==null){
            val currentCategory=viewModel.getSelectedCategory()!!.copy()
            currentCategory.subCategorys= listOf(item)
            list.add(currentCategory)
            viewModel.setSelectedStoreCategories(list)
        }else{
            list.remove(category)

            val listt=category.subCategorys.toMutableList()
            listt.add(item)
            category.subCategorys=listt

            list.add(category)
            viewModel.setSelectedStoreCategories(list)
        }
        categoriesValueAdapter.notifyDataSetChanged()
    }

    override fun onItemDeSelected(item: String) {
        val category=viewModel.getSelectedStoreCategories().find { it.id == viewModel.getSelectedCategory()!!.id }!!.copy()

        val valuesOfSelected=category.subCategorys.toMutableList()
        valuesOfSelected.remove(item)

        val list=viewModel.getSelectedStoreCategories().toMutableList()

        if(valuesOfSelected.isEmpty()){
            list.remove(
                viewModel.getSelectedStoreCategories().find { it.id == viewModel.getSelectedCategory()!!.id }!!
            )
            viewModel.setSelectedStoreCategories(list)
        }else{
            list.remove(
                viewModel.getSelectedStoreCategories().find { it.id == viewModel.getSelectedCategory()!!.id }!!
            )
            category.subCategorys=valuesOfSelected
            list.add(category)
            viewModel.setSelectedStoreCategories(list)
        }
        categoriesValueAdapter.notifyDataSetChanged()
    }
}