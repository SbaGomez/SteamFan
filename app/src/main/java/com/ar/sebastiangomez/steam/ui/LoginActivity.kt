package com.ar.sebastiangomez.steam.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ar.sebastiangomez.steam.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var logoSplash: ImageView
    private lateinit var buttonLogin: Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var oneTapClient: SignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<IntentSenderRequest>

    companion object {
        private const val TAG = "LOG-LOGIN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        if (isSessionActive()) {
            navigateToHome()
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    private fun setupFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun setupGoogleSignInLauncher() {
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        firebaseAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    val user = firebaseAuth.currentUser
                                    saveSession()
                                    onLoginSuccess(user?.displayName ?: "")
                                } else {
                                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                                    val errorMessage = getString(R.string.login_failed, task.exception?.message)
                                    showToast(errorMessage)
                                }
                            }
                    }
                } catch (e: ApiException) {
                    Log.w(TAG, "Google sign in failed", e)
                    val errorMessage = getString(R.string.google_sign_in_failed, e.statusCode, e.message)
                    showToast(errorMessage)
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
        oneTapClient = Identity.getSignInClient(this)
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent).build()
                    googleSignInLauncher.launch(intentSenderRequest)
                } catch (e: Exception) {
                    Log.e(TAG, "Error launching Google sign in intent: ${e.message}", e)
                    showToast(getString(R.string.google_sign_in_failed_generic))
                }
            }
            .addOnFailureListener(this) { e ->
                Log.e(TAG, "Google sign in failed: ${e.message}", e)
                showToast(getString(R.string.google_sign_in_failed_generic))
            }
    }

    private fun saveSession() {
        val sharedPreferences = getSharedPreferences("session", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    private fun isSessionActive(): Boolean {
        val sharedPreferences = getSharedPreferences("session", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun onLoginSuccess(userName: String) {
        val welcomeMessage = getString(R.string.welcome_back, userName)
        showToast(welcomeMessage)
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
