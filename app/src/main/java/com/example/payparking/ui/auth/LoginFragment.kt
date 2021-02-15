package com.example.payparking.ui.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.payparking.R
import com.example.payparking.viewmodel.LoginRegisterViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.login_fragment.*


class LoginFragment : Fragment() {

        companion object {
            fun newInstance() = LoginFragment()

        }
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private lateinit var auth: FirebaseAuth

    private lateinit var viewModel: LoginRegisterViewModel

    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        auth = Firebase.auth
        login_button.setOnClickListener {
            signIn(login_email_input.text.toString(), login_password_input.text.toString())
        }
        configureGoogleSignIn()
        setupUI()
        setUpListeners()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginRegisterViewModel::class.java)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            } catch (e: ApiException) {
                Toast.makeText(activity, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        val navController = findNavController()
        if(currentUser != null){
            navController.navigate(R.id.nav_fragment_car)

        }else{

        }
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = login_email_input.text.toString()
        if (TextUtils.isEmpty(email)) {
            login_email_input.error = "Required."
            valid = false
        } else {
            login_email_input.error = null
        }

        val password = login_password_input.text.toString()
        if (TextUtils.isEmpty(password)) {
            login_password_input.error = "Required."
            valid = false
        } else {
            login_password_input.error = null
        }

        return valid
    }

    private fun setUpListeners() {
        login_to_register?.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_register))
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(
                        activity, "Authentication success!",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.nav_fragment_car)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        activity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), mGoogleSignInOptions)
    }

    private fun setupUI() {
        login_google.setOnClickListener {
            googleSignIn()
        }
    }

    private fun googleSignIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val userId = auth!!.currentUser!!.uid

                val currentUserDb = mDatabaseReference!!.child(userId)
                val acct = GoogleSignIn.getLastSignedInAccount(activity)
                if (acct != null) {
                    val personName = acct.displayName
                    val personEmail = acct.email
                    currentUserDb.child("Name").setValue(personName)
                    currentUserDb.child("E-mail").setValue(personEmail)

                }

                findNavController().navigate(R.id.nav_fragment_car)
            } else {
                Toast.makeText(activity, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }
        }
    }
}