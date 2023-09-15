package hr.algebra.sverccommercefinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.algebra.sverccommercefinal.data.Address
import hr.algebra.sverccommercefinal.util.Constants.ADDRESS_SUBCOLLECTION
import hr.algebra.sverccommercefinal.util.Constants.USER_COLLECTION
import hr.algebra.sverccommercefinal.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class responsible for managing user addresses and address-related operations.
 *
 * @property firestore: The instance of FirebaseFirestore for accessing the Firestore database.
 * @property auth: The instance of FirebaseAuth for user authentication.
 */
@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    // Flow representing the result of adding a new address with resource status.
    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    val addNewAddress = _addNewAddress.asStateFlow()

    // Flow representing error messages during address validation.
    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    /**
     * Adds a new address to the user's address collection in Firestore.
     *
     * @param address: The Address object containing address information.
     */
    fun addAddress(address: Address) {
        val validateInputs = validateInputs(address)
        if (validateInputs) {
            viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }
            firestore.collection(USER_COLLECTION).document(auth.uid!!)
                .collection(ADDRESS_SUBCOLLECTION).document().set(address)
                .addOnSuccessListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Success(address)) }
                }
                .addOnFailureListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Error(it.message.toString())) }
                }
        } else {
            viewModelScope.launch { _error.emit("All fields are required") }
        }
    }

    /**
     * Validates the address fields to ensure they are not empty.
     *
     * @param address: The Address object to be validated.
     * @return `true` if all fields are not empty, `false` otherwise.
     */
    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty()
    }
}

