package hr.algebra.sverccommercefinal.util

import android.util.Patterns


// Function to validate an email address.
fun validateEmail(email: String): RegisterValidation {
    if (email.isEmpty()) // Check if the email is empty.
        return RegisterValidation.Failed("Email can not be empty") // Return a validation failure with an error message.

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) // Check if the email matches the email pattern.
        return RegisterValidation.Failed("Invalid email format") // Return a validation failure with an error message.

    return RegisterValidation.Success // If all checks pass, return a validation success.
}

// Function to validate a password.
fun validatePassword(password: String): RegisterValidation {
    if (password.isEmpty()) // Check if the password is empty.
        return RegisterValidation.Failed("Password can not be empty") // Return a validation failure with an error message.

    if (password.length < 6) // Check if the password has less than 6 characters.
        return RegisterValidation.Failed("Password should contain 6 characters") // Return a validation failure with an error message.

    return RegisterValidation.Success // If all checks pass, return a validation success.
}