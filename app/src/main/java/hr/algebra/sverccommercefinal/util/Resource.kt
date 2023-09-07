package hr.algebra.sverccommercefinal.util



// This is a sealed class called 'Resource' that is used for representing various states of a data operation.
sealed class Resource<T>(
    val data: T? = null,        // A nullable property to hold the data (result) of the operation.
    val message: String? = null // A nullable property to hold an optional error message.
) {
    // This is a subclass 'Success' of 'Resource' used to represent a successful operation.
    class Success<T>(data: T) : Resource<T>(data)

    // This is a subclass 'Error' of 'Resource' used to represent an operation that encountered an error.
    class Error<T>(message: String) : Resource<T>(message = message)

    // This is a subclass 'Loading' of 'Resource' used to represent that an operation is in progress.
    class Loading<T> : Resource<T>()

    // This is a subclass 'Unspecified' of 'Resource' used to represent an unspecified state.
    class Unspecified<T> : Resource<T>()
}

