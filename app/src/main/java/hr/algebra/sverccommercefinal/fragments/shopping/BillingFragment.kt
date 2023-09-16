package hr.algebra.sverccommercefinal.fragments.shopping

import android.app.AlertDialog
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
import hr.algebra.sverccommercefinal.adapters.AddressAdapter
import hr.algebra.sverccommercefinal.adapters.BillingProductsAdapter
import hr.algebra.sverccommercefinal.data.Address
import hr.algebra.sverccommercefinal.data.CartProduct
import hr.algebra.sverccommercefinal.data.Order
import hr.algebra.sverccommercefinal.data.OrderStatus
import hr.algebra.sverccommercefinal.databinding.FragmentBillingBinding
import hr.algebra.sverccommercefinal.util.HorizontalItemDecoration
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.viewmodel.BillingViewModel
import hr.algebra.sverccommercefinal.viewmodel.OrderViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Fragment responsible for displaying billing information and user addresses during the checkout process.
 *
 * @property binding: Lazily inflated view binding for this fragment's layout.
 * @property addressAdapter: Adapter for managing user addresses in a RecyclerView.
 * @property billingProductsAdapter: Adapter for managing billing products in a RecyclerView.
 * @property billingViewModel: ViewModel for managing user addresses.
 * @property args: Arguments passed to this fragment, including billing products and total price.
 * @property products: List of billing products to be displayed.
 * @property totalPrice: Total price of the billing products.
 * @property selectedAddress: The user-selected address for billing.
 * @property orderViewModel: ViewModel for managing order placement.
 */
@AndroidEntryPoint
class BillingFragment : Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f
    private var selectedAddress: Address? = null
    private val orderViewModel by viewModels<OrderViewModel>()

    /**
     * Called when the fragment is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve billing products and total price from arguments.
        products = args.products.toList()
        totalPrice = args.totalPrice
    }

    /**
     * Called to create and return the view hierarchy associated with the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    /**
     * Called after the fragment's view has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerViews for user addresses and billing products.
        setupBillingProductsRv()
        setupAddressRv()

        // Hide some UI elements if the fragment is not used for payment.
        if (!args.payment) {
            binding.apply {
                buttonPlaceOrder.visibility = View.INVISIBLE
                totalBoxContainer.visibility = View.INVISIBLE
                middleLine.visibility = View.INVISIBLE
                bottomLine.visibility = View.INVISIBLE
            }
        }

        // Handle the close button click to navigate back.
        binding.imageCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }

        // Handle click to add a new address.
        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        // Observe and update user addresses in the RecyclerView.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            billingViewModel.address.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        // Observe and handle the result of the order placement process.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(), "Order placed successfully!", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        // Submit billing products to the adapter and display total price.
        billingProductsAdapter.differ.submitList(products)
        binding.tvTotalPrice.text = "€ ${String.format("%.2f", totalPrice)}"

        // Handle address selection.
        addressAdapter.onClick = {
            selectedAddress = it
            if (!args.payment) {
                val b = Bundle().apply { putParcelable("address", selectedAddress) }
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment, b)
            }
        }

        // Handle the place order button click.
        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                Toast.makeText(requireContext(), "Please select an address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showOrderConfirmationDialog()
        }
    }

    /**
     * Show the order confirmation dialog and initiate the order placement process.
     */
    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order items")
            setMessage("Do you want to complete this order?")
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes") { dialog, _ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    /**
     * Set up the RecyclerView for user addresses.
     */
    private fun setupAddressRv() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    /**
     * Set up the RecyclerView for billing products.
     */
    private fun setupBillingProductsRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = billingProductsAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}


