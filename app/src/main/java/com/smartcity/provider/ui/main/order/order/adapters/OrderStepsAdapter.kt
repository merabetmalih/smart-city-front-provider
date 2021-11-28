package com.smartcity.provider.ui.main.order.order.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.*
import com.smartcity.provider.R
import kotlinx.android.synthetic.main.layout_order_steps_item.view.*


class OrderStepsAdapter (
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        var selectedStepPosition = 0

        fun getSelectedStepPositions():Int{
            return selectedStepPosition
        }

        fun setSelectedStepPositions(position:Int){
            selectedStepPosition =position
        }
    }

    fun resetSelectedStepPosition(){
        selectedStepPosition =0
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Pair<String,Int>>() {

        override fun areItemsTheSame(oldItem: Pair<String,Int>, newItem: Pair<String,Int>): Boolean {
            return oldItem.first == newItem.first
        }

        override fun areContentsTheSame(oldItem: Pair<String,Int>, newItem: Pair<String,Int>): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            OrderStepsRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class OrderStepsRecyclerChangeCallback(
        private val adapterView: OrderStepsAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapterView.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapterView.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapterView.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            adapterView.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return OrderStepsHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_order_steps_item, parent, false),
            interaction = interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OrderStepsHolder -> {
                holder.bind(differ.currentList.get(position),position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(orderStepsList: List<Pair<String,Int>>?){
        val newList = orderStepsList?.toMutableList()
        differ.submitList(newList)
    }

    class OrderStepsHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {


        fun bind(item:Pair<String,Int>,position: Int) = with(itemView) {


            if(selectedStepPosition == position){
                itemView.order_step_background.background = ResourcesCompat.getDrawable(resources,R.drawable.selected_order_step,null)
            }
            else{
                itemView.order_step_background.background = null
            }

            itemView.setOnClickListener {
                selectedStepPosition =position
                interaction?.onStepItemSelected(bindingAdapterPosition,item.first)
            }

            itemView.order_step_icon.background=androidx.core.content.res.ResourcesCompat.getDrawable(resources,
                item.second,null)

            itemView.order_step_text.text=item.first

        }
    }

    interface Interaction {
        fun onStepItemSelected(position: Int, item: String)
    }
    
}