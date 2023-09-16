package hr.algebra.sverccommercefinal.fragments.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.data.User
import hr.algebra.sverccommercefinal.databinding.FragmentUserAccountBinding
import hr.algebra.sverccommercefinal.dialog.setupBottomSheetDialog
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.viewmodel.UserAccountViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Fragment responsible for displaying and updating user account information.
 *
 * @property binding: Lazily inflated view binding for this fragment's layout.
 * @property viewModel: ViewModel for managing user account information.
 * @property imageActivityResultLauncher: ActivityResultLauncher for handling image selection.
 * @property imageUri: The URI of the selected user profile image.
 */
@AndroidEntryPoint
class UserAccountFragment : Fragment() {
    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null

    /**
     * Called when the fragment is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the ActivityResultLauncher for image selection.
        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            imageUri = result.data?.data
            Glide.with(this).load(imageUri).into(binding.imageUser)
        }
    }

    /**
     * Called to create and return the view hierarchy associated with the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserAccountBinding.inflate(inflater)
        return binding.root
    }

    /**
     * Called after the fragment's view has been created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe and update user information.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showUserLoading()
                    }
                    is Resource.Success -> {
                        hideUserLoading()
                        showUserInformation(it.data!!)
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        // Handle the close button click to navigate back.
        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigateUp()
        }


        // Set up click listener for updating the password.
        binding.tvUpdatePassword.setOnClickListener {
            // Show the password reset dialog.
            setupBottomSheetDialog { email -> viewModel.resetPassword(email) }
        }

        // Observe and handle user information update.
        @Suppress("DEPRECATION")
        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonSave.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonSave.revertAnimation()
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
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

        // Set up click listener for saving user information.
        binding.buttonSave.setOnClickListener {
            binding.apply {
                val firstName = etFirstName.text.toString().trim()
                val lastName = etLastName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val user = User(firstName, lastName, email)
                viewModel.updateUser(user, imageUri)
            }
        }

        // Set up click listener for editing profile image.
        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }
    }

    /**
     * Show user information in the UI.
     *
     * @param data: User object containing user information.
     */
    private fun showUserInformation(data: User) {
        binding.apply {
            Glide.with(this@UserAccountFragment).load(data.imagePath)
                .error(ColorDrawable(Color.BLACK)).into(imageUser)
            etFirstName.setText(data.firstName)
            etLastName.setText(data.lastName)
            etEmail.setText(data.email)
        }
    }

    /**
     * Hide loading indicators and show user information.
     */
    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.INVISIBLE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            etFirstName.visibility = View.VISIBLE
            etLastName.visibility = View.VISIBLE
            etEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }

    /**
     * Show loading indicators while user information is being loaded.
     */
    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            etFirstName.visibility = View.INVISIBLE
            etLastName.visibility = View.INVISIBLE
            etEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }
}
