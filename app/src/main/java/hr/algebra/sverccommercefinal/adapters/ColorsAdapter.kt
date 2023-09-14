package hr.algebra.sverccommercefinal.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import hr.algebra.sverccommercefinal.databinding.ColorRvItemBinding

/**
 * Adapter class for populating a RecyclerView with a list of color items.
 * This adapter is designed to display a list of colors, allowing the selection of a single color.
 *
 * @property selectedPosition: The position of the currently selected color item.
 */
class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ColorsViewHolder>() {

    // The position of the currently selected color item. Defaulted to -1 (no color selected).
    private var selectedPosition = -1

    /**
     * ViewHolder class for individual color items within the RecyclerView.
     *
     * @param binding: ColorRvItemBinding representing the layout of each color item.
     */
    inner class ColorsViewHolder(val binding: ColorRvItemBinding) : ViewHolder(binding.root) {

        /**
         * Binds a color to the ViewHolder, updating its appearance based on whether it is selected or not.
         *
         * @param color: The color (integer) to be displayed.
         * @param position: The position of the color item in the dataset.
         */
        fun bind(color: Int, position: Int) {
            // Create a ColorDrawable based on the color integer.
            val imageDrawable = ColorDrawable(color)
            binding.imageColor.setImageDrawable(imageDrawable)

            if (position == selectedPosition) {
                binding.apply {
                    // Color is selected, show the shadow and selected indicators.
                    imageShadow.visibility = View.VISIBLE
                    imageSelected.visibility = View.VISIBLE
                }
            } else {
                // Color is not selected, hide the shadow and selected indicators.
                binding.apply {
                    imageShadow.visibility = View.INVISIBLE
                    imageSelected.visibility = View.INVISIBLE
                }
            }
        }
    }

    /**
     * Callback for calculating the differences between two lists of color items.
     */
    private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            // Check if items (colors) are the same based on their content.
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            // Check if the contents of items (colors) are the same.
            return oldItem == newItem
        }
    }

    // AsyncListDiffer used to manage and calculate differences in the dataset.
    val differ = AsyncListDiffer(this, diffCallback)

    /**
     * Creates and returns a ViewHolder for a color item within the RecyclerView.
     *
     * @param parent: The parent ViewGroup where the ViewHolder will be attached.
     * @param viewType: The type of view to be created (unused in this case).
     * @return A ColorsViewHolder for a color item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {
        return ColorsViewHolder(
            ColorRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    /**
     * Returns the total number of color items in the dataset.
     *
     * @return The number of color items in the dataset.
     */
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    /**
     * Binds a color item to a ViewHolder at the specified position.
     * Also handles item click events and notifies the click listener when an item is clicked.
     *
     * @param holder: The ViewHolder to bind the color item to.
     * @param position: The position of the color item in the dataset.
     */
    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color, position)

        // Handle item click events.
        holder.itemView.setOnClickListener {
            // If a color was previously selected, notify the change.
            if (selectedPosition >= 0) {
                notifyItemChanged(selectedPosition)
            }
            // Update the selected position and notify the item change.
            selectedPosition = holder.adapterPosition
            notifyItemChanged(position)
            // Invoke the onItemClick callback with the selected color.
            onItemClick?.invoke(color)
        }
    }

    // Callback for item click events, invoked when a color is clicked.
    var onItemClick: ((Int) -> Unit)? = null
}
