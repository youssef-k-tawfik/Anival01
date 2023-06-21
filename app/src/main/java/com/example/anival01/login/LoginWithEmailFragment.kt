package com.example.anival01.login

//@Suppress("DEPRECATION")
//class LoginWithEmailFragment : Fragment() {
//
//    private lateinit var b: FragmentLoginWithEmailBinding
//    private lateinit var auth: FirebaseAuth
//    private lateinit var dbFirestore: FirebaseFirestore
//    private lateinit var googleSignInClient: GoogleSignInClient
//    private lateinit var callbackManager: CallbackManager
//    private lateinit var email: String
//    private lateinit var password: String
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        b = FragmentLoginWithEmailBinding.inflate(layoutInflater)
//        auth = FirebaseAuth.getInstance()
//        dbFirestore = FirebaseFirestore.getInstance()
//        FacebookSdk.sdkInitialize(requireContext().applicationContext)
//        callbackManager = CallbackManager.Factory.create()
//        return b.root
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        FacebookSdk.setIsDebugEnabled(true)
//        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
//
//        // Initialize other components or perform other necessary actions
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        b.btnLogin.setOnClickListener {
//            loginUser()
//        }
//        b.tvGoToRegister.setOnClickListener {
//            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
//        }
//        b.tvForgotPassword.setOnClickListener {
//            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
//
//        }
//        b.tvResendVerificationLink.setOnClickListener {
//            email = b.tiInsideEmail.text.toString()
//            password = b.tiInsidePassword.text.toString()
//
//            if (email.isNotEmpty() && password.isNotEmpty()) {
//                CoroutineScope(Dispatchers.IO).launch {
//                    try {
//                        // use await to wait for the function to fully finish before continuing with the code
//                        auth.signInWithEmailAndPassword(email, password).await()
//                        // Go to News Feed
//                        if (auth.currentUser?.isEmailVerified == true) {
//                            withContext(Dispatchers.Main) {
//                                toastHere("Email already verified")
//                            }
//                        } else {
//                            auth.currentUser?.sendEmailVerification()?.addOnCompleteListener {
//                                val alertDialog = AlertDialog.Builder(requireContext())
//                                    .setTitle("Verification Email Sent")
//                                    .setMessage("A verification email has been sent to your email address. Please check your inbox and follow the instructions to verify your account.")
//                                    .setPositiveButton("OK") { dialog, _ ->
//                                        dialog.dismiss()
//                                        // Perform any necessary actions after the user acknowledges the dialog
//                                        // Nothing
//                                    }
//                                    .create()
//                                alertDialog.show()
//                            }
//                            auth.signOut()
//                        }
//
//                    } catch (e: Exception) {
//                        // Access UI components inside a coroutine using withContext()
//                        withContext(Dispatchers.Main) {
//                            toastHere(e.message.toString())
//                        }
//                    }
//                }
//            } else toastHere("Email or pw empty")
//
//        }
//
//        // Sign-in with Google
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
//
//        b.googleSignInButton.setOnClickListener {
//            signInWithGoogle()
//        }
//        // End of google sign-in
//
//        // Sign-in with Facebook
//        b.facebookLoginButton.setOnClickListener {
//            signInWithFacebook()
//        }
//        // End of Facebook sign-in
//
//        // testing btn
//        b.btnSkipToNewsFeed.setOnClickListener {
//            goToMainScreen()
//        }
//    }
//
//    //fb s
//
//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        callbackManager.onActivityResult(requestCode, resultCode, data)
//    }
//
//    private fun signInWithFacebook() {
//        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
//        LoginManager.getInstance().registerCallback(callbackManager, facebookCallback)
//    }
//
//    private val facebookCallback = object : FacebookCallback<LoginResult> {
//
//        override fun onSuccess(result: LoginResult) {
//            val accessToken = result.accessToken
//            Timber.tag("token111").i(accessToken.toString()) // same1
//            Timber.tag("token111111111111")
//                .i(AccessToken.getCurrentAccessToken().toString()) // same1
//            Timber.tag("token1124@#$11111")
//                .i(AccessToken.getCurrentAccessToken()?.token) // token only
//
//            val credential = FacebookAuthProvider.getCredential(accessToken.token)
//            Timber.tag("token222").i(credential.toString())
//
//            auth.signInWithCredential(credential)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Timber.tag("koos111").i("sign in facebook succesfful ya3am")
//
//                        val graphRequest = GraphRequest.newMeRequest(accessToken) { json, _ ->
//                            val firstName = json?.optString("first_name")
//                            val lastName = json?.optString("last_name")
//                            val email = json?.optString("email")
//
//                            auth.currentUser?.let { user ->
//                                addNewDocumentForNewUser(
//                                    user.uid,
//                                    firstName!!,
//                                    lastName!!,
//                                    email!!
//                                )
//                            }
//                        }
//                        Timber.tag("koos222").i("wslt hna")
//
//                        val parameters = Bundle()
//                        parameters.putString("fields", "first_name,last_name,email")
//                        graphRequest.parameters = parameters
//                        graphRequest.executeAsync()
//                        Timber.tag("koos333").i("before going to main screen")
//
//                        goToMainScreen()
//                        Timber.tag("koos4444").i("after going to main screen")
//                    } else {
//                        toastHere("see logcat @koos5555555")
//                        Timber.tag("koos5555555").i(task.exception.toString())
//                    }
//                }
//        }
//
//        override fun onCancel() = toastHere("Login canceled.!.")
//        override fun onError(error: FacebookException) {
//            toastHere("see logcat @koos6666 'OnError'")
//            Timber.tag("koos6666").i(error.toString())
//        }
//    }
//
//    //fb e
//
//
//    // Start of google sign-in  ##################################################    // gooooooogle
//    private fun signInWithGoogle() {
//        val signInIntent = googleSignInClient.signInIntent
//        launcher.launch(signInIntent)
//    }
//
//    private val launcher =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//                handleResults(task)
//            }
//        }
//
//    private fun handleResults(task: Task<GoogleSignInAccount>) {
//        if (task.isSuccessful) {
//            val account: GoogleSignInAccount? = task.result
//            if (account != null) {
//                updateUI(account)
//            }
//        } else toastHere(task.exception.toString())
//    }
//
//    private fun updateUI(account: GoogleSignInAccount) {
//        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//        auth.signInWithCredential(credential).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                if (task.result?.additionalUserInfo?.isNewUser == true) {
//                    auth.currentUser?.let { user ->
//                        addNewDocumentForNewUser(
//                            user.uid,
//                            account.givenName.toString(),
//                            account.familyName.toString(),
//                            account.email.toString()
//                        )
//                    }
//                }
//                goToMainScreen()
//            } else toastHere(task.exception.toString())
//        }
//    }
//    // End of google sign-in  ####################################################    // gooooooogle
//
//    private fun loginUser() {
//        email = b.tiInsideEmail.text.toString()
//        password = b.tiInsidePassword.text.toString()
//        if (email.isNotEmpty() && password.isNotEmpty()) {
//            CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    // use await to wait for the function to fully finish before continuing with the code
//                    auth.signInWithEmailAndPassword(email, password).await()
//                    // Go to News Feed
//                    if (auth.currentUser?.isEmailVerified == true) {
//                        withContext(Dispatchers.Main) {
//                            goToMainScreen()
//                        }
//                    } else {
//                        auth.signOut()
//                        withContext(Dispatchers.Main) {
//                            toastHere("Please verify your email first to login")
//                        }
//                    }
//
//                } catch (e: Exception) {
//                    // Access UI components inside a coroutine using withContext()
//                    withContext(Dispatchers.Main) {
//                        toastHere(e.message.toString())
//                    }
//                }
//            }
//        } else toastHere("Email or pw empty")
//    }
//
//    private fun addNewDocumentForNewUser(
//        userID: String,
//        firstName: String,
//        lastName: String,
//        email: String
//    ) {
//        val newUser = User(
//            userID,
//            firstName,
//            lastName,
//            0,
//            email
//        )
//        auth.currentUser?.uid?.let {
//            dbFirestore.collection("users").document(it).set(newUser)
//        }
//    }
//
//    private fun toastHere(msg: String) {
//        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
//    }
//
//    private fun goToMainScreen() {
//        val intent = Intent(requireContext(), MainScreen::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
//    }
//
//    override fun onStart() {
//        super.onStart()
//        if ((auth.currentUser != null && auth.currentUser?.isEmailVerified == true) || AccessToken.getCurrentAccessToken() != null) goToMainScreen()
////        toastHere(AccessToken.getCurrentAccessToken().toString())
//    }
//
//}