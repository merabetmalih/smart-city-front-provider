package com.smartcity.provider.ui.main.store

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.smartcity.provider.R
import com.smartcity.provider.models.OfferType
import com.smartcity.provider.models.product.AttributeValue
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.models.product.ProductVariants
import com.smartcity.provider.ui.main.custom_category.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.custom_category.viewProduct.adapters.OptionsAdapter
import com.smartcity.provider.ui.main.custom_category.viewProduct.adapters.ValuesAdapter
import com.smartcity.provider.ui.main.custom_category.viewProduct.adapters.VariantImageAdapter
import com.smartcity.provider.ui.main.custom_category.viewProduct.adapters.ViewPagerAdapter
import com.smartcity.provider.ui.main.custom_category.viewmodel.getViewProductFields
import com.smartcity.provider.ui.main.store.state.STORE_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.store.state.StoreViewState
import com.smartcity.provider.ui.main.store.viewmodel.clearChoisesMap
import com.smartcity.provider.ui.main.store.viewmodel.getChoisesMap
import com.smartcity.provider.ui.main.store.viewmodel.getViewProductFields
import com.smartcity.provider.ui.main.store.viewmodel.setChoisesMap
import com.smartcity.provider.util.Constants
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_view_product.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class ViewProductFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseStoreFragment(R.layout.fragment_view_product,viewModelFactory),
    OptionsAdapter.Interaction,
    VariantImageAdapter.Interaction
{
    private lateinit var dialogView: View
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var product: Product
    private lateinit var  variantImageRecyclerAdapter: VariantImageAdapter
    private lateinit var  optionsRecyclerAdapter: OptionsAdapter
    private lateinit var optionsRecyclerview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[STORE_VIEW_STATE_BUNDLE_KEY] as StoreViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }

    fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        uiCommunicationListener.expandAppBar()
        uiCommunicationListener.displayBottomNavigation(false)

        viewModel.getViewProductFields()?.let {
            product=it
        }

        initViewPager()
        setNewPrice(product_new_price)
        setDiscountValue()
        setName()
        setOptions()
        setDescription()
        variantsDialog()
        subscribeObservers()
        backProceed()
        editProduct()
    }

    private fun editProduct() {
        edit_button.visibility = View.GONE
    }

    private fun backProceed() {
        back_button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun getVariantValues (productVariants: ProductVariants):Map<String,String>{
        val variantValues:MutableMap<String, String> = mutableMapOf()
        productVariants.productVariantAttributeValuesProductVariant.map {
            variantValues.put(it.attributeValue.attribute,it.attributeValue.value)
        }
        return variantValues
    }

    fun getExistedOptionValue(map: Map<String, String>):List<AttributeValue>{
        var result= mutableListOf<AttributeValue>()
        val temp=mutableListOf<AttributeValue>()
        val current=mutableListOf<AttributeValue>()

        if(map.isNotEmpty()){
            map.map {choices->
                current.add(AttributeValue(choices.value,choices.key))

                product.productVariants.map {
                    val list= mutableListOf<AttributeValue>()
                    it.productVariantAttributeValuesProductVariant.map {
                        list.add(it.attributeValue)
                    }
                    list.map {//same option
                        if (it.attribute == choices.key){
                            temp.add(it)
                        }
                    }

                    if (list.containsAll(current)){
                        temp.addAll(list)
                    }

                }
                if(result.isEmpty()){
                    result=temp
                }
                result=result.intersect(temp).toMutableList()
                temp.clear()
            }
        }
        else{
            product.productVariants.map {
                it.productVariantAttributeValuesProductVariant.map {
                    result.add(it.attributeValue)
                }
            }
        }
        return result.distinct()
    }

    fun sortMapByList(map: Map<String, String>, sortedList:List<String>):Map<String,String>{
        val resultSortedMap= mutableMapOf<String,String>()
        if (map.isNotEmpty()){
            sortedList.map {
                if(map.containsKey(it)){
                    resultSortedMap[it] = map[it]!!
                }
            }
        }
        return resultSortedMap
    }



    private fun showDetailedProduct(map: Map<String, String>){
        product.let {
            for (variant in it.productVariants){
                if(getVariantValues(variant) == map){

                    if (variant.image.isNullOrEmpty()){//variant without images
                        product.let {
                            val image= Constants.PRODUCT_IMAGE_URL +it.images.first().image
                            if (variant.offer==null){
                                setVariantDialog(
                                    "${variant.price}${Constants.DOLLAR}",
                                    variant.unit.toString(),
                                    image
                                )
                            }else{

                                val offer=variant.offer
                                var price=0.0
                                when(offer!!.type){
                                    OfferType.FIXED ->{
                                        price=variant.price-offer.newPrice!!
                                    }

                                    OfferType.PERCENTAGE ->{
                                        price=BigDecimal(variant.price-(variant.price*offer.percentage!!/100)).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                                    }
                                }

                                setVariantDialog(
                                    "${price}${Constants.DOLLAR}",
                                    variant.unit.toString(),
                                    image
                                )

                            }

                        }
                    }else{
                        if (variant.offer==null){
                            setVariantDialog(
                                "${variant.price}${Constants.DOLLAR}",
                                variant.unit.toString(),
                                Constants.PRODUCT_IMAGE_URL +variant.image!!
                            )
                        }else{
                            val offer=variant.offer
                            var price=0.0
                            when(offer!!.type){
                                OfferType.FIXED ->{
                                    price=variant.price-offer.newPrice!!
                                }

                                OfferType.PERCENTAGE ->{
                                    price=BigDecimal(variant.price-(variant.price*offer.percentage!!/100)).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                                }
                            }

                            setVariantDialog(
                                "${price}${Constants.DOLLAR}",
                                variant.unit.toString(),
                                Constants.PRODUCT_IMAGE_URL +variant.image!!
                            )
                        }


                    }

                    return@let
                }
            }

            setVariantDialog(
                "0",
                "0",
                "0"
            )
        }
    }

    private fun showDefaultProduct(){
        product.let {
            val image= Constants.PRODUCT_IMAGE_URL +it.images.first().image

            if(it.attributes.isEmpty()){
                setVariantDialog(getPrice(),it.productVariants.first().unit.toString(),image)
            }else{
                setVariantDialog(getPrice(),"0",image)
            }

        }
    }



    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            val map=viewModel.getChoisesMap()
            val sortedOption= mutableListOf<String>()
            product.attributes.map {
                sortedOption.add(it.name)
            }

            val resultSortedMap= sortMapByList(map,sortedOption)

            //show only valid option value
            ValuesAdapter.setAvailableOptionValue(getExistedOptionValue(resultSortedMap))

            if (map.isNotEmpty()){
                if(map.size==product.attributes.size){
                    showDetailedProduct(map)
                }else{
                    //Log.d("ii","map.isNotEmpty() else showDefaultProduct")
                    showDefaultProduct()
                }
            }

            /*if(this::dialogView.isInitialized){
                if(product.attributes.isEmpty()){
                    showDefaultProductWithQuantity(product.productVariants.first().unit.toString())
                }
            }*/


        })
    }

    private fun variantsDialog() {
        options_view.setOnClickListener {
            showVariantDialog()
        }
    }

    override fun onItemSelected() {
        showVariantDialog()
    }

    fun initOptionsRecyclerView(recyclerview:RecyclerView){
        recyclerview.apply {
            layoutManager = LinearLayoutManager(this@ViewProductFragment.context,
                LinearLayoutManager.VERTICAL, false)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)
            optionsRecyclerAdapter =
                OptionsAdapter(
                    this@ViewProductFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = optionsRecyclerAdapter
        }
    }

    fun setVariantDialog(price:String,quantity:String,image:String){
        dialogView.findViewById<TextView>(R.id.product_variant_price).text=price
        dialogView.findViewById<TextView>(R.id.product_variant_quantity).text=quantity
        requestManager
            .load(image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(dialogView.findViewById<ImageView>(R.id.product_variant_image))
    }


    fun showVariantDialog(){
        val dialog = Dialog(context!!, android.R.style.Theme_Light)
        dialog.window.setBackgroundDrawable( ColorDrawable(Color.parseColor("#99000000")))
        dialogView = layoutInflater.inflate(R.layout.dialog_variants, null)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)

        val rec=dialogView.findViewById<RecyclerView>(R.id.options_recyclerview_variant)
        optionsRecyclerview=rec
        initOptionsRecyclerView(rec)

        product.attributes.map {//init adapter positions
            ValuesAdapter.getMapSelectedPosition().put(it.name,-1)
            ValuesAdapter.getOldPosition().put(it.name,-1)
        }

        optionsRecyclerAdapter.submitList(
            product.attributes
        )

        val cancel=dialogView.findViewById<View>(R.id.cancel)
        cancel!!.setOnClickListener {
            dialog.dismiss()
        }

        //set default values
        showDefaultProduct()

        dialog.setOnDismissListener {
            viewModel.clearChoisesMap()
        }

        dialog.show()
    }

    private fun setDescription() {
        product_description.text=product.description
        expand_description.setOnClickListener {
            if (product_description.visibility==View.GONE){
                TransitionManager.beginDelayedTransition(card_view, AutoTransition())
                product_description.visibility=View.VISIBLE
                expand_description.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }else{
                TransitionManager.beginDelayedTransition(card_view, AutoTransition())
                product_description.visibility=View.GONE
                expand_description.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }
    }

    fun initVariantImageRecyclerView(){
        product_recyclerview_variant_image.apply {
            layoutManager = LinearLayoutManager(this@ViewProductFragment.context,LinearLayoutManager.HORIZONTAL, false)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            variantImageRecyclerAdapter =
                VariantImageAdapter(
                    requestManager,
                    this@ViewProductFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = variantImageRecyclerAdapter
        }

    }

    private fun setOptions() {
        if (product.attributes.isNotEmpty()){
            var options=""
            product.attributes.map {
                options=options+" , "+it.attributeValues.size.toString()+" "+it.name
            }
            product_attrebute.text=options.drop(2)
            initVariantImageRecyclerView()
            product.productVariants.map {

                it.image
            }

            val imagesList=product.productVariants.map {
                it.image
            }
            if (imagesList.contains(null)){
                product_recyclerview_variant_image.visibility=View.GONE
            }else{
                variantImageRecyclerAdapter.submitList(
                    product.productVariants.map {
                        it.image!!
                    }.distinct()
                )
            }

        }else{
            product_attrebute.text="Stock"
            //options_view.visibility=View.GONE
            //options_view_separatore.visibility=View.GONE
        }

    }

    private fun setName() {
        product_name.text=product.name
    }

    @SuppressLint("SetTextI18n")
    private fun setNewPrice(view: View) {
        val prices = mutableListOf<Double>()
        product.productVariants.map {
            val offer=it.offer
            if (offer!=null){
                when(offer.type){
                    OfferType.PERCENTAGE ->{
                        prices.add(BigDecimal(it.price-(it.price*offer.percentage!!/100)).setScale(2, RoundingMode.HALF_EVEN).toDouble())
                    }

                    OfferType.FIXED ->{
                        prices.add(it.price-offer.newPrice!!)
                    }
                    null -> {}
                }
            }else{
                prices.add(it.price)
            }
        }


        if(prices.maxOrNull() != prices.minOrNull()){
            (view as TextView).text= "${Constants.DOLLAR} ${prices.minOrNull()} - ${prices.maxOrNull()}"
        }else{
            (view as TextView).text= "${prices.maxOrNull()}${Constants.DOLLAR}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setOldPrice(){
        product_old_price.visibility=View.VISIBLE
        val prices =product.productVariants.map { productVariant -> productVariant.price }

        if(prices.maxOrNull() != prices.minOrNull()){
            product_old_price.text= "${Constants.DOLLAR} ${prices.minOrNull()} - ${prices.maxOrNull()}"
        }else{
            product_old_price.text= "${prices.maxOrNull()}${Constants.DOLLAR}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDiscountValue(){
        val percentages= mutableListOf<Int>()
        val fixed = mutableListOf<Double>()
        product.productVariants.map {
            val offer=it.offer
            if(offer!=null){
                when(offer.type){
                    OfferType.PERCENTAGE ->{
                        percentages.add(offer.percentage!!)
                    }

                    OfferType.FIXED ->{
                        fixed.add(offer.newPrice!!)
                    }
                }
            }
        }

        if(percentages.isNotEmpty()){
            discount_percentage.visibility=View.VISIBLE
            discount_percentage.text="-${percentages.maxOrNull()}%"
            setOldPrice()
        }


        if(fixed.isNotEmpty()){
            discount_fixed.visibility=View.VISIBLE
            discount_fixed.text="-${fixed.maxOrNull()} ${Constants.DOLLAR}"
            setOldPrice()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getPrice():String {
        val prices = mutableListOf<Double>()
        product.productVariants.map {
            val offer=it.offer
            if (offer!=null){
                when(offer.type){
                    OfferType.PERCENTAGE ->{
                        prices.add(BigDecimal(it.price-(it.price*offer.percentage!!/100)).setScale(2, RoundingMode.HALF_EVEN).toDouble())
                    }

                    OfferType.FIXED ->{
                        prices.add(it.price-offer.newPrice!!)
                    }
                    null -> {}
                }
            }else{
                prices.add(it.price)
            }
        }

        if(prices.maxOrNull() != prices.minOrNull()){
            return "${Constants.DOLLAR} ${prices.minOrNull()} - ${prices.maxOrNull()}"
        }else{
            return "${prices.maxOrNull()}${Constants.DOLLAR}"
        }
    }


    private fun initViewPager() {
        viewPager = activity!!.findViewById(R.id.view_pager)
        viewPagerAdapter =
            ViewPagerAdapter(
                requestManager
            )

        val images=product.images.map { it.image }
        viewPagerAdapter.setImageUrls(images)
        viewPager.adapter = viewPagerAdapter

        if (images.size<2){
            dotsIndicator.visibility=View.GONE
        }
        dotsIndicator.setViewPager(view_pager)
        view_pager.adapter?.registerDataSetObserver(dotsIndicator.dataSetObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager.adapter=null
    }

    override fun onItemSelected(option: String, value: String) {
        optionsRecyclerview.adapter!!.notifyDataSetChanged()
        val map=viewModel.getChoisesMap()
        if(map[option]==value){
            map.remove(option)
        }else{
            map.put(option,value)
        }
        viewModel.setChoisesMap(map)
    }
}