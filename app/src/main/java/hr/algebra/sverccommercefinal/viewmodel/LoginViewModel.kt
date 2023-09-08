package hr.algebra.sverccommercefinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.algebra.sverccommercefinal.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel // Indicates that this is a Hilt-enabled ViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth // Injected FirebaseAuth instance.
) : ViewModel() {

    // A private mutable shared flow for representing the login process.
    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()

    // A public shared flow property that exposes the login process state.
    val login = _login.asSharedFlow()

    // Function to perform user login with an email and password.
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _login.emit(Resource.Loading()) // Emit a loading state when login begins.
        }
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                viewModelScope.launch {
                    authResult.user?.let {
                        // Set the '_login' state to 'Success' with the FirebaseUser object.
                        _login.emit(Resource.Success(it))
                    }
                }
            }
            .addOnFailureListener { exception ->
                viewModelScope.launch {
                    // Set the '_login' state to 'Error' with the error message.
                    _login.emit(Resource.Error(exception.message.toString()))
                }
            }
    }
}
