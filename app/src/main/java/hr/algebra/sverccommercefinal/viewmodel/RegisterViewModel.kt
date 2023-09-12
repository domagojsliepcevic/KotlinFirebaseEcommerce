package hr.algebra.sverccommercefinal.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.algebra.sverccommercefinal.data.User
import hr.algebra.sverccommercefinal.util.Constants.USER_COLLECTION
import hr.algebra.sverccommercefinal.util.RegisterFieldsState
import hr.algebra.sverccommercefinal.util.RegisterValidation
import hr.algebra.sverccommercefinal.util.Resource
import hr.algebra.sverccommercefinal.util.validateEmail
import hr.algebra.sverccommercefinal.util.validatePassword
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * ViewModel class responsible for handling user registration with Firebase.
 *
 * @property firebaseAuth: Injected FirebaseAuth instance for user authentication.
 * @property db: Injected FirebaseFirestore instance for data storage.
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    // A private mutable StateFlow representing the registration process, initially set to 'Loading' state.
    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())

    // A public immutable Flow property exposing the registration process state.
    val register: Flow<Resource<User>> = _register

    // A channel for sending and receiving validation state updates.
    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

    /**
     * Function to create a new user account with email and password.
     *
     * @param user: User object containing user details for registration.
     * @param password: Password provided by the user.
     */
    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading()) // Emit a loading state when registration begins.
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.let { firebaseUser ->
                        saveUserInfo(firebaseUser.uid, user) // Save user info to Firestore.
                    }
                }
                .addOnFailureListener { exception ->
                    // Set the '_register' state to 'Error' with the error message.
                    _register.value = Resource.Error(exception.message.toString())
                }
        } else {
            // Validate email and password and send the validation state to the channel.
            val registerFieldState = RegisterFieldsState(
                validateEmail(user.email), validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldState)
            }
        }
    }

    /**
     * Function to save user information to Firestore.
     *
     * @param userUid: User's UID obtained from Firebase authentication.
     * @param user: User object containing user details.
     */
    private fun saveUserInfo(userUid: String, user: User) {
        db.collection(USER_COLLECTION) // USER_COLLECTION is defined in util -> Constants.
            .document(userUid) // Use the user's UID as the document ID.
            .set(user) // Set user data in the document.
            .addOnSuccessListener {
                // Set the '_register' state to 'Success' with the User object.
                _register.value = Resource.Success(user)
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    /**
     * Function to check if email and password are valid for registration.
     *
     * @param user: User object containing user details.
     * @param password: Password provided by the user.
     * @return Boolean indicating whether the registration is allowed based on validation results.
     */
    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        // Determine if registration is allowed based on email and password validation results.
        val shouldRegister =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success
        return shouldRegister
    }
}



