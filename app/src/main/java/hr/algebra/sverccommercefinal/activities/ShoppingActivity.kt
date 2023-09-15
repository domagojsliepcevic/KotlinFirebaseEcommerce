package hr.algebra.sverccommercefinal.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.databinding.ActivityShoppingBinding
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * An Android activity representing the main shopping experience.
 *
 * This activity serves as the entry point for the shopping app. It sets up the navigation
 * using a BottomNavigationView and NavHostFragment to navigate between different fragments.
 *
 * @property binding: Lazily inflated view binding for this activity's layout.
 */
@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {
    // Lazily inflated view binding for this activity's layout.
    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    // ViewModel for managing the user's shopping cart.
    val viewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize the navigation controller and set up the BottomNavigationView.
        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)

        // Observe changes in the user's shopping cart and update the cart badge.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        val count = it.data?.size ?: 0
                        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

                        // Set a badge on the cart icon in the BottomNavigationView.
                        bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                            backgroundColor = resources.getColor(R.color.g_blue)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}

