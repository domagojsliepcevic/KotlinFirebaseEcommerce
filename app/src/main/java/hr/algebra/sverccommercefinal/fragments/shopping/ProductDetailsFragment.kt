package hr.algebra.sverccommercefinal.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.adapters.ColorsAdapter
import hr.algebra.sverccommercefinal.adapters.SizesAdapter
import hr.algebra.sverccommercefinal.adapters.ViewPagerToImages
import hr.algebra.sverccommercefinal.data.CartProduct
import hr.algebra.sverccommercefinal.databinding.FragmentProductDetailsBinding
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.util.hideBottomNavigationView
import hr.algebra.sverccommercefinal.viewmodel.DetailsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

/**
 * Fragment class responsible for displaying detailed information about a product.
 */
@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    // Arguments passed to this fragment via navigation.
    private val args by navArgs<ProductDetailsFragmentArgs>()

    // Binding for this fragment's layout.
    private lateinit var binding: FragmentProductDetailsBinding

    // Lazy-initialized adapters for ViewPager, sizes RecyclerView, and colors RecyclerView.
    private val viewPagerAdapter by lazy { ViewPagerToImages() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private var selectedColor: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<DetailsViewModel>()

    /**
     * Inflates the layout for this fragment, hides the bottom navigation view, and initializes the binding.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Hide the bottom navigation view when displaying product details.
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

        // Handle item clicks in the sizes RecyclerView.
        sizesAdapter.onItemClick = {
            selectedSize = it
        }

        // Handle item clicks in the colors RecyclerView.
        colorsAdapter.onItemClick = {
            selectedColor = it
        }

        // Handle the "Add to Cart" button click.
        binding.buttonAddToCart.setOnClickListener {
            val hasColors = !product.colors.isNullOrEmpty()
            val hasSizes = !product.sizes.isNullOrEmpty()

            val isValidSelection = (!hasColors || selectedColor != null) && (!hasSizes || selectedSize != null)

            if (isValidSelection) {
                // Add or update the selected product to the cart using the ViewModel.
                viewModel.addUpdateProductInCart(CartProduct(product, 1, selectedColor, selectedSize))
            } else {
                // Show a Snackbar error message when required options are not selected.
                Snackbar.make(view, "Error: Please select valid colors and sizes", Snackbar.LENGTH_SHORT).show()
            }
        }


        // Observe the addToCart flow from the ViewModel and update UI accordingly.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        // Show a loading animation when adding to cart.
                        binding.buttonAddToCart.startAnimation()
                    }
                    is Resource.Success -> {
                        // Update the button text and color when product is added to cart successfully.
                        binding.buttonAddToCart.revertAnimation()
                        binding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.g_green))
                        binding.buttonAddToCart.text = getString(R.string.product_added_to_cart)

                        // Delayed action to revert the button color and text.
                        delay(300) // Adjust the delay time as needed
                        binding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.g_blue))
                        binding.buttonAddToCart.text = getString(R.string.add_to_cart)
                    }
                    is Resource.Error -> {
                        // Handle errors when adding to cart and show a toast with the error message.
                        binding.buttonAddToCart.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        // Populate the UI with product details.
        binding.apply {
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

