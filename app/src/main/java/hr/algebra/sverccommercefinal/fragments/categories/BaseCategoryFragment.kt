package hr.algebra.sverccommercefinal.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.adapters.BestProductAdapter
import hr.algebra.sverccommercefinal.databinding.FragmentBaseCategoryBinding
import hr.algebra.sverccommercefinal.util.showBottomNavigationView

/**
 * Base fragment for displaying category-specific products. All other category fragments inherit from this base fragment.
 *
 * This base fragment provides common functionality for displaying products, such as best products and offers,
 * within category-specific fragments.
 */
open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding

    // Adapter for displaying offers. Lazily initialized to improve performance.
    protected val offerAdapter: BestProductAdapter by lazy { BestProductAdapter() }

    // Adapter for displaying best products. Lazily initialized to improve performance.
    protected val bestProductAdapter: BestProductAdapter by lazy { BestProductAdapter() }

    /**
     * Inflates the layout for this fragment and initializes the binding.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    /**
     * Initializes the RecyclerViews for displaying offers and best products.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView for best products.
        setupBestProductsRv()

        // Set up RecyclerView for offers.
        setupOfferRv()

        // Set an onClick listener for best product items to navigate to the product details page.
        bestProductAdapter.onClick = { product ->
            val bundle = Bundle().apply { putParcelable("product", product) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        // Set an onClick listener for offer items to navigate to the product details page.
        offerAdapter.onClick = { product ->
            val bundle = Bundle().apply { putParcelable("product", product) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        // Set up an `OnScrollListener` for the `rvOffer` RecyclerView to handle paging.
        binding.rvOffer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Check if the user has reached the end of the offers RecyclerView.
                if (!recyclerView.canScrollVertically(1) && dx != 0) {
                    onOfferPagingRequest()
                }
            }
        })

        // Set up an `OnScrollChangeListener` for the `nestedScrollBaseCategory` view to handle paging of best products.
        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                onBestProductsPagingRequest()
            }
        })
    }

    /**
     * Show loading indicator for offers.
     */
    fun showOfferLoading() {
        binding.offerProductsProgressBar.visibility = View.VISIBLE
    }

    /**
     * Hide loading indicator for offers.
     */
    fun hideOfferLoading() {
        binding.offerProductsProgressBar.visibility = View.GONE
    }

    /**
     * Show loading indicator for best products.
     */
    fun showBestProductsLoading() {
        binding.bestProductsProgressBar.visibility = View.VISIBLE
    }

    /**
     * Hide loading indicator for best products.
     */
    fun hideBestProductsLoading() {
        binding.bestProductsProgressBar.visibility = View.GONE
    }

    /**
     * Callback function for handling offer paging requests.
     * Subclasses can override this function to implement custom paging behavior.
     */
    open fun onOfferPagingRequest() {
        // Implement custom paging behavior for offers if needed.
    }

    /**
     * Callback function for handling best products paging requests.
     * Subclasses can override this function to implement custom paging behavior.
     */
    open fun onBestProductsPagingRequest() {
        // Implement custom paging behavior for best products if needed.
    }

    /**
     * Sets up the RecyclerView and its adapter for displaying best products.
     */
    private fun setupBestProductsRv() {
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductAdapter
        }
    }

    /**
     * Sets up the RecyclerView and its adapter for displaying offers.
     */
    private fun setupOfferRv() {
        binding.rvOffer.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter
        }
    }

    /**
     * Callback for when the fragment is resumed. Show the bottom navigation view.
     */
    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}


