package hr.algebra.sverccommercefinal.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.data.Address
import hr.algebra.sverccommercefinal.databinding.FragmentAddressBinding
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.viewmodel.AddressViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Fragment class responsible for adding a new address.
 */
@AndroidEntryPoint
class AddressFragment : Fragment() {
    private lateinit var binding: FragmentAddressBinding
    private val viewModel by viewModels<AddressViewModel>()
    val args by navArgs<AddressFragmentArgs>()

    /**
     * Called when the fragment is created. Sets up observers for address addition and error messages.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Observe the address addition process and display loading or success/error messages.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        // Observe and display error messages.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Called when the view for the fragment is created. Inflates the layout and sets up
     * the click listener for the "Save" button.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    /**
     * Called when the view is created. Sets up the click listener for the "Save" button to add a new address.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val address = args.address
        if (address == null) {
            binding.buttonDelete.visibility = View.GONE
        } else {
            binding.apply {
                etAddressTitle.setText(address.addressTitle)
                etFullName.setText(address.fullName)
                etStreet.setText(address.street)
                etPhone.setText(address.phone)
                etCity.setText(address.city)
                etState.setText(address.state)
            }
        }

        binding.apply {
            buttonSave.setOnClickListener {
                val addressTitle = etAddressTitle.text.toString()
                val fullName = etFullName.text.toString()
                val street = etStreet.text.toString()
                val phone = etPhone.text.toString()
                val city = etCity.text.toString()
                val state = etState.text.toString()

                val address = Address(addressTitle, fullName, street, phone, city, state)

                viewModel.addAddress(address)
            }
        }
    }
}

