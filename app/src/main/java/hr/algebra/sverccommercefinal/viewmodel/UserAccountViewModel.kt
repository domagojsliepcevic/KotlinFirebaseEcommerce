package hr.algebra.sverccommercefinal.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.algebra.sverccommercefinal.SvercApplicationFinal
import hr.algebra.sverccommercefinal.data.User
import hr.algebra.sverccommercefinal.util.Constants.USER_COLLECTION
import hr.algebra.sverccommercefinal.util.RegisterValidation
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.util.validateEmail
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for managing user account information and related operations.
 *
 * @property firestore: The FirebaseFirestore instance for accessing Firestore database.
 * @property auth: The FirebaseAuth instance for user authentication.
 * @property storage: The Firebase StorageReference for cloud storage operations.
 * @property app: The Application instance for the AndroidViewModel.
 */
@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application
) : AndroidViewModel(app) {

    // MutableStateFlow for representing the user's information.
    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    // MutableStateFlow for representing the user's information update process.
    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    // A private mutable shared flow for representing the password reset process.
    private val _resetPassword = MutableSharedFlow<Resource<String>>()

    // A public shared flow property that exposes the password reset process state.
    val resetPassword = _resetPassword.asSharedFlow()

    /**
     * Initialize the ViewModel by fetching the user's information.
     */
    init {
        getUser()
    }

    /**
     * Fetch the user's information from Firestore.
     */
    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        firestore.collection(USER_COLLECTION).document(auth.uid!!).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch { _user.emit(Resource.Success(it)) }
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch { _user.emit(Resource.Error(exception.message.toString())) }
            }
    }

    /**
     * Update the user's information, including their profile image.
     *
     * @param user: The updated user object.
     * @param imageUri: The URI of the new profile image, if provided.
     */
    fun updateUser(user: User, imageUri: Uri?) {
        // Validate user inputs
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()

        if (!areInputsValid) {
            viewModelScope.launch {
                _user.emit(Resource.Error("Invalid email or user data"))
            }
            return
        }

        viewModelScope.launch {
            _updateInfo.emit(Resource.Loading())
        }

        if (imageUri == null) {
            // Update user information without changing the profile image.
            saveUserInformation(user, true)
        } else {
            // Update user information and profile image.
            saveUserInformationWithNewImage(user, imageUri)
        }
    }

    /**
     * Update user information with a new profile image.
     *
     * @param user: The updated user object.
     * @param imageUri: The URI of the new profile image.
     */
    private fun saveUserInformationWithNewImage(user: User, imageUri: Uri) {
        viewModelScope.launch {
            try {
                // Load the image from the provided URI.
                val imageBitmap =
                    MediaStore.Images.Media.getBitmap(getApplication<SvercApplicationFinal>().contentResolver, imageUri)
                val byteArrayOutputStream = ByteArrayOutputStream()
                // Compress the image and convert it to bytes.
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()
                val imageDirectory = storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()
                // Save user information with the new image URL.
                saveUserInformation(user.copy(imagePath = imageUrl), false)
            } catch (e: Exception) {
                viewModelScope.launch { _updateInfo.emit(Resource.Error(e.message.toString())) }
            }
        }
    }

    /**
     * Save user information to Firestore.
     *
     * @param user: The updated user object.
     * @param shouldRetrieveOldImage: Whether to retrieve the old profile image.
     */
    private fun saveUserInformation(user: User, shouldRetrieveOldImage: Boolean) {
        firestore.runTransaction { transaction ->
            val documentReference = firestore.collection(USER_COLLECTION).document(auth.uid!!)
            if (shouldRetrieveOldImage) {
                val currentUser = transaction.get(documentReference).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                transaction.set(documentReference, newUser)
            } else {
                transaction.set(documentReference, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch { _updateInfo.emit(Resource.Success(user)) }
        }.addOnFailureListener { exception ->
            viewModelScope.launch { _updateInfo.emit(Resource.Error(exception.message.toString())) }
        }
    }

    /**
     * Function to send a password reset email to the user.
     *
     * @param email: User's email address for password reset.
     */
    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading()) // Emit a loading state when reset begins.
        }

        auth
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    // Set the '_resetPassword' state to 'Success' with the email address.
                    _resetPassword.emit(Resource.Success(email))
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    // Set the '_resetPassword' state to 'Error' with the error message.
                    _resetPassword.emit(Resource.Error(exception.message.toString()))
                }
            }
    }
}
