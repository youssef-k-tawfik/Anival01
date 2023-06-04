package com.example.anival01.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.anival01.R
import com.example.anival01.databinding.FragmentLoginBinding
import com.example.anival01.mainScreen.MainScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private lateinit var b: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentLoginBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
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

        // testing btn
        b.btnSkipToNewsFeed.setOnClickListener {
            goToMainScreen()
        }

    }


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