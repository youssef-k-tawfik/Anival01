package com.example.anival01.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.anival01.R
import com.example.anival01.databinding.ActivityMainBinding
import com.example.anival01.mainScreen.MainScreen
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber


@Suppress("DEPRECATION") //just to remove warnings
class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private lateinit var stroke0: AppCompatImageView
    private lateinit var stroke1: AppCompatImageView
    private var isRepeatEnabled = true
    private lateinit var auth: FirebaseAuth
    private lateinit var dbFirestore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var provider: String
    private var startY: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        Timber.plant(Timber.DebugTree())
        auth = FirebaseAuth.getInstance()
        dbFirestore = FirebaseFirestore.getInstance()
        FacebookSdk.sdkInitialize(this)
        callbackManager = CallbackManager.Factory.create()
        setProvidersSpinner()

        // strokes animation
        stroke0 = b.strokeBa4a0
        stroke1 = b.strokeBa4a1
        animateImageView(stroke0, 0)
        stroke1.postDelayed({ animateImageView(stroke1, 1500) }, 1500)

        b.btnGetStarted.setOnClickListener {
            // remove button
            b.clMainRoot.removeView(b.btnGetStarted)
            // stop strokes
            isRepeatEnabled = false

            // move logo to top
            val logoAnimator = ValueAnimator.ofInt(120, -1250)
            logoAnimator.addUpdateListener { animator ->
                val layoutParams = b.imgAnivalLogo.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.topMargin = animator.animatedValue as Int
                b.imgAnivalLogo.layoutParams = layoutParams
            }
            logoAnimator.duration = 300
            logoAnimator.start()

            // show sign in constraintlayout
            animateLayout(b.clLoginRootContainer, -500, 0)

        }

        // Forgot password
        b.tvForgotPassword.setOnClickListener {
            email = b.inputEmailSignIn.text.toString()
            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) toastHere("Reset email was successfully sent!")
                        else task.exception?.message?.let { toastHere(it) }
                    }
            } else toastHere("Enter email please")
        }

        // Resend verification
        b.tvResendVerificationLink.setOnClickListener {
            email = b.inputEmailSignIn.text.toString()
            password = b.inputPasswordSignIn.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // use await to wait for the function to fully finish before continuing with the code
                        auth.signInWithEmailAndPassword(email, password).await()
                        // Go to News Feed
                        if (auth.currentUser?.isEmailVerified == true) {
                            withContext(Dispatchers.Main) {
                                toastHere("Email already verified")
                            }
                        } else {
                            auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                                //TODO: Create a modern dialog
                                val alertDialog = AlertDialog.Builder(this@MainActivity)
                                    .setTitle("Verification Email Sent")
                                    .setMessage("A verification email has been sent to your email address. Please check your inbox and follow the instructions to verify your account.")
                                    .setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()
                                        // Perform any necessary actions after the user acknowledges the dialog
                                        // Nothing
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

        // sign in with email
        b.btnSignInWithEmail.setOnClickListener {
            loginUser()
        }

        // sign in with google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        b.btnSignInWithGoogle.setOnClickListener {
            signInWithGoogle()
        }

        // sign in with Facebook
        b.btnSignInWithFacebook.setOnClickListener {
            signInWithFacebook()
        }

        // Create New Account
        b.tvCreateAccount.setOnClickListener {
            // hide sign in layout
            animateLayout(b.clLoginRootContainer, 0, -500)

            // show register layout
            animateLayout(b.clRegisterRootContainer, -1500, 0)
        }

        // Return to Sign in layout
        b.tvGoToSignIn.setOnClickListener {
            // hide register layout
            animateLayout(b.clRegisterRootContainer, 0, -1500)

            // show sign in layout
            animateLayout(b.clLoginRootContainer, -500, 0)
        }

        // Register New Account
        b.btnSignUp.setOnClickListener {
            registerUser()
        }

        // swipe down to go back to sign-in layout
        // ignore warning from studio
        b.clRegisterRootContainer.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    startY = motionEvent.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val endY = motionEvent.y
                    val deltaY = endY - startY
                    if (deltaY > 0 && deltaY > 100) {
                        // Swipe down detected
                        b.tvGoToSignIn.performClick()
                        true
                    } else {
                        view.performClick() // Call performClick for regular click events
                    }
                }
                else -> false
            }
        }

    }

    private fun animateLayout(layout: ConstraintLayout, from: Int, to: Int, duration: Long = 500) {
        val layoutAnimator = ValueAnimator.ofInt(from, to)
        layoutAnimator.addUpdateListener { animator ->
            val layoutParams =
                layout.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.bottomMargin = animator.animatedValue as Int
            layout.layoutParams = layoutParams
        }
        layoutAnimator.duration = duration
        layoutAnimator.start()
    }

    // General coroutines controller
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun toastHere(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    private fun animateImageView(view: View, delay: Long) {
        view.visibility = View.VISIBLE

        val scaleAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 2f)
        scaleAnimatorX.duration = 1000
        scaleAnimatorX.interpolator = AccelerateDecelerateInterpolator()

        val scaleAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 2f)
        scaleAnimatorY.duration = 1000
        scaleAnimatorY.interpolator = AccelerateDecelerateInterpolator()

        val alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        alphaAnimator.duration = 1000

        val resetAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1f)
        resetAnimatorX.duration = 0

        val resetAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1f)
        resetAnimatorY.duration = 0

        val resetAnimatorAlpha = ObjectAnimator.ofFloat(view, "alpha", 1f)
        resetAnimatorAlpha.duration = 0

        resetAnimatorX.start()
        resetAnimatorY.start()
        resetAnimatorAlpha.start()

        val animatorSet = AnimatorSet()
        animatorSet.play(scaleAnimatorX).with(scaleAnimatorY).with(alphaAnimator)
        animatorSet.startDelay = delay
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (isRepeatEnabled) animatorSet.start()
            }
        })

        animatorSet.start()
    }

    private fun loginUser() {
        email = b.inputEmailSignIn.text.toString()
        password = b.inputPasswordSignIn.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    // using await to wait for the function to fully finish before continuing with the code
                    auth.signInWithEmailAndPassword(email, password).await()
                    if (auth.currentUser?.isEmailVerified == true) {
                        withContext(Dispatchers.Main) {
                            successfulSignIn()
                        }
                    } else {
                        auth.signOut()
                        withContext(Dispatchers.Main) {
                            toastHere("Please verify your email first to login")
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        toastHere(e.message.toString())
                    }
                }
            }
        } else toastHere("Email or pw empty")
    }

    private fun successfulSignIn() = startActivity(Intent(this, MainScreen::class.java))


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
            account?.let {
                updateUI(it)
            }
        } else toastHere(task.exception.toString())
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        coroutineScope.launch {
            try {
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result?.additionalUserInfo?.isNewUser == true) {
                            auth.currentUser?.let { user ->
                                addNewDocumentForNewUser(
                                    user.uid,
                                    account.givenName.toString(),
                                    account.familyName.toString(),
                                    account.email.toString()
                                )
                            }
                        }
                        successfulSignIn()
                    } else toastHere(task.exception.toString())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    toastHere(e.message.toString())
                }
            }
        }
    }
    // End of google sign-in  ####################################################    // gooooooogle


    //fb s
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private val loginManager = LoginManager.getInstance()
    private fun signInWithFacebook() {
        loginManager.logInWithReadPermissions(this, listOf("email", "public_profile"))
        loginManager.registerCallback(callbackManager, facebookCallback)
    }


    private val facebookCallback = object : FacebookCallback<LoginResult> {
        private var signInJob: Job? = null

        override fun onSuccess(result: LoginResult) {
            val accessToken = result.accessToken
            val credential = FacebookAuthProvider.getCredential(accessToken.token)
            signInJob = coroutineScope.launch {
                try {
                    auth.signInWithCredential(credential).await()
                    val graphRequest = GraphRequest.newMeRequest(accessToken) { json, _ ->
                        val firstName = json?.optString("first_name")
                        val lastName = json?.optString("last_name")
                        val email = json?.optString("email")

                        auth.currentUser?.let { user ->
                            addNewDocumentForNewUser(
                                user.uid,
                                firstName!!,
                                lastName!!,
                                email!!
                            )
                        }
                    }

                    val parameters = Bundle()
                    parameters.putString("fields", "first_name,last_name,email")
                    graphRequest.parameters = parameters
                    graphRequest.executeAsync()

                    successfulSignIn()
                } catch (e: Exception) {
                    toastHere(e.message.toString())
                }
            }
        }

        override fun onCancel() {
            toastHere("Login canceled.!.")
            signInJob?.cancel()
        }

        override fun onError(error: FacebookException) {
            toastHere("onError: $error")
            signInJob?.cancel()
        }
    }
    //fb e

    // Register New Account
    private fun registerUser() {
        // Get inputs
        firstName = b.registerFirstName.text.toString()
        lastName = b.registerLastName.text.toString()
        email = b.registerEmailAddress.text.toString() + provider
        password = b.registerPassword.text.toString()

        // Verification steps
        if (firstName.length < 2) toastHere("First name cannot be less than 2 characters")
        else if (lastName.length < 2) toastHere("Last name cannot be less than 2 characters")
        else if (email.isEmpty()) toastHere("Email is empty!")
        else if (password.length < 4) toastHere("Password cannot be less than 4 characters")
        else firebaseSignUpProcess()
    }

    private fun firebaseSignUpProcess() {
        coroutineScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
                    // TODO: New Dialog (Modern)
                    val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this@MainActivity)
                        .setTitle("Verification Email Sent")
                        .setMessage("A verification email has been sent to your email address. Please check your inbox and follow the instructions to verify your account.")
                        .setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                            // Perform any necessary actions after the user acknowledges the dialog
                            // Back to Login
                            b.tvGoToSignIn.performClick()
                        }
                        .create()
                    alertDialog.show()
                }
                auth.currentUser?.let { user ->
                    addNewDocumentForNewUser(
                        user.uid,
                        firstName,
                        lastName,
                        email
                    )
                }
                // To be tested if not here will user be logged in? (will need to verify)
                auth.signOut()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    toastHere(e.message.toString())
                }
            }
        }
    }

    private fun setProvidersSpinner() {
        // setting provider spinner
        val providers = arrayOf("@gmail.com", "@yahoo.com", "@hotmail.com", "@outlook.com")
        val adapterProviders =
            ArrayAdapter(this, R.layout.spinner_providers_item, providers)
//            ArrayAdapter(this, android.R.layout.simple_spinner_item, providers)
        adapterProviders.setDropDownViewResource(R.layout.spinner_providers_item)
        b.spinnerProviders.adapter = adapterProviders

        //on provider selected
        b.spinnerProviders.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

    // END REGISTER
    private fun addNewDocumentForNewUser(
        userID: String,
        firstName: String,
        lastName: String,
        email: String
    ) {
        val newUser = User(
            userID,
            firstName,
            lastName,
            0,
            email
        )
        auth.currentUser?.uid?.let {
            dbFirestore.collection("users").document(it).set(newUser)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onStart() {
        super.onStart()
        if ((auth.currentUser != null && auth.currentUser?.isEmailVerified == true) || AccessToken.getCurrentAccessToken() != null) successfulSignIn()
    }

}