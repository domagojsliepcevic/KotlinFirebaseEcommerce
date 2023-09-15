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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class responsible for managing user addresses during the billing process.
 *
 * @property firestore: The instance of FirebaseFirestore for accessing the Firestore database.
 * @property auth: The instance of FirebaseAuth for user authentication.
 */
@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    // Flow representing user addresses with resource status.
    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    /**
     * Initializes the ViewModel by fetching user addresses.
     */
    init {
        getUserAddresses()
    }

    /**
     * Fetches user addresses from Firestore and updates the [_address] flow.
     */
    fun getUserAddresses() {
        viewModelScope.launch { _address.emit(Resource.Loading()) }
        firestore.collection(USER_COLLECTION).document(auth.uid!!)
            .collection(ADDRESS_SUBCOLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    viewModelScope.launch { _address.emit(Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                }
                val addresses = value?.toObjects(Address::class.java)
                viewModelScope.launch { _address.emit(Resource.Success(addresses!!)) }
            }
    }
}
