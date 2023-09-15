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
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.adapters.AddressAdapter
import hr.algebra.sverccommercefinal.adapters.BillingProductsAdapter
import hr.algebra.sverccommercefinal.data.CartProduct
import hr.algebra.sverccommercefinal.databinding.FragmentBillingBinding
import hr.algebra.sverccommercefinal.util.HorizontalItemDecoration
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.viewmodel.BillingViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Fragment responsible for displaying billing information and user addresses during the checkout process.
 *
 * @property binding: Lazily inflated view binding for this fragment's layout.
 * @property addressAdapter: Adapter for managing user addresses in a RecyclerView.
 * @property billingProductsAdapter: Adapter for managing billing products in a RecyclerView.
 * @property viewModel: ViewModel for managing user addresses.
 * @property args: Arguments passed to this fragment, including billing products and total price.
 * @property products: List of billing products to be displayed.
 * @property totalPrice: Total price of the billing products.
 */
@AndroidEntryPoint
class BillingFragment : Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val viewModel by viewModels<BillingViewModel>()
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProduct>()
    private var totalPrice = 0f

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
            viewModel.address.collectLatest {
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

        // Submit billing products to the adapter and display total price.
        billingProductsAdapter.differ.submitList(products)
        binding.tvTotalPrice.text = "€ ${String.format("%.2f", totalPrice)}"
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
