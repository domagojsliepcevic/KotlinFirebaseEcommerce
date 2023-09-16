package hr.algebra.sverccommercefinal.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.activities.LoginRegisterActivity
import hr.algebra.sverccommercefinal.databinding.FragmentProfileBinding
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.util.showBottomNavigationView
import hr.algebra.sverccommercefinal.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Fragment responsible for displaying user profile information and actions.
 *
 * @property binding: Lazily inflated view binding for this fragment's layout.
 * @property viewModel: ViewModel for managing user profile information.
 */
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    val viewModel by viewModels<ProfileViewModel>()

    /**
     * Called when the fragment's view is created.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    /**
     * Called after the fragment's view has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigate to the UserAccountFragment when the profile view is clicked.
        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }

        // Navigate to the AllOrdersFragment when the "All Orders" option is clicked.
        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_allOrdersFragment)
        }

        // Navigate to the BillingFragment with default values when the "Billing" option is clicked.
        binding.linearBilling.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(
                0f,
                emptyArray()
            )
            findNavController().navigate(action)
        }

        // Log out the user and navigate to the LoginRegisterActivity when the "Log Out" option is clicked.
        binding.linearLogOut.setOnClickListener {
            viewModel.logout()
            val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        // Update the user profile information on the view.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Glide.with(requireView()).load(it.data!!.imagePath)
                            .error(ColorDrawable(Color.BLACK)).into(binding.imageUser)
                        binding.tvUserName.text = "${it.data.firstName} ${it.data.lastName}"
                    }
                    is Resource.Error -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    /**
     * Called when the fragment is resumed.
     */
    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}
