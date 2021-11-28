package com.smartcity.provider.ui.main.account.discount.addProductDiscount.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.smartcity.provider.R
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.util.Constants
import kotlinx.android.synthetic.main.layout_product_simple_discount_selction_list_item.view.*
import kotlinx.android.synthetic.main.layout_product_variant_discount_selction_list_item.view.*


class ProductDiscountAdapter (
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_WITH_VARIANT = 0
    private val VIEW_TYPE_SIMPLE = 1

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return (oldItem.name == newItem.name).and(oldItem.description == newItem.description)
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            ProductRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class  ProductRecyclerChangeCallback(
        private val adapter: ProductDiscountAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (differ.currentList.get(position).attributes.isEmpty()) VIEW_TYPE_SIMPLE else VIEW_TYPE_WITH_VARIANT
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):  RecyclerView.ViewHolder {
        when(viewType){
            VIEW_TYPE_WITH_VARIANT ->{
                return ProductVariantsHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(
                            R.layout.layout_product_variant_discount_selction_list_item,
                            parent,
                            false
                        ),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }

            VIEW_TYPE_SIMPLE ->{
                return ProductSimpleHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(
                            R.layout.layout_product_simple_discount_selction_list_item,
                            parent,
                            false
                        ),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }

            else ->{
                return ProductVariantsHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(
                            R.layout.layout_product_variant_discount_selction_list_item,
                            parent,
                            false
                        ),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductSimpleHolder -> {
                holder.bind(differ.currentList.get(position),selectedProduct)
            }

            is ProductVariantsHolder -> {
                holder.bind(differ.currentList.get(position),selectedProduct)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    lateinit var selectedProduct:List<Product>

    fun submitList(productList: List<Product>?,selectedProduct:List<Product>){
        val newList = productList?.toMutableList()
        this.selectedProduct=selectedProduct
        differ.submitList(newList)
    }

    class ProductSimpleHolder(
        itemView: View,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        val TAG: String = "AppDebug"
        @SuppressLint("SetTextI18n")
        fun bind(item: Product,selectedProduct:List<Product>) = with(itemView) {

            val image= Constants.PRODUCT_IMAGE_URL +item.images.first().image
            Log.d(TAG,image)
            requestManager
                .load(image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.product_image_simple)

            val name=item.name
            name.replace("\n","").replace("\r","")
            if(name.length>70){
                itemView.product_name_simple.text=name.subSequence(0,70).padEnd(73,'.')
            }else{
                itemView.product_name_simple.text=name
            }
            itemView.product_price.text=item.productVariants.first().price.toString()+ Constants.DOLLAR

            itemView.product_check_box_simple.isChecked = item.id in selectedProduct.map { it.id }

            itemView.product_check_box_simple.setOnCheckedChangeListener { compoundButton, b ->
                when(b){
                    true ->{
                        interaction?.onItemSelected(item)
                    }

                    false ->{
                        interaction?.onItemDeSelected(item)
                    }
                }
            }
        }
    }

    class ProductVariantsHolder(
        itemView: View,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        val TAG: String = "AppDebug"
        @SuppressLint("SetTextI18n")
        fun bind(item: Product,selectedProduct:List<Product>) = with(itemView) {

            val image= Constants.PRODUCT_IMAGE_URL +item.images.first().image
            Log.d(TAG,image)
            requestManager
                .load(image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.product_image)

            val name=item.name
            name.replace("\n","").replace("\r","")
            if(name.length>70){

                itemView.product_name.text=name.subSequence(0,70).padEnd(73,'.')
            }else{
                itemView.product_name.text=name
            }
            itemView.product_variants_number.text=item.productVariants.size.toString()

            itemView.product_check_box.isChecked=item.id in selectedProduct.map { it.id }

            itemView.product_check_box.setOnCheckedChangeListener { compoundButton, b ->
                when(b){
                    true ->{
                        interaction?.onItemSelected(item)
                    }

                    false ->{
                        interaction?.onItemDeSelected(item)
                    }
                }
            }

            itemView.setOnClickListener {
                interaction?.onItemSelectedNavigate(item)
            }

            itemView.product_display_variants.setOnClickListener {
                interaction?.onItemSelectedNavigate(item)
            }
        }
    }

    interface Interaction {
        fun onItemSelected(item: Product)
        fun onItemDeSelected(item: Product)
        fun onItemSelectedNavigate(item: Product)
    }
}