package com.example.anival01.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.anival01.databinding.FragmentResendVerificationLinkBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ResendVerificationLinkFragment : Fragment() {

    private lateinit var b: FragmentResendVerificationLinkBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var pw: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentResendVerificationLinkBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        b.btnResendVerificationLink.setOnClickListener {
            email = b.tiInsideEmailResendVerificationLink.text.toString()
            pw = b.tiInsidePasswordResendVerificationLink.text.toString()

            if (email.isNotEmpty() && pw.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // use await to wait for the function to fully finish before continuing with the code
                        auth.signInWithEmailAndPassword(email, pw).await()
                        // Go to News Feed
                        if (auth.currentUser?.isEmailVerified == true) {
                            withContext(Dispatchers.Main) {
                                toastHere("Email already verified")
                            }
                        } else {
                            auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                                val alertDialog = AlertDialog.Builder(requireContext())
                                    .setTitle("Verification Email Sent")
                                    .setMessage("A verification email has been sent to your email address. Please check your inbox and follow the instructions to verify your account.")
                                    .setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()
                                        // Perform any necessary actions after the user acknowledges the dialog
                                        // Back to Login
                                        backToLoginFragment()
                                    }
                                    .create()
                                alertDialog.show()
                            }
                            auth.signOut()
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


        b.imgGoBackToLogin.setOnClickListener {
            backToLoginFragment()
        }
    }


    private fun backToLoginFragment() {
        findNavController().navigateUp()
    }

    private fun toastHere(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }
}