package hr.algebra.sverccommercefinal.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.activities.ShoppingActivity
import hr.algebra.sverccommercefinal.databinding.FragmentLoginBinding
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint // Indicates that this fragment is Hilt-enabled for dependency injection.
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding // View binding for this fragment.
    private val viewModel by viewModels<LoginViewModel>() // Initialize the LoginViewModel using Hilt's viewModels delegate.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflates the layout for this fragment and initializes view binding.
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up a click listener to navigate to the registration fragment.
        binding.tvDontHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.apply {
            // Register a click listener for the login button.
            buttonLoginLogin.setOnClickListener {
                val email = etEmailLogin.text.toString().trim()
                val password = etPasswordLogin.text.toString()
                // Call the ViewModel's login function with the provided email and password.
                viewModel.login(email, password)
            }
        }

        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            // Collect and react to changes in the ViewModel's 'login' flow.
            viewModel.login.collect { it ->
                when (it) {
                    is Resource.Loading -> {
                        // Start an animation (presumably a loading animation) on the login button.
                        binding.buttonLoginLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        // Revert any animation on the login button.
                        binding.buttonLoginLogin.revertAnimation()
                        // Create an intent to start the ShoppingActivity and clear the back stack.
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        // Show a toast with the error message and revert any animation on the login button.
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.buttonLoginLogin.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }
    }
}
