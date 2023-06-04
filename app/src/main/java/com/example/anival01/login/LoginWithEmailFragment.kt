package com.example.anival01.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.anival01.R
import com.example.anival01.databinding.FragmentLoginWithEmailBinding
import com.example.anival01.mainScreen.MainScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginWithEmailFragment : Fragment() {

    private lateinit var b: FragmentLoginWithEmailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentLoginWithEmailBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.btnLogin.setOnClickListener {
            loginUser()
        }
        b.tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        b.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)

        }
        b.tvResendVerificationLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resendVerificationLinkFragment)

        }

        // Sign-in with Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        b.googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }
        // End of google sign-in

        // TODO: Sign-in with Facebook



        // testing btn
        b.btnSkipToNewsFeed.setOnClickListener {
            goToMainScreen()
        }
    }

    // Start of google sign-in  ##################################################    // gooooooogle
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else toastHere(task.exception.toString())
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(requireContext(), MainScreen::class.java)
                    .putExtra("name", account.displayName)
                startActivity(intent)
            } else toastHere(it.exception.toString())
        }
    }
    // End of google sign-in  ####################################################    // gooooooogle

    private fun loginUser() {
        val email = b.tiInsideEmail.text.toString()
        val password = b.tiInsidePassword.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // use await to wait for the function to fully finish before continuing with the code
                    auth.signInWithEmailAndPassword(email, password).await()
                    // Go to News Feed
                    if (auth.currentUser?.isEmailVerified == true) {
                        withContext(Dispatchers.Main) {
                            goToMainScreen()
                        }
                    } else {
                        auth.signOut()
                        withContext(Dispatchers.Main) {
                            toastHere("Please verify your email first to login")
                        }
                    }

                } catch (e: Exception) {
                    // Access UI components inside a coroutine using withContext()
                    withContext(Dispatchers.Main) {
                        toastHere(e.message.toString())
                    }
                }
            }
        } else toastHere("Email or pw empty")
    }

    private fun toastHere(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    private fun goToMainScreen() {
        val intent = Intent(requireContext(), MainScreen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        if (
            auth.currentUser != null &&
            auth.currentUser?.isEmailVerified == true
        ) goToMainScreen()
    }


}