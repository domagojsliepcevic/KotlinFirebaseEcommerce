package hr.algebra.sverccommercefinal.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.sverccommercefinal.adapters.ColorsAdapter
import hr.algebra.sverccommercefinal.adapters.SizesAdapter
import hr.algebra.sverccommercefinal.adapters.ViewPagerToImages
import hr.algebra.sverccommercefinal.databinding.FragmentProductDetailsBinding
import hr.algebra.sverccommercefinal.util.hideBottomNavigationView

/**
 * Fragment class responsible for displaying detailed information about a product.
 */
class ProductDetailsFragment : Fragment() {

    // Arguments passed to this fragment via navigation.
    private val args by navArgs<ProductDetailsFragmentArgs>()

    // Binding for this fragment's layout.
    private lateinit var binding: FragmentProductDetailsBinding

    // Lazy-initialized adapters for ViewPager, sizes RecyclerView, and colors RecyclerView.
    private val viewPagerAdapter by lazy { ViewPagerToImages() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }

    /**
     * Inflates the layout for this fragment, hides the bottom navigation view, and initializes the binding.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    /**
     * Handles the creation of the fragment's view and sets up various UI components.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the product from the arguments passed during navigation.
        val product = args.product

        // Initialize RecyclerViews and ViewPager.
        setupSizesRv()
        setupColorsRv()
        setupViewPager()

        // Handle the close button click to navigate back.
        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            // Populate the UI with product details.
            tvProductName.text = product.name
            tvProductPrice.text = "€ ${product.price}"
            tvProductDescription.text = product.description

            // Hide the "Colors" text view if there are no colors available for the product.
            if (product.colors.isNullOrEmpty()) {
                tvProductColors.visibility = View.INVISIBLE
            }

            // Hide the "Sizes" text view if there are no sizes available for the product.
            if (product.sizes.isNullOrEmpty()) {
                tvProductSize.visibility = View.INVISIBLE
            }
        }

        // Submit data to adapters for ViewPager, colors RecyclerView, and sizes RecyclerView.
        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let {
            colorsAdapter.differ.submitList(it)
        }

        product.sizes?.let {
            sizesAdapter.differ.submitList(it)
        }
    }

    /**
     * Sets up the ViewPager for displaying product images.
     */
    private fun setupViewPager() {
        binding.viewPagerProductImages.adapter = viewPagerAdapter
    }

    /**
     * Sets up the RecyclerView for displaying colors.
     */
    private fun setupColorsRv() {
        binding.rvColors.apply {
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    /**
     * Sets up the RecyclerView for displaying sizes.
     */
    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
}
