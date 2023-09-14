package hr.algebra.sverccommercefinal.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hr.algebra.sverccommercefinal.data.Product
import hr.algebra.sverccommercefinal.databinding.SpecialRvItemBinding

/**
 * RecyclerView adapter for displaying special products in a list.
 *
 * @property differ: AsyncListDiffer responsible for calculating the differences between old and new lists.
 */
class SpecialProductsAdapter : RecyclerView.Adapter<SpecialProductsAdapter.SpecialProductsViewHolder>() {

    /**
     * Inner ViewHolder class representing a single item in the RecyclerView.
     *
     * @property binding: View binding for the item layout.
     */
    inner class SpecialProductsViewHolder(private val binding: SpecialRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the data of a product to the ViewHolder's views.
         *
         * @param product: The product to be displayed in the item.
         */
        fun bind(product: Product) {
            binding.apply {
                // Load the product's image into the ImageView using Glide.
                Glide.with(itemView).load(product.images[0]).into(imageSpecialRvItem)
                // Set the product's name and price to the corresponding TextViews.
                tvSpecialProductName.text = product.name
                tvSpecialProductPrice.text = product.price.toString()
            }
        }
    }

    /**
     * Callback for calculating the differences between old and new items in the list.
     */
    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            // Check if the unique identifiers of old and new items are the same.
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductsViewHolder {
        // Inflate the item layout and create a ViewHolder for it.
        return SpecialProductsViewHolder(
            SpecialRvItemBinding.inflate(
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
    override fun onBindViewHolder(holder: SpecialProductsViewHolder, position: Int) {
        // Get the product at the current position and bind it to the ViewHolder.
        val product = differ.currentList[position]
        holder.bind(product)

        // Set an OnClickListener to handle item clicks.
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    // Callback to handle item clicks.
    var onClick: ((Product) -> Unit)? = null
}

