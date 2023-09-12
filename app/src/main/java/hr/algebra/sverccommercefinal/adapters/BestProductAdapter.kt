package hr.algebra.sverccommercefinal.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hr.algebra.sverccommercefinal.data.Product
import hr.algebra.sverccommercefinal.databinding.ProductRvItemBinding

/**
 * RecyclerView adapter for displaying best products in a list.
 *
 * @property differ: AsyncListDiffer responsible for calculating the differences between old and new lists.
 */
class BestProductAdapter: RecyclerView.Adapter<BestProductAdapter.BestProductViewHolder>(){

    /**
     * Inner ViewHolder class representing a single item in the RecyclerView.
     *
     * @property binding: View binding for the item layout.
     */
    inner class BestProductViewHolder(private val binding: ProductRvItemBinding):
        RecyclerView.ViewHolder(binding.root){

        /**
         * Binds the data of a product to the ViewHolder's views.
         *
         * @param product: The product to be displayed in the item.
         */
        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f -it
                    val priceAfterOffer = remainingPricePercentage * product.price
                    tvNewPrice.text = "$ ${String.format("%.2f",priceAfterOffer)}"
                    tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG

                }
                if (product.offerPercentage == null)
                    tvNewPrice.visibility == View.INVISIBLE
                tvPrice.text = "$ ${product.price}"
                tvName.text = product.name
            }

        }
    }

    /**
     * Callback for calculating the differences between old and new items in the list.
     */
    private val diffCallback = object: DiffUtil.ItemCallback<Product>(){
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductViewHolder {
        // Inflate the item layout and create a ViewHolder for it.
        return BestProductViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        // Return the current item count in the list.
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestProductViewHolder, position: Int) {
        // Get the product at the current position and bind it to the ViewHolder.
        val product = differ.currentList[position]
        holder.bind(product)
    }


}