package com.smartcity.provider.ui.main.account.policy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.smartcity.provider.R
import com.smartcity.provider.models.TaxRange
import kotlinx.android.synthetic.main.layout_deposit_range_list_item.view.*

class DepositRangeAdapter (
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TaxRange>() {

        override fun areItemsTheSame(oldItem: TaxRange, newItem: TaxRange): Boolean {
            return oldItem.startRange == newItem.startRange
        }

        override fun areContentsTheSame(oldItem: TaxRange, newItem: TaxRange): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            DepositRangeRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class DepositRangeRecyclerChangeCallback(
        private val adapter: DepositRangeAdapter
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
    ): DepositRangeHolder {

        return DepositRangeHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_deposit_range_list_item, parent, false),
            interaction = interaction
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DepositRangeHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(attributeValueList: Set<TaxRange>?){
        val newList = attributeValueList?.toMutableList()
        differ.submitList(newList)
    }

    class DepositRangeHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {


        fun bind(item: TaxRange) = with(itemView) {

            itemView.setOnClickListener {
                interaction?.onItemSelected(bindingAdapterPosition,item)
            }
            itemView.delete_deposit_range.setOnClickListener {
                interaction?.onItemSelected(bindingAdapterPosition,item)
            }
            itemView.deposit_range_start.text=item.startRange.toString()
            itemView.deposit_range_end.text=item.endRange.toString()
            itemView.deposit_range_amount.text=item.fixAmount.toString()
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: TaxRange)
    }
}