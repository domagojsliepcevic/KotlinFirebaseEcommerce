package hr.algebra.sverccommercefinal.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import hr.algebra.sverccommercefinal.R

@AndroidEntryPoint
class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
    }

    /**
     * Override the onBackPressed method to close the app when the back button is pressed.
     */
    override fun onBackPressed() {
        // This will close the app when the back button is pressed from LoginRegisterActivity
        finishAffinity()
    }
}
