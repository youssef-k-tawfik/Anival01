package com.example.anival01.login

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.anival01.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {

    private lateinit var b: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var spinnerGenders: Spinner
    private lateinit var gender: String
    private lateinit var spinnerProviders: Spinner
    private lateinit var provider: String
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentRegisterBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        spinnerProviders = b.spinnerProviders
        spinnerGenders = b.spinnerGender
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setGendersSpinner()
        setProvidersSpinner()

        b.btnRegister.setOnClickListener {
            registerUser()
        }

        b.tvGoBackToLogin.setOnClickListener {
            backToLoginFragment()
        }

    }

    private fun setProvidersSpinner() {
        // setting provider spinner
        val providers = arrayOf("@gmail.com", "@yahoo.com", "@hotmail.com", "@outlook.com")
        val adapterProviders =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_item, providers)
        adapterProviders.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProviders.adapter = adapterProviders

        //on provider selected
        spinnerProviders.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedOption = parent.getItemAtPosition(position).toString()
                // Perform actions based on the selected option
                provider = selectedOption
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where no option is selected
                provider = providers[0]
            }
        }
    }

    private fun setGendersSpinner() {
        // setting gender spinner
        val genders = arrayOf("Male", "Female")
        val adapterGenders =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_item, genders)
        adapterGenders.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenders.adapter = adapterGenders

        //on gender selected
        spinnerGenders.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedOption = parent.getItemAtPosition(position).toString()
                // Perform actions based on the selected option
                gender = selectedOption
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where no option is selected
                gender = genders[0]
            }
        }
    }

    private fun registerUser() {
        // Get inputs
        firstName = b.tiInsideFirstNameRegister.text.toString()
        lastName = b.tiInsideLastNameRegister.text.toString()
        email = b.tiInsideEmailRegister.text.toString() + provider
        password = b.tiInsidePasswordRegister.text.toString()

        // Verification steps
        if (firstName.length < 2) toastHere("First name cannot be less than 2 characters")
        else if (lastName.length < 2) toastHere("Last name cannot be less than 2 characters")
        else if (email.isEmpty()) toastHere("Email is empty!")
        else if (password.length < 4) toastHere("Password cannot be less than 4 characters")
        else firebaseSignUpProcess()
    }

    private fun firebaseSignUpProcess() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Register New User
                auth.createUserWithEmailAndPassword(email, password).await()
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
                addNewDocumentForNewUser()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun addNewDocumentForNewUser() {
        val newUser = User(firstName, lastName, 0, gender, email)
        auth.currentUser?.uid?.let { it ->
            db.collection("users").document(it).set(newUser)
        }
    }


    private fun toastHere(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    private fun backToLoginFragment() {
        findNavController().navigateUp()
    }
}