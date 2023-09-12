package hr.algebra.sverccommercefinal.viewmodel.factory


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import hr.algebra.sverccommercefinal.data.Category
import hr.algebra.sverccommercefinal.viewmodel.CategoryViewModel

/**
 * Factory class responsible for creating instances of [CategoryViewModel].
 *
 * @property firestore: Injected instance of FirebaseFirestore for accessing Firestore database.
 * @property category: The category for which [CategoryViewModel] instances will be created.
 */
class BaseCategoryViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of [CategoryViewModel] with the provided [firestore] and [category].
     *
     * @param modelClass The class of the ViewModel to be created.
     * @return An instance of [CategoryViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(firestore, category) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
