package hr.algebra.sverccommercefinal.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.sverccommercefinal.adapters.BillingProductsAdapter
import hr.algebra.sverccommercefinal.data.OrderStatus
import hr.algebra.sverccommercefinal.data.getOrderStatus
import hr.algebra.sverccommercefinal.databinding.FragmentOrderDetailBinding
import hr.algebra.sverccommercefinal.util.VerticalItemDecoration

/**
 * Fragment responsible for displaying the details of a specific order.
 */
class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val args by navArgs<OrderDetailFragmentArgs>()

    /**
     * Called to create and return the view hierarchy associated with the fragment.
     *
     * @param inflater: The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container: The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState: If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root view of the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    /**
     * Called after the fragment's view has been created.
     *
     * @param view: The root view of the fragment.
     * @param savedInstanceState: If non-null, this fragment is being re-constructed from a previous saved state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the order details from navigation arguments.
        val order = args.order

        // Set up the RecyclerView for displaying order products.
        setupOrderRv()

        // Populate the order details in the layout.
        binding.apply {
            tvOrderId.text = "Order#${order.orderId}"
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status
                )
            )

            // Determine the current order state and update the step view.
            val currentOrderState = when (getOrderStatus(order.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }

            stepView.go(currentOrderState, false)

            // Mark the order as "done" if it's delivered.
            if (currentOrderState == 3) {
                stepView.done(true)
            }

            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.city} ${order.address.street}"
            tvPhoneNumber.text = order.address.phone
            tvTotalPrice.text = "€ ${String.format("%.2f", order.totalPrice)}"
        }

        // Submit the order products to the adapter for display.
        billingProductsAdapter.differ.submitList(order.products)
    }

    /**
     * Set up the RecyclerView for displaying order products.
     */
    private fun setupOrderRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = billingProductsAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}
