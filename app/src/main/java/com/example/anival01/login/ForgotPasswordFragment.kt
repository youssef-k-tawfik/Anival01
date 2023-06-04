package com.example.anival01.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.anival01.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragment : Fragment() {

    private lateinit var b: FragmentForgotPasswordBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentForgotPasswordBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.btnSendReset.setOnClickListener {
            email = b.tiInsideEmailResetPassword.text.toString()
            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            toastHere("Reset email was successfully sent!")
                            backToLoginFragment()
                        } else task.exception?.message?.let { it1 -> toastHere(it1) }
                    }
            } else toastHere("Enter email please")
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