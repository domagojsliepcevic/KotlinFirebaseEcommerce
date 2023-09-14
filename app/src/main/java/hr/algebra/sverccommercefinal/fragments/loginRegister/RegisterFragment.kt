package hr.algebra.sverccommercefinal.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.activities.ShoppingActivity
import hr.algebra.sverccommercefinal.data.User
import hr.algebra.sverccommercefinal.databinding.FragmentRegisterBinding
import hr.algebra.sverccommercefinal.util.RegisterValidation
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.viewmodel.RegisterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val TAG = "RegisterFragment" // TAG for logging purposes.

@AndroidEntryPoint // Indicates that this fragment is Hilt-enabled for dependency injection.
class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding // View binding for this fragment.
    private val viewModel by viewModels<RegisterViewModel>() // Initialize the RegisterViewModel using Hilt's viewModels delegate.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflates the layout for this fragment and initializes the binding.
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDoYouHaveAccount.setOnClickListener {
            // Navigate to the login fragment when the "Do you have an account?" text is clicked.
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            // Register a click listener for the registration button.
            buttonRegisterRegister.setOnClickListener {
                // Create a User object with data from user input fields.
                val user = User(
                    etFirstNameRegister.text.toString().trim(),
                    etLastNameRegister.text.toString().trim(),
                    etEmailRegister.text.toString().trim()
                )
                val password = etPasswordRegister.text.toString()
                // Call the ViewModel's function to create a user account with the provided data.
                viewModel.createAccountWithEmailAndPassword(user, password)



            }
        }

        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            // Collect and react to changes in the ViewModel's 'register' flow.
            viewModel.register.collect { it ->
                when (it) {
                    is Resource.Loading -> {
                        // Start an animation (presumably a loading animation) on the registration button.
                        binding.buttonRegisterRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        // Log a successful registration and revert any animation on the button.
                        Log.d("test", it.data.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                        // Create an intent to start the ShoppingActivity and clear the back stack.
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        // Log an error message and revert any animation on the button.
                        Log.e(TAG, it.message.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            // Collect and react to changes in the ViewModel's 'validation' flow.
            viewModel.validation.collect { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        // If email validation fails, set an error message on the email input field.
                        binding.etEmailRegister.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }
                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        // If password validation fails, set an error message on the password input field.
                        binding.etPasswordRegister.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }
}

