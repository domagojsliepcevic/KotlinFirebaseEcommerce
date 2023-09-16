package hr.algebra.sverccommercefinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.algebra.sverccommercefinal.data.User
import hr.algebra.sverccommercefinal.util.Constants.USER_COLLECTION
import hr.algebra.sverccommercefinal.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class responsible for managing user profile information and logout functionality.
 *
 * @property firestore: The FirebaseFirestore instance for accessing Firestore database.
 * @property auth: The FirebaseAuth instance for user authentication.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    // MutableStateFlow to represent the user resource.
    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())

    // Exposed StateFlow for observing user information changes.
    val user = _user.asStateFlow()

    /**
     * Initialization block to fetch the user's profile information when the ViewModel is created.
     */
    init {
        getUser()
    }

    /**
     * Function to retrieve the user's profile information from Firestore.
     */
    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading()) // Emit a loading state.
        }

        // Add a snapshot listener to get real-time updates of the user document.
        firestore.collection(USER_COLLECTION).document(auth.uid!!)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // Handle errors by emitting an error state.
                    viewModelScope.launch {
                        _user.emit(Resource.Error(error.message.toString()))
                    }
                } else {
                    // Parse the user document and emit it as a Success state.
                    val user = value?.toObject(User::class.java)
                    user?.let {
                        viewModelScope.launch {
                            _user.emit(Resource.Success(user))
                        }
                    }
                }
            }
    }

    /**
     * Function to log out the current user.
     */
    fun logout() {
        auth.signOut() // Sign out the user.
    }
}
