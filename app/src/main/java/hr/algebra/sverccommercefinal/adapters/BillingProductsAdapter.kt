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
import hr.algebra.sverccommercefinal.databinding.BillingProductsRvItemBinding
import hr.algebra.sverccommercefinal.helper.getProductPrice

/**
 * Adapter class responsible for displaying cart products in the billing screen.
 *
 * @property differ: AsyncListDiffer used for calculating and managing item differences.
 */
class BillingProductsAdapter : RecyclerView.Adapter<BillingProductsAdapter.BillingProductsViewHolder>() {

    inner class BillingProductsViewHolder(val binding: BillingProductsRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the data of a cart product to the ViewHolder's views.
         *
         * @param billingProduct: The cart product to be displayed in the item.
         */
        fun bind(billingProduct: CartProduct) {
            binding.apply {
                // Load the product's image into the ImageView using Glide.
                Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)

                // Set the product's name and quantity to the corresponding TextViews.
                tvProductCartName.text = billingProduct.product.name
                tvBillingProductQuantity.text = billingProduct.quantity.toString()

                // Calculate the price after applying the offer percentage, if available.
                val priceAfterPercentage =
                    billingProduct.product.offerPercentage.getProductPrice(billingProduct.product.price)
                tvProductCartPrice.text = "€ ${String.format("%.2f", priceAfterPercentage)}"

                // Set the selected color and size for the cart product.
                imageCartProductColor.setImageDrawable(
                    ColorDrawable(billingProduct.selectedColor ?: Color.TRANSPARENT)
                )
                tvCartProductSize.text = billingProduct.selectedSize ?: ""
                imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    // DiffUtil callback for calculating item differences.
    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            // Check if the items represent the same product.
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            // Check if the contents of old and new items are the same (full equality check).
            return oldItem == newItem
        }
    }

    // AsyncListDiffer used to manage item differences efficiently.
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductsViewHolder {
        // Inflate the item layout and create a ViewHolder for it.
        return BillingProductsViewHolder(
            BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BillingProductsViewHolder, position: Int) {
        // Get the cart product at the current position and bind it to the ViewHolder.
        val billingProduct = differ.currentList[position]
        holder.bind(billingProduct)
    }
}
