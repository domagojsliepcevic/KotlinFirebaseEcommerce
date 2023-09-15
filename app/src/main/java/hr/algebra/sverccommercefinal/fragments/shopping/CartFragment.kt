package hr.algebra.sverccommercefinal.fragments.shopping


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.adapters.CartProductAdapter
import hr.algebra.sverccommercefinal.databinding.FragmentCartBinding
import hr.algebra.sverccommercefinal.firebase.FirebaseCommon
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.util.VerticalItemDecoration
import hr.algebra.sverccommercefinal.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Fragment responsible for displaying the user's shopping cart.
 *
 * @property binding: Lazily inflated view binding for this fragment's layout.
 * @property cartAdapter: Adapter for managing cart product items in a RecyclerView.
 * @property viewModel: ViewModel for managing the user's shopping cart.
 */
class CartFragment : Fragment(R.layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductAdapter() }

    // ViewModel for managing the user's shopping cart.
    val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the RecyclerView for the cart.
        setupCartRv()

        var totalPrice = 0f

        // Handle the close button click to navigate back.
        binding.imageCloseCart.setOnClickListener {
            findNavController().navigateUp()
        }

        // Observe and update the total price of cart products.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest { price ->
                price?.let {
                    totalPrice = it
                    binding.tvTotalPrice.text = "€ ${String.format("%.2f", price)}"
                }
            }
        }

        // Handle item click to navigate to product details.
        cartAdapter.onProductClick = {
            val b = Bundle().apply { putParcelable("product", it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, b)
        }

        // Handle click to increase the quantity of a cart product.
        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }

        // Handle click to decrease the quantity of a cart product.
        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }

        binding.buttonCheckout.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToBillingFragment(totalPrice,cartAdapter.differ.currentList.toTypedArray())
            findNavController().navigate(action)
        }

        // Observe and display a delete confirmation dialog.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from cart")
                    setMessage("Do you want to delete this item from your cart?")
                    setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Yes") { dialog, _ ->
                        viewModel.deleteCartProduct(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }

        // Observe and update the cart products list.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarCart.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        if (it.data!!.isEmpty()) {
                            showEmptyCart()
                            hideOtherViews()
                        } else {
                            hideEmptyCart()
                            showOtherViews()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    /**
     * Shows the necessary views when the cart is not empty.
     */
    private fun showOtherViews() {
        binding.apply {
            // Make the RecyclerView, totalBoxContainer, and checkout button visible.
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    /**
     * Hides the unnecessary views when the cart is not empty.
     */
    private fun hideOtherViews() {
        binding.apply {
            // Hide the RecyclerView, totalBoxContainer, and checkout button.
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    /**
     * Hides the view indicating an empty cart.
     */
    private fun hideEmptyCart() {
        binding.apply {
            // Hide the empty cart view.
            layoutCartEmpty.visibility = View.GONE
        }
    }

    /**
     * Shows the view indicating an empty cart.
     */
    private fun showEmptyCart() {
        binding.apply {
            // Show the empty cart view.
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    /**
     * Sets up the RecyclerView for displaying cart products.
     */
    private fun setupCartRv() {
        binding.rvCart.apply {
            // Configure the RecyclerView with a vertical LinearLayoutManager.
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            // Set the adapter to the CartProductAdapter for managing cart product items.
            adapter = cartAdapter

            // Add a vertical item decoration for spacing between items.
            addItemDecoration(VerticalItemDecoration())
        }
    }

}

