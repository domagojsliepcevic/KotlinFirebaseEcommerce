package hr.algebra.sverccommercefinal.adapters


import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.data.Address
import hr.algebra.sverccommercefinal.databinding.AddressRvItemBinding



/**
 * Adapter class responsible for displaying a list of addresses.
 */
class AddressAdapter : Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(val binding: AddressRvItemBinding) :
        ViewHolder(binding.root) {

        /**
         * Binds address data to the ViewHolder's views and handles the selection state.
         *
         * @param address: The address to be displayed in the item.
         * @param isSelected: Whether the address is currently selected.
         */
        fun bind(address: Address, isSelected: Boolean) {
            binding.apply {
                buttonAddress.text = address.addressTitle
                if (isSelected) {
                    @Suppress("DEPRECATION")
                    buttonAddress.background =
                        ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                } else {
                    @Suppress("DEPRECATION")
                    buttonAddress.background =
                        ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
                }
            }
        }
    }

    // Callback for calculating the differences between old and new address items in the list.
    private val diffCallback = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            // Check if the unique identifiers of old and new items are the same.
            return oldItem.addressTitle == newItem.addressTitle && oldItem.fullName == newItem.fullName
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        // Inflate the item layout and create a ViewHolder for it.
        return AddressViewHolder(
            AddressRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    /**
     * Returns the current item count in the list.
     */
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // Keeps track of the currently selected address item.
    var selectedAddress = -1

    /**
     * Binds data to the ViewHolder at the specified position and handles item clicks.
     *
     * @param holder: The ViewHolder to bind data to.
     * @param position: The position of the item in the list.
     */
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, selectedAddress == position)

        holder.binding.buttonAddress.setOnClickListener {
            if (selectedAddress >= 0) {
                // Notify the previous selected item to update its appearance.
                notifyItemChanged(selectedAddress)
            }
            // Update the selected address and notify the clicked item to update its appearance.
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            // Invoke the click callback.
            onClick?.invoke(address)
        }
    }

    /**
     * Initializes the adapter and adds a listener to update the selected item when the data changes.
     */
    init {
        // Add a list listener to notify the selected item to update its appearance when data changes.
        differ.addListListener { _, _ ->
            notifyItemChanged(selectedAddress)
        }
    }

    // Callback to handle item clicks.
    var onClick: ((Address) -> Unit)? = null
}
