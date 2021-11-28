package com.smartcity.provider.ui.main.account.discount.addProductDiscount.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.smartcity.provider.R
import com.smartcity.provider.models.CustomCategory
import kotlinx.android.synthetic.main.layout_custom_category_discount_list_item.view.*

class CustomCategoryDiscountAdapter  (
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CustomCategory>() {

        override fun areItemsTheSame(oldItem: CustomCategory, newItem: CustomCategory): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: CustomCategory, newItem: CustomCategory): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            CustomCategoryRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class CustomCategoryRecyclerChangeCallback(
        private val adapter: CustomCategoryDiscountAdapter
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
    ): CustomCategoryDiscountHolder {
        return CustomCategoryDiscountHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_custom_category_discount_list_item, parent, false),
            interaction = interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CustomCategoryDiscountHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(customCategoryList: List<CustomCategory>?){
        val newList = customCategoryList?.toMutableList()
        differ.submitList(newList)
    }

    class CustomCategoryDiscountHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: CustomCategory) = with(itemView) {
            itemView.custom_category_discount_name.text=item.name

            itemView.setOnClickListener {
                interaction?.onItemSelected(bindingAdapterPosition,item)
            }
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: CustomCategory)
    }
}