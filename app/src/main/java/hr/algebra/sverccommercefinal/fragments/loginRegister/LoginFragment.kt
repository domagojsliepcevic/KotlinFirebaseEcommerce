package hr.algebra.sverccommercefinal.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.R
import hr.algebra.sverccommercefinal.activities.ShoppingActivity
import hr.algebra.sverccommercefinal.databinding.FragmentLoginBinding
import hr.algebra.sverccommercefinal.dialog.setupBottomSheetDialog
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.viewmodel.LoginViewModel

/**
 * Fragment responsible for user login.
 *
 * @property binding: Lazily inflated view binding for this fragment's layout.
 * @property viewModel: ViewModel for managing the user login process.
 */
@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up click listeners and actions for UI elements.
        binding.apply {
            tvDontHaveAccount.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            buttonLoginLogin.setOnClickListener {
                val email = etEmailLogin.text.toString().trim()
                val password = etPasswordLogin.text.toString()

                if (validateInput(email, password)) {
                    // Input validation passed; call the ViewModel's login function.
                    viewModel.login(email, password)
                }
            }

            tvForgotPasswordLogin.setOnClickListener {
                setupBottomSheetDialog { email -> viewModel.resetPassword(email) }
            }
        }

        // Observe and handle reset password result.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect { it ->
                when (it) {
                    is Resource.Success -> {
                        Snackbar.make(requireView(), "Reset link was sent to your email", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        // Observe and handle login result.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.login.collect { it ->
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonLoginLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonLoginLogin.revertAnimation()
                        val intent = Intent(requireActivity(), ShoppingActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.buttonLoginLogin.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Both email and password are required.", Toast.LENGTH_LONG).show()
            return false
        } else if (!isValidEmail(email)) {
            Toast.makeText(requireContext(), "Invalid email format.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}



