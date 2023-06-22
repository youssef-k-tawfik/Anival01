package com.example.anival01.mainScreen

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.LinearInterpolator
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.anival01.databinding.ActivityMainScreenBinding
import com.example.anival01.login.MainActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber


@Suppress("DEPRECATION")
class MainScreen : AppCompatActivity() {
    private lateinit var b: ActivityMainScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    //    private lateinit var firstName: String
//    private lateinit var lastName: String
    private var points: Int = -16

    // news
    private val newsImages = arrayOf(
        com.example.anival01.R.drawable.news0,
        com.example.anival01.R.drawable.news1,
        com.example.anival01.R.drawable.news2
    )
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var currentImageIndex = 0
    private val delayInMillis: Long = 5000 // 5 seconds
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: NewsImageAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var radioButton0: RadioButton
    private lateinit var radioButton1: RadioButton
    private lateinit var radioButton2: RadioButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(b.root)
        Timber.plant(Timber.DebugTree())
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        b.btnLeftArrow.background = null
        b.btnRightArrow.background = null

        // Perform logging out
        b.btnSignOut.setOnClickListener {
            //fb logout
            LoginManager.getInstance().logOut()

            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            // stop back button from returning to main screen
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // getting name and points
        coroutineScope.launch {
            // get first and last name
            val userRef = auth.currentUser?.let { user ->
                db.collection("users").document(user.uid)
            }
            userRef?.get()?.addOnSuccessListener { document ->
                if (document != null && document.exists()) {
//                    firstName = document.getString("firstName").toString()
//                    lastName = document.getString("lastName").toString()
                    points = document.getLong("points")?.toInt() ?: -17

                    // Use the first name and last name as needed
//                    val fullName = "Welcome, $firstName $lastName!"
//                    b.tvName.text = fullName
                    val pointsHolder = points.toString()
                    b.tvHowMuchPoints.text = pointsHolder
                } else {
                    toastHere("No Document found ya3am")
                }
            }?.addOnFailureListener { e ->
                // Error occurred while retrieving the document
                e.message?.let { toastHere(it) }
            }
        }

        // news
        radioButton0 = b.radioButton0
        radioButton1 = b.radioButton1
        radioButton2 = b.radioButton2

        recyclerView = b.rvNewsHolder

        // Set up the RecyclerView and its adapter
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        imageAdapter = NewsImageAdapter(newsImages)
        recyclerView.adapter = imageAdapter

        handler = Handler()
        runnable = Runnable {
            changeImage()
            handler.postDelayed(runnable, delayInMillis)
        }
        handler.postDelayed(runnable, delayInMillis)

        radioButton0.setOnClickListener {
            currentImageIndex = -1
            changeImage()
            radioButton0.isChecked = true
        }

        radioButton1.setOnClickListener {
            currentImageIndex = 0
            changeImage()
            radioButton1.isChecked = true
        }

        radioButton2.setOnClickListener {
            currentImageIndex = 1
            changeImage()
            radioButton2.isChecked = true
        }

        b.btnRightArrow.setOnClickListener {
            when (currentImageIndex) {
                0 -> {
                    radioButton1.performClick()
                }

                1 -> {
                    radioButton2.performClick()
                }
            }
        }
        b.btnLeftArrow.setOnClickListener {
            when (currentImageIndex) {
                1 -> {
                    radioButton0.performClick()
                }

                2 -> {
                    radioButton1.performClick()
                }
            }
        }


        // rotate stroke 360
        val rotation = ObjectAnimator.ofFloat(b.imgPointsStroke, "rotation", 0f, 360f)
        rotation.duration = 2000
        rotation.repeatCount = ObjectAnimator.INFINITE
        rotation.interpolator = LinearInterpolator()
        rotation.start()

    }

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        handler.removeCallbacks(runnable)
    }

    private fun toastHere(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    // news               news               news               news               news
    // news               news               news               news               news
    // news               news               news               news               news
    private inner class SlowSmoothScroller(context: Context) : LinearSmoothScroller(context) {
        override fun calculateTimeForScrolling(dx: Int): Int {
            // Adjust the duration of the scroll animation here
            return super.calculateTimeForScrolling(dx) * 4 // Increase the value to slow down the animation
        }
    }

    private fun changeImage() {
        currentImageIndex = (currentImageIndex + 1) % newsImages.size
        val smoothScroller = SlowSmoothScroller(this)
        smoothScroller.targetPosition = currentImageIndex
        recyclerView.layoutManager?.startSmoothScroll(smoothScroller)

        // check corresponding radio button
        when (currentImageIndex) {
            0 -> {
                radioButton0.isChecked = true
            }

            1 -> {
                radioButton1.isChecked = true
            }

            2 -> {
                radioButton2.isChecked = true
            }
        }
    }

    // news               news               news               news               news
    // news               news               news               news               news
    // news               news               news               news               news

}