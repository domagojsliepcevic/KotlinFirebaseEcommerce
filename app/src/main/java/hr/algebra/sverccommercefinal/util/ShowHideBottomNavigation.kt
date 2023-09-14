package hr.algebra.sverccommercefinal.util


import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.activities.ShoppingActivity

/**
 * Function to hide the bottom navigation view in the current fragment.
 *
 * This function finds the bottom navigation view in the parent activity and sets its visibility to GONE.
 * It is typically used when you want to hide the bottom navigation view temporarily, such as when
 * displaying a full-screen fragment.
 *
 * Note: This function assumes that the parent activity is of type [ShoppingActivity] and contains
 * the bottom navigation view with the specified ID.
 */
fun Fragment.hideBottomNavigationView() {
    // Find the bottom navigation view in the parent activity.
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation
    )

    // Set the visibility of the bottom navigation view to GONE.
    bottomNavigationView.visibility = View.GONE
}

/**
 * Function to show the bottom navigation view in the current fragment.
 *
 * This function finds the bottom navigation view in the parent activity and sets its visibility to VISIBLE.
 * It is used to show the bottom navigation view again after it has been hidden.
 *
 * Note: This function assumes that the parent activity is of type [ShoppingActivity] and contains
 * the bottom navigation view with the specified ID.
 */
fun Fragment.showBottomNavigationView() {
    // Find the bottom navigation view in the parent activity.
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation
    )

    // Set the visibility of the bottom navigation view to VISIBLE.
    bottomNavigationView.visibility = View.VISIBLE
}
