package hr.algebra.sverccommercefinal.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hr.algebra.sverccommercefinal.data.CartProduct
import hr.algebra.sverccommercefinal.databinding.CartProductItemBinding
import hr.algebra.sverccommercefinal.helper.getProductPrice

/**
 * RecyclerView adapter for displaying products in the user's shopping cart.
 */
class CartProductAdapter : RecyclerView.Adapter<CartProductAdapter.CartProductsViewHolder>() {

    /**
     * Inner ViewHolder class representing a single item in the RecyclerView.
     *
     * @property binding: View binding for the item layout.
     */
    inner class CartProductsViewHolder(val binding: CartProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the data of a cart product to the ViewHolder's views.
         *
         * @param cartProduct: The cart product to be displayed in the item.
         */
        fun bind(cartProduct: CartProduct) {
            binding.apply {
                // Load the cart product's image into the ImageView using Glide.
                Glide.with(itemView).load(cartProduct.product.images[0]).into(imageCartProduct)
                // Set the cart product's name and quantity to the corresponding TextViews.
                tvProductCartName.text = cartProduct.product.name
                tvCartProductQuantity.text = cartProduct.quantity.toString()

                // Calculate the price after applying the offer percentage, if available.
                val priceAfterPercentage = cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)
                tvProductCartPrice.text = "€ ${String.format("%.2f", priceAfterPercentage)}"

                // Set the selected color and size for the cart product.
                imageCartProductColor.setImageDrawable(ColorDrawable(cartProduct.selectedColor ?: Color.TRANSPARENT))
                tvCartProductSize.text = cartProduct.selectedSize ?: ""
                imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    /**
     * Callback for calculating the differences between old and new items in the list.
     */
    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            // Check if the unique identifiers of old and new items are the same.
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        // Inflate the item layout and create a ViewHolder for it.
        return CartProductsViewHolder(
            CartProductItemBinding.inflate(
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
    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        // Get the cart product at the current position and bind it to the ViewHolder.
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)

        // Set an OnClickListener to handle item clicks.
        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cartProduct)
        }

        // Set an OnClickListener to handle plus button clicks.
        holder.binding.imagePlus.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }

        // Set an OnClickListener to handle minus button clicks.
        holder.binding.imageMinus.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }
    }

    // Callbacks to handle item clicks and quantity change clicks.
    var onProductClick: ((CartProduct) -> Unit)? = null
    var onPlusClick: ((CartProduct) -> Unit)? = null
    var onMinusClick: ((CartProduct) -> Unit)? = null
}
