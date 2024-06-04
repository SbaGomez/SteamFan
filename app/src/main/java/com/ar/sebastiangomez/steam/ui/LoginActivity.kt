package com.ar.sebastiangomez.steam.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ar.sebastiangomez.steam.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var logoSplash: ImageView
    private lateinit var buttonLogin: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    companion object {
        private const val TAG = "LOG-LOGIN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Apply edge-to-edge window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (isSessionActive()) {
            navigateToHome()
            return
        }

        bindViewObjects()
        setupGoogleSignIn()
        setupFirebaseAuth()
        setupGoogleSignInLauncher()
        displayRandomImage()
    }

    private fun bindViewObjects() {
        logoSplash = findViewById(R.id.logoSplash)
        buttonLogin = findViewById(R.id.googleButton)
        buttonLogin.setOnClickListener { onLoginClick() }
    }

    private fun setupGoogleSignIn() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun setupFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun setupGoogleSignInLauncher() {
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    account?.let { firebaseAuthWithGoogleAccount(it) }
                } catch (e: ApiException) {
                    Log.e(TAG, "Google sign in failed", e)
                    showToast(getString(R.string.google_sign_in_failed, e.statusCode, e.message))
                }
            } else {
                Log.e(TAG, "Google sign in failed: result code ${result.resultCode}")
                showToast(getString(R.string.google_sign_in_failed_generic))
            }
        }
    }

    private fun displayRandomImage() {
        val images = arrayOf(R.drawable.gameslogo)
        val randomIndex = images.indices.random()
        logoSplash.setImageResource(images[randomIndex])
    }

    private fun onLoginClick() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }

    private fun saveSession() {
        val sharedPreferences = getSharedPreferences("session", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: ${account.email}")
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
                val email = account.email
                val message = if (isNewUser) {
                    getString(R.string.account_created_successfully)
                } else {
                    getString(R.string.welcome_back, email)
                }
                Log.d(TAG, message)
                showToast(message)
                saveSession()
                navigateToHome()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Firebase authentication failed", exception)
                showToast(getString(R.string.login_failed, exception.message))
            }
    }

    private fun isSessionActive(): Boolean {
        val sharedPreferences = getSharedPreferences("session", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}