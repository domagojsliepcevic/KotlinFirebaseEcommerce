package hr.algebra.sverccommercefinal.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.adapters.BestDealsAdapter
import hr.algebra.sverccommercefinal.adapters.BestProductAdapter
import hr.algebra.sverccommercefinal.adapters.SpecialProductsAdapter
import hr.algebra.sverccommercefinal.databinding.FragmentMainCategoryBinding
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.viewmodel.MainCategoryViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Fragment responsible for displaying special category products.
 *
 * @property TAG: A tag used for logging purposes.
 * @property specialProductsAdapter: Adapter for the RecyclerView displaying special products.
 * @property bestDealsAdapter: Adapter for the RecyclerView displaying best deals products.
 * @property bestProductAdapter: Adapter for the RecyclerView displaying best products.
 * @property viewModel: ViewModel responsible for managing special category products data.
 */
private val TAG = "MainCategoryFragment"

@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductAdapter: BestProductAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()

    /**
     * Inflates the layout for this fragment and initializes the binding.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    /**
     * Initializes the RecyclerViews, observes the special category products data,
     * best deals products data, and best products data, and updates the UI accordingly.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()
        setupBestDealsRv()
        setupBestProductRv()

        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Show loading progress for special products.
                        showLoading()
                    }
                    is Resource.Success -> {
                        // Update the RecyclerView with the list of special products and hide loading progress.
                        specialProductsAdapter.differ.submitList(resource.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        // Hide loading progress, log the error, and display a toast with the error message.
                        hideLoading()
                        Log.e(TAG, resource.message.toString())
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }

            viewModel.bestDealsProducts.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Show loading progress for best deals products.
                        showLoading()
                    }
                    is Resource.Success -> {
                        // Update the RecyclerView with the list of best deals products and hide loading progress.
                        bestDealsAdapter.differ.submitList(resource.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        // Hide loading progress, log the error, and display a toast with the error message.
                        hideLoading()
                        Log.e(TAG, resource.message.toString())
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }

            viewModel.bestProducts.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Show loading progress for best products.
                        showLoading()
                    }
                    is Resource.Success -> {
                        // Update the RecyclerView with the list of best products and hide loading progress.
                        bestProductAdapter.differ.submitList(resource.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        // Hide loading progress, log the error, and display a toast with the error message.
                        hideLoading()
                        Log.e(TAG, resource.message.toString())
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    /**
     * Hides the loading progress indicator.
     */
    private fun hideLoading() {
        binding.mainCategoryProgressBar.visibility = View.INVISIBLE
    }

    /**
     * Shows the loading progress indicator.
     */
    private fun showLoading() {
        binding.mainCategoryProgressBar.visibility = View.VISIBLE
    }

    /**
     * Sets up the RecyclerView and its adapter for displaying special products.
     */
    private fun setupSpecialProductsRv() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter
        }
    }

    /**
     * Sets up the RecyclerView and its adapter for displaying best deals products.
     */
    private fun setupBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    /**
     * Sets up the RecyclerView and its adapter for displaying best products.
     */
    private fun setupBestProductRv() {
        bestProductAdapter = BestProductAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductAdapter
        }
    }
}

