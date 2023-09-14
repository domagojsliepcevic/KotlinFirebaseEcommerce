package hr.algebra.sverccommercefinal.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.sverccommercefinal.databinding.SizeRvItemBinding

/**
 * Adapter class for populating a RecyclerView with a list of size items.
 * This adapter is designed to display a list of sizes, allowing the selection of a single size.
 */
class SizesAdapter : RecyclerView.Adapter<SizesAdapter.SizesViewHolder>() {

    // The position of the currently selected size item. Defaulted to -1 (no size selected).
    private var selectedPosition = -1

    /**
     * ViewHolder class for individual size items within the RecyclerView.
     *
     * @param binding: SizeRvItemBinding representing the layout of each size item.
     */
    inner class SizesViewHolder(val binding: SizeRvItemBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds a size to the ViewHolder, updating its appearance based on whether it is selected or not.
         *
         * @param size: The size (string) to be displayed.
         * @param position: The position of the size item in the dataset.
         */
        fun bind(size: String, position: Int) {
            // Set the size text in the TextView.
            binding.tvSize.text = size

            if (position == selectedPosition) {
                binding.apply {
                    // Size is selected, show the shadow indicator.
                    imageShadow.visibility = View.VISIBLE
                }
            } else {
                // Size is not selected, hide the shadow indicator.
                binding.apply {
                    imageShadow.visibility = View.INVISIBLE
                }
            }
        }
    }

    /**
     * Callback for calculating the differences between two lists of size items.
     */
    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            // Check if items (sizes) are the same based on their content.
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            // Check if the contents of items (sizes) are the same.
            return oldItem == newItem
        }
    }

    // AsyncListDiffer used to manage and calculate differences in the dataset.
    val differ = AsyncListDiffer(this, diffCallback)

    /**
     * Creates and returns a ViewHolder for a size item within the RecyclerView.
     *
     * @param parent: The parent ViewGroup where the ViewHolder will be attached.
     * @param viewType: The type of view to be created (unused in this case).
     * @return A SizesViewHolder for a size item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesViewHolder {
        return SizesViewHolder(
            SizeRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    /**
     * Returns the total number of size items in the dataset.
     *
     * @return The number of size items in the dataset.
     */
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    /**
     * Binds a size item to a ViewHolder at the specified position.
     * Also handles item click events and notifies the click listener when an item is clicked.
     *
     * @param holder: The ViewHolder to bind the size item to.
     * @param position: The position of the size item in the dataset.
     */
    override fun onBindViewHolder(holder: SizesViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.bind(size, position)

        // Handle item click events.
        holder.itemView.setOnClickListener {
            // If a size was previously selected, notify the change.
            if (selectedPosition >= 0) {
                notifyItemChanged(selectedPosition)
            }
            // Update the selected position and notify the item change.
            selectedPosition = holder.adapterPosition
            notifyItemChanged(position)
            // Invoke the onItemClick callback with the selected size.
            onItemClick?.invoke(size)
        }
    }

    // Callback for item click events, invoked when a size is clicked.
    var onItemClick: ((String) -> Unit)? = null
}
