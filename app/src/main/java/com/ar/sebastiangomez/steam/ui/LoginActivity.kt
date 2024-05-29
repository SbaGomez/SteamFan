package com.ar.sebastiangomez.steam.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
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

    private val tag = "LOG-LOGIN"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bindViewObject()
    }

    private fun bindViewObject()
    {
        logoSplash = findViewById(R.id.logoSplash)
        buttonLogin = findViewById(R.id.googleButton)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        firebaseAuth = FirebaseAuth.getInstance()


        // Array con los IDs de las imágenes en drawable
        val imagenes = arrayOf(
            R.drawable.gameslogo
        )

        // Generar un índice aleatorio
        val randomIndex = imagenes.indices.random()

        // Asignar la imagen aleatoria al logoSplash
        logoSplash.setImageResource(imagenes[randomIndex])
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLoginClick(view: View)
    {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)
            }
            catch (e: Exception) {
                Log.e("DEMO-API", "onActivityResult: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val firebaseUser = firebaseAuth.currentUser
                val uid = firebaseUser!!.uid
                val email = firebaseUser.email

                if (authResult.additionalUserInfo!!.isNewUser) {
                    // Crear Account
                    Toast.makeText(this@LoginActivity, "Cuenta creada...", Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(this@LoginActivity, "Cuenta existente...", Toast.LENGTH_LONG).show()
                }

                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this@LoginActivity, "Login fallido...", Toast.LENGTH_LONG).show()
            }
    }
}