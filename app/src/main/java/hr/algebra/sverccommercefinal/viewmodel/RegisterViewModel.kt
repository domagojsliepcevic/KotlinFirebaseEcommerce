package hr.algebra.sverccommercefinal.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.algebra.sverccommercefinal.data.User
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

@HiltViewModel // Indicates that this is a Hilt-enabled ViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth // Injected FirebaseAuth instance.
) : ViewModel() {

    // A private mutable state flow for representing the registration process, initially set to 'Loading' state.
    private val _register = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())

    // A public immutable flow property that exposes the registration process state.
    val register: Flow<Resource<FirebaseUser>> = _register

    // A channel for sending and receiving validation state updates.
    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

    // Function to create a new user account with email and password.
    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading()) // Emit a loading state when registration begins.
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { authResult ->
                    authResult.user?.let { firebaseUser ->
                        // Set the '_register' state to 'Success' with the FirebaseUser object.
                        _register.value = Resource.Success(firebaseUser)
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

    // Function to check if email and password are valid for registration.
    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        // Determine if registration is allowed based on email and password validation results.
        val shouldRegister =
            emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success
        return shouldRegister
    }
}

