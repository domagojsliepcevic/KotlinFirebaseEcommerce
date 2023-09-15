package hr.algebra.sverccommercefinal.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.adapters.AllOrdersAdapter
import hr.algebra.sverccommercefinal.databinding.FragmentAllOrdersBinding
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.viewmodel.AllOrdersViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Fragment responsible for displaying a list of user orders.
 */
@AndroidEntryPoint
class AllOrdersFragment : Fragment() {
    private lateinit var binding: FragmentAllOrdersBinding
    val viewModel by viewModels<AllOrdersViewModel>()
    val allOrdersAdapter by lazy { AllOrdersAdapter() }

    /**
     * Called to create and return the view hierarchy associated with the fragment.
     *
     * @param inflater: The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container: The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState: If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root view of the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllOrdersBinding.inflate(inflater)
        return binding.root
    }

    /**
     * Called after the fragment's view has been created.
     *
     * @param view: The root view of the fragment.
     * @param savedInstanceState: If non-null, this fragment is being re-constructed from a previous saved state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the RecyclerView for displaying orders.
        setupOrdersRv()

        // Observe and update the list of user orders in the RecyclerView.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.allOrders.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAllOrders.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        allOrdersAdapter.differ.submitList(it.data)
                        if (it.data.isNullOrEmpty()) {
                            binding.tvEmptyOrders.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Error -> {
                        binding.progressbarAllOrders.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        allOrdersAdapter.onClick = {
            val action = AllOrdersFragmentDirections.actionAllOrdersFragmentToOrderDetailFragment(it)
            findNavController().navigate(action)
        }
    }

    /**
     * Set up the RecyclerView for displaying user orders.
     */
    private fun setupOrdersRv() {
        binding.rvAllOrders.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = allOrdersAdapter
        }
    }
}
