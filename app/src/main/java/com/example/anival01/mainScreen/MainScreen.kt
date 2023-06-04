package com.example.anival01.mainScreen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.anival01.databinding.ActivityMainScreenBinding
import com.example.anival01.login.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class MainScreen : AppCompatActivity() {
    private lateinit var b: ActivityMainScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var firstName: String
    private lateinit var lastName: String
    private var points: Int = -16

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(b.root)
        Timber.plant(Timber.DebugTree())
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        b.btnSignOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // get first and last name
        val userRef = auth.currentUser?.let { db.collection("users").document(it.uid) }
        userRef?.get()?.addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                firstName = document.getString("firstName").toString()
                lastName = document.getString("lastName").toString()
                points = document.getLong("points")?.toInt() ?: -17

                // Use the first name and last name as needed
                val fullName = "Welcome, $firstName $lastName!"
                b.tvName.text = fullName
                val pointsHolder = "You have $points Points"
                b.tvHowMuchPoints.text = pointsHolder
            } else {
                toastHere("No Documetn found ya3am")
            }
        }?.addOnFailureListener { e ->
            // Error occurred while retrieving the document
            e.message?.let { toastHere(it) }
        }

        val displayName = intent.getStringExtra("name")
        b.tvName.text = displayName

    }

    private fun toastHere(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

}