package hr.algebra.sverccommercefinal.dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import hr.algebra.sverccommercefinal.R

/**
 * Sets up and displays a bottom sheet dialog for a specific functionality.
 *
 * @param onSendClick Callback function to be executed when the "Send" button is clicked.
 */
fun Fragment.setupBottomSheetDialog(onSendClick: (String) -> Unit) {
    // Create a new BottomSheetDialog with a custom style.
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)

    // Inflate the layout for the bottom sheet dialog.
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)

    // Set the dialog's content view to the inflated view.
    dialog.setContentView(view)

    // Set the initial state of the dialog to expanded.
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

    // Show the dialog.
    dialog.show()

    // Find and reference UI elements from the dialog layout.
    val etEmail = view.findViewById<EditText>(R.id.etResetPassword)
    val buttonSend = view.findViewById<Button>(R.id.buttonSendResetPassword)
    val buttonCancel = view.findViewById<Button>(R.id.buttonCancelResetPassword)

    // Set a click listener for the "Send" button.
    buttonSend.setOnClickListener {
        // Retrieve the email input text and trim any leading/trailing spaces.
        val email = etEmail.text.toString().trim()

        // Execute the provided callback function with the email input.
        onSendClick(email)

        // Dismiss the dialog.
        dialog.dismiss()
    }

    // Set a click listener for the "Cancel" button to dismiss the dialog.
    buttonCancel.setOnClickListener {
        dialog.dismiss()
    }
}
