package hr.algebra.sverccommercefinal.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.adapters.BestProductAdapter
import hr.algebra.sverccommercefinal.databinding.FragmentBaseCategoryBinding

/**
 * Base fragment for displaying category-specific products.
 * All other category fragments inherit from this base fragment.
 */
open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    private lateinit var offerAdapter: BestProductAdapter
    private lateinit var bestProductAdapter: BestProductAdapter

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
    }

    /**
     * Sets up the RecyclerView and its adapter for displaying best products.
     */
    private fun setupBestProductsRv() {
        bestProductAdapter = BestProductAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductAdapter
        }
    }

    /**
     * Sets up the RecyclerView and its adapter for displaying offers.
     */
    private fun setupOfferRv() {
        offerAdapter = BestProductAdapter()
        binding.rvBestProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter
        }
    }
}
