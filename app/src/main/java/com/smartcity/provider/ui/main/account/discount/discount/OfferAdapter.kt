package com.smartcity.provider.ui.main.account.discount.discount

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.smartcity.provider.R
import com.smartcity.provider.models.Offer
import com.smartcity.provider.models.OfferState
import com.smartcity.provider.models.OfferType
import com.smartcity.provider.util.Constants.Companion.DOLLAR
import com.smartcity.provider.util.DateUtils.Companion.convertStringToStringDateSimpleFormat
import kotlinx.android.synthetic.main.layout_offer_list_item.view.*

class OfferAdapter (
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Offer>() {

        override fun areItemsTheSame(oldItem: Offer, newItem: Offer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Offer, newItem: Offer): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            CustomCategoryRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class CustomCategoryRecyclerChangeCallback(
        private val adapter: OfferAdapter
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
    ): OfferHolder {
        return OfferHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_offer_list_item, parent, false),
            interaction = interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OfferHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(productVariantsList: List<Offer>?){
        val newList = productVariantsList?.toMutableList()
        differ.submitList(newList)
    }

    class OfferHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Offer) = with(itemView) {

            itemView.offer_name.text=item.discountCode

            val topBottom=0
            val rightLeft=25
            when(item.offerState){
                OfferState.EXPIRED  ->{
                    offer_state.text="Expired"
                    itemView.offer_state.background= androidx.core.content.res.ResourcesCompat.getDrawable(resources,
                        R.drawable.radius_button_grey,null)
                    itemView.offer_state.setPadding(rightLeft,topBottom,rightLeft,topBottom)
                }
                OfferState.ACTIVE ->{
                    offer_state.text="Active"
                    itemView.offer_state.background= androidx.core.content.res.ResourcesCompat.getDrawable(resources,
                        R.drawable.radius_button_green,null)
                    itemView.offer_state.setPadding(rightLeft,topBottom,rightLeft,topBottom)
                }
                OfferState.PLANNED ->{
                    offer_state.text="Planned"
                    itemView.offer_state.background= androidx.core.content.res.ResourcesCompat.getDrawable(resources,
                        R.drawable.radius_button_yellow,null)
                    itemView.offer_state.setPadding(rightLeft,topBottom,rightLeft,topBottom)
                }
            }

            when(item.type){
                OfferType.PERCENTAGE->{
                    itemView.discount_value.text= "${item.percentage}%"
                }

                OfferType.FIXED->{
                    itemView.discount_value.text= "${item.newPrice}${DOLLAR}"
                }
            }

            itemView.discount_product_number.text="${item.products!!.size}"

            itemView.discount_date.text="${convertStringToStringDateSimpleFormat(item.startDate!!)} - ${convertStringToStringDateSimpleFormat(item.endDate!!)}"

            itemView.setOnClickListener {
                interaction?.onItemSelected(item)
            }
        }
    }

    interface Interaction {
        fun onItemSelected(item: Offer)
    }
}