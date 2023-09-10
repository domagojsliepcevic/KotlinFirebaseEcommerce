package hr.algebra.sverccommercefinal.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Adapter class for managing the fragments within a ViewPager in the Home screen.
 *
 * @param fragments A list of fragments to be displayed in the ViewPager.
 * @param fm The FragmentManager to interact with fragments.
 * @param lifeCycle The lifecycle of the parent container, typically the activity's lifecycle.
 */
class HomeViewPagerAdapter(
    private val fragments: List<Fragment>,
    fm: FragmentManager,
    lifeCycle: Lifecycle
) : FragmentStateAdapter(fm, lifeCycle) {

    /**
     * Returns the total number of fragments in the adapter.
     */
    override fun getItemCount(): Int {
        return fragments.size
    }

    /**
     * Creates and returns a fragment at the specified position.
     *
     * @param position The position of the fragment to create.
     * @return The Fragment instance to be displayed in the ViewPager.
     */
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
