package com.smartcity.provider.ui.main.account.discount.addProductDiscount.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.smartcity.provider.R
import com.smartcity.provider.models.product.Product
import com.smartcity.provider.models.product.ProductVariants
import com.smartcity.provider.util.Constants
import kotlinx.android.synthetic.main.layout_product_simple_discount_selction_list_item.view.*

class ProductVariantAdapter (
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProductVariants>() {

        override fun areItemsTheSame(oldItem: ProductVariants, newItem: ProductVariants): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductVariants, newItem: ProductVariants): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            CustomCategoryRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class CustomCategoryRecyclerChangeCallback(
        private val adapter: ProductVariantAdapter
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductVariantDiscountHolder {
        return ProductVariantDiscountHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_product_simple_discount_selction_list_item, parent, false),
            interaction = interaction,
            requestManager = requestManager

        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductVariantDiscountHolder -> {
                holder.bind(differ.currentList.get(position),selectedProductVariants)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var selectedProductVariants:List<ProductVariants> = listOf()

    fun submitList(productVariantsList: List<ProductVariants>?,selectedProductVariants:Product?){
        val newList = productVariantsList?.toMutableList()
        selectedProductVariants?.let {
            this.selectedProductVariants=it.productVariants
        }
        differ.submitList(newList)
    }

    class ProductVariantDiscountHolder(
        itemView: View,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ProductVariants,selectedProductVariants:List<ProductVariants>) = with(itemView) {
            item.image?.let {
                val image= Constants.PRODUCT_IMAGE_URL +item.image
                requestManager
                    .load(image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(itemView.product_image_simple)
            }

            itemView.product_price.text=item.price.toString()+ Constants.DOLLAR

            var option=""
            item.productVariantAttributeValuesProductVariant.map {
                option=option+"/"+it.attributeValue.value
            }

            itemView.product_name_simple.text=option.drop(1)

            itemView.product_check_box_simple.isChecked = item in selectedProductVariants

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

    interface Interaction {
        fun onItemSelected(item: ProductVariants)
        fun onItemDeSelected(item: ProductVariants)
    }
}
