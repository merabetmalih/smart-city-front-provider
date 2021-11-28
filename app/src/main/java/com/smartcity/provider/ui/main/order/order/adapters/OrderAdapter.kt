package com.smartcity.provider.ui.main.order.order.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.models.product.Order
import com.smartcity.provider.models.product.OrderType
import com.smartcity.provider.util.Constants
import com.smartcity.provider.util.DateUtils.Companion.convertStringToStringDate
import com.smartcity.provider.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.layout_order_item_header.view.*


class OrderAdapter (
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private  var  orderProductRecyclerAdapter: OrderProductAdapter?=null

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Order>() {

        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return false
        }
    }

    private val differ =
        AsyncListDiffer(
            OrderRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )
    internal inner class OrderRecyclerChangeCallback(
        private val adapter: OrderAdapter
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
    ): OrderHolder {
        return OrderHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_order_item_header, parent, false),
            orderProductRecyclerAdapter = orderProductRecyclerAdapter,
            requestManager = requestManager,
            interaction=interaction
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OrderHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(attributeValueList: List<Order>?){
        val newList = attributeValueList?.toMutableList()
        differ.submitList(newList)
    }

    class OrderHolder(
        itemView: View,
        private var orderProductRecyclerAdapter: OrderProductAdapter?,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView),
        OrderProductAdapter.Interaction {

        private lateinit var order:Order

        fun initProductsRecyclerView(recyclerview:RecyclerView){
            recyclerview.apply {
                layoutManager = LinearLayoutManager(context)
                val topSpacingDecorator = TopSpacingItemDecoration(0)
                removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
                addItemDecoration(topSpacingDecorator)

                orderProductRecyclerAdapter =
                    OrderProductAdapter(
                        requestManager,
                        this@OrderHolder
                    )
                addOnScrollListener(object: RecyclerView.OnScrollListener(){

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    }
                })
                adapter = orderProductRecyclerAdapter
            }

        }

        @SuppressLint("SetTextI18n")
        fun bind(item: Order) = with(itemView) {
            order=item

            itemView.order_id.text=item.id.toString()

            itemView.order_time.text=convertStringToStringDate(item.createAt)

            itemView.order_type_delivery.visibility=View.GONE
            itemView.order_type_self_pickup.visibility=View.GONE
            when(item.orderType){
                OrderType.DELIVERY ->{
                    itemView.order_type.text="Delivery"
                    itemView.order_type_delivery.visibility=View.VISIBLE
                }

                OrderType.SELFPICKUP ->{
                    itemView.order_type.text="Self pickup"
                    itemView.order_type_self_pickup.visibility=View.VISIBLE
                    itemView.order_delivery_address_container.visibility=View.GONE
                }
            }

            itemView.order_receiver.text="${item.firstName} ${item.lastName} born in ${item.birthDay}"

            item.address?.let {item->
                itemView.order_delivery_address.text="${item.city}, ${item.street}, ${item.houseNumber.toString()}, ${item.zipCode.toString()}"
            }

            initProductsRecyclerView(itemView.products_order_recycler_view)
            orderProductRecyclerAdapter?.let {
                it.submitList(
                    item.orderProductVariants.sortedBy { it.productVariant.price }.take(2)
                )
            }

            if(item.orderProductVariants.size>2){
                itemView.order_more_products.visibility=View.VISIBLE
                order_remaining_products.text=(item.orderProductVariants.size-2).toString()
            }

            itemView.order_product_quantity.text= item.orderProductVariants.size.toString()

            itemView.order_product_total.text=item.bill!!.total.toString()+ Constants.DOLLAR
            itemView.order_product_paid.text=item.bill!!.alreadyPaid.toString()+ Constants.DOLLAR
            itemView.order_product_rest.text=(item.bill!!.total-item.bill!!.alreadyPaid).toString()+ Constants.DOLLAR


            itemView.setOnClickListener {
                interaction?.selectedOrder(item)
            }
            itemView.order_more_products.setOnClickListener{
                interaction?.selectedOrder(item)
            }

            /*setPopUpWindow(item)
            itemView.test.setOnClickListener {
                mypopupWindow.showAsDropDown(it,-153,0)

            }*/


        }

        override fun selectedProduct() {
            interaction?.selectedOrder(order)
        }

        lateinit var mypopupWindow:PopupWindow
        fun setPopUpWindow(order: Order) {

            val inflater: LayoutInflater = LayoutInflater.from(itemView.context)
            val view: View = inflater.inflate(R.layout.dialog_popup, null)
             mypopupWindow = PopupWindow(view,RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT,true)

            val start=view.findViewById<RelativeLayout>(R.id.btn1)
            start.setOnClickListener {

            }
        }



    }

    interface Interaction {
        fun selectedOrder(item:Order)
    }
}