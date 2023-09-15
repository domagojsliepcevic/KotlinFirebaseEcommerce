package hr.algebra.sverccommercefinal.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.data.Order
import hr.algebra.sverccommercefinal.data.OrderStatus
import hr.algebra.sverccommercefinal.data.getOrderStatus
import hr.algebra.sverccommercefinal.databinding.OrderItemBinding


/**
 * Adapter for displaying a list of user orders in a RecyclerView.
 */
class AllOrdersAdapter : RecyclerView.Adapter<AllOrdersAdapter.OrdersViewHolder>() {

    /**
     * Inner ViewHolder class representing a single item in the RecyclerView.
     *
     * @property binding: View binding for the item layout.
     */
    inner class OrdersViewHolder(private val binding: OrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Bind data to the ViewHolder.
         *
         * @param order: The order object to bind.
         */
        @Suppress("DEPRECATION")
        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = order.orderId.toString()
                tvOrderDate.text = order.date
                val resources = itemView.resources

                // Set color drawable based on order status.
                val colorDrawable = when (getOrderStatus(order.orderStatus)) {
                    is OrderStatus.Ordered -> {
                        ColorDrawable(resources.getColor(R.color.g_orange_yellow))
                    }
                    is OrderStatus.Confirmed -> {
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }
                    is OrderStatus.Delivered -> {
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }
                    is OrderStatus.Shipped -> {
                        ColorDrawable(resources.getColor(R.color.g_green))
                    }
                    is OrderStatus.Canceled -> {
                        ColorDrawable(resources.getColor(R.color.g_red))
                    }
                    is OrderStatus.Returned -> {
                        ColorDrawable(resources.getColor(R.color.g_red))
                    }
                }

                imageOrderState.setImageDrawable(colorDrawable)

                // Set a click listener for the item.
                itemView.setOnClickListener {
                    onClick?.invoke(order)
                }
            }
        }
    }

    /**
     * Callback for calculating the differences between old and new items in the list.
     */
    private val diffCallback = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            // Check if the unique identifiers of old and new items are the same.
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            // Check if the content of old and new items is the same (full equality check).
            return oldItem == newItem
        }
    }

    // Initialize an AsyncListDiffer with the diffCallback.
    val differ = AsyncListDiffer(this, diffCallback)

    /**
     * Creates a new ViewHolder by inflating the item layout.
     *
     * @param parent: The parent ViewGroup.
     * @param viewType: The type of view.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        // Inflate the item layout and create a ViewHolder for it.
        return OrdersViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    /**
     * Returns the current item count in the list.
     */
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    /**
     * Binds data to the ViewHolder at the specified position.
     *
     * @param holder: The ViewHolder to bind data to.
     * @param position: The position of the item in the list.
     */
    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)
    }

    /**
     * Callback for item click events.
     */
    var onClick: ((Order) -> Unit)? = null
}
