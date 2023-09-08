package hr.algebra.sverccommercefinal.util

// A sealed class called 'RegisterValidation' is defined to represent validation results.
sealed class RegisterValidation() {
    // An object 'Success' represents a successful validation.
    object Success : RegisterValidation()
    // A data class 'Failed' represents a failed validation with an associated error message.
    data class Failed(val message: String) : RegisterValidation()
}

// A data class 'RegisterFieldsState' holds the validation state for email and password.
data class RegisterFieldsState(
    val email: RegisterValidation,    // Represents the validation state for email.
    val password: RegisterValidation  // Represents the validation state for password.
)

