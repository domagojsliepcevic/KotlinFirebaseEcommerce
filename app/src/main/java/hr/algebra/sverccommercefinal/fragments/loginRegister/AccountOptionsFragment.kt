package hr.algebra.sverccommercefinal.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.databinding.FragmentAccountOptionsBinding

class AccountOptionsFragment : Fragment(R.layout.fragment_account_options) {
    private lateinit var binding: FragmentAccountOptionsBinding // View binding for this fragment.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflates the layout for this fragment and initializes view binding.
        binding = FragmentAccountOptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up click listeners for the login and register buttons.
        binding.buttonLoginAccountOptions.setOnClickListener {
            // Navigate to the login fragment when the "Login" button is clicked.
            findNavController().navigate(R.id.action_accountOptionsFragment_to_loginFragment)
        }

        binding.buttonRegisterAccountOptions.setOnClickListener {
            // Navigate to the register fragment when the "Register" button is clicked.
            findNavController().navigate(R.id.action_accountOptionsFragment_to_registerFragment)
        }
    }
}
