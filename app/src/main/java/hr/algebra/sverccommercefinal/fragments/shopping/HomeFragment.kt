package hr.algebra.sverccommercefinal.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.adapters.HomeViewPagerAdapter
import hr.algebra.sverccommercefinal.databinding.FragmentHomeBinding
import hr.algebra.sverccommercefinal.fragments.categories.AccessoryFragment
import hr.algebra.sverccommercefinal.fragments.categories.ClothesFragment
import hr.algebra.sverccommercefinal.fragments.categories.ElectronicsFragment
import hr.algebra.sverccommercefinal.fragments.categories.FurnitureFragment
import hr.algebra.sverccommercefinal.fragments.categories.MainCategoryFragment
import hr.algebra.sverccommercefinal.fragments.categories.OutdoorsFragment
import hr.algebra.sverccommercefinal.fragments.categories.ToolsFragment


/**
 * Fragment for the Home screen, displaying a ViewPager with various category fragments.
 */
class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // List of category fragments to display in the ViewPager.
        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ClothesFragment(),
            ElectronicsFragment(),
            AccessoryFragment(),
            FurnitureFragment(),
            OutdoorsFragment(),
            ToolsFragment()
        )
        //Disables user input for the ViewPager located in the "binding.viewpagerHome" view.
        binding.viewpagerHome.isUserInputEnabled = false

        // Create an adapter for the ViewPager.
        val viewPagerToAdapter = HomeViewPagerAdapter(categoriesFragments, childFragmentManager, lifecycle)

        // Set the adapter for the ViewPager.
        binding.viewpagerHome.adapter = viewPagerToAdapter

        // Attach a TabLayoutMediator to synchronize tabs with the ViewPager.
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, position ->
            // Set tab text based on the fragment position.
            when (position) {
                0 -> tab.text = "Home"
                1 -> tab.text = "Clothes"
                2 -> tab.text = "Electronics"
                3 -> tab.text = "Accessory"
                4 -> tab.text = "Furniture"
                5 -> tab.text = "Outdoors"
                6 -> tab.text = "Tools"

            }
        }.attach()
    }
}
