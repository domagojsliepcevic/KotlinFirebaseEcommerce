package hr.algebra.sverccommercefinal.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.databinding.ActivityShoppingBinding

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
    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize the navigation controller and set up the BottomNavigationView.
        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)
    }
}
