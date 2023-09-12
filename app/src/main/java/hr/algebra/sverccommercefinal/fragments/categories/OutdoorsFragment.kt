package hr.algebra.sverccommercefinal.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.data.Category
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.viewmodel.CategoryViewModel
import hr.algebra.sverccommercefinal.viewmodel.factory.BaseCategoryViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject
/**
 * Fragment responsible for displaying Outdoors category products.
 *
 * This fragment inherits from [BaseCategoryFragment] and is annotated with [AndroidEntryPoint]
 * to enable Hilt dependency injection.
 */
@AndroidEntryPoint
class OutdoorsFragment : BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore // Injected instance of FirebaseFirestore for database access.

    // View model for managing Outdoors category data.
    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Outdoors)
    }

    /**
     * Called when the fragment's view is created. Sets up observers for offer and best products data.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        // Show loading progress for offer products.
                        showOfferLoading()
                    }
                    is Resource.Success -> {
                        // Update the offer products RecyclerView with the retrieved data and hide loading progress.
                        offerAdapter.differ.submitList(it.data)
                        hideOfferLoading()
                    }
                    is Resource.Error -> {
                        // Display an error message using Snackbar and hide loading progress for offer products.
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                        hideOfferLoading()
                    }
                    else -> Unit
                }
            }
        }

        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        // Show loading progress for best products.
                        showBestProductsLoading()
                    }
                    is Resource.Success -> {
                        // Update the best products RecyclerView with the retrieved data and hide loading progress.
                        bestProductAdapter.differ.submitList(it.data)
                        hideBestProductsLoading()
                    }
                    is Resource.Error -> {
                        // Display an error message using Snackbar and hide loading progress for best products.
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                        hideBestProductsLoading()
                    }
                    else -> Unit
                }
            }
        }
    }
}
