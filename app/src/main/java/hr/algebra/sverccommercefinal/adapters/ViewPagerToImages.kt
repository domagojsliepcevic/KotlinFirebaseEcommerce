package hr.algebra.sverccommercefinal.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import hr.algebra.sverccommercefinal.databinding.ViewpagerImageItemBinding

/**
 * Adapter class for populating a ViewPager with a list of image paths.
 *
 * This adapter is designed to work with a ViewPager to display a list of images in a gallery or slider.
 * It loads and displays images using Glide library for efficient image loading and caching.
 *
 * @param differ: AsyncListDiffer used for calculating and managing differences in the dataset.
 */
class ViewPagerToImages : RecyclerView.Adapter<ViewPagerToImages.ViewPagerToImagesViewHolder>() {

    /**
     * ViewHolder class for individual image items within the ViewPager.
     *
     * @param binding: ViewpagerImageItemBinding representing the layout of each image item.
     */
    inner class ViewPagerToImagesViewHolder(val binding: ViewpagerImageItemBinding) : ViewHolder(binding.root) {

        /**
         * Binds an image to the ViewHolder using Glide to load and display the image from the given imagePath.
         *
         * @param imagePath: The path or URL of the image to be loaded and displayed.
         */
        fun bind(imagePath: String) {
            Glide.with(itemView).load(imagePath).into(binding.imageProductDetails)
        }
    }

    /**
     * Callback for calculating the differences between two lists of image paths.
     */
    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            // Check if items (image paths) are the same based on their content.
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            // Check if the contents of items (image paths) are the same.
            return oldItem == newItem
        }
    }

    // AsyncListDiffer used to manage and calculate differences in the dataset.
    val differ = AsyncListDiffer(this, diffCallback)

    /**
     * Creates and returns a ViewHolder for an image item within the ViewPager.
     *
     * @param parent: The parent ViewGroup where the ViewHolder will be attached.
     * @param viewType: The type of view to be created (unused in this case).
     * @return A ViewPagerToImagesViewHolder for an image item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerToImagesViewHolder {
        return ViewPagerToImagesViewHolder(
            ViewpagerImageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    /**
     * Returns the total number of images in the dataset.
     *
     * @return The number of images in the dataset.
     */
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    /**
     * Binds an image item to a ViewHolder at the specified position.
     *
     * @param holder: The ViewHolder to bind the image item to.
     * @param position: The position of the image item in the dataset.
     */
    override fun onBindViewHolder(holder: ViewPagerToImagesViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }
}
