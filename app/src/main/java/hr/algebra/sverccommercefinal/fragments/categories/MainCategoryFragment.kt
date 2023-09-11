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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.R
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
 * @property viewModel: ViewModel responsible for managing special category products data.
 */
private val TAG = "MainCategoryFragment"

@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
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
     * Initializes the RecyclerView, observes the special category products data,
     * and updates the UI accordingly.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()

        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Show loading progress.
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
}
