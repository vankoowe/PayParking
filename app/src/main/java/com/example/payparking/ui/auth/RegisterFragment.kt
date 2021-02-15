package com.example.payparking.ui.auth

import android.content.ContentValues.TAG
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.payparking.R
import com.example.payparking.viewmodel.LoginRegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.register_fragment.*

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private lateinit var viewModel: LoginRegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        register_button.setOnClickListener {
            createAccount(register_name_input.text.toString(), register_email_input.text.toString(), register_password_input.text.toString())
        }
        setUpListeners()    }

    private fun setUpListeners() {
        register_to_login?.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_login))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginRegisterViewModel::class.java)
        // TODO: Use the ViewModel
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = register_email_input.text.toString()
        if (TextUtils.isEmpty(email)) {
            register_email_input.error = "Required."
            valid = false
        } else {
            register_email_input.error = null
        }

        val password = register_password_input.text.toString()
        if (TextUtils.isEmpty(password)) {
            register_password_input.error = "Required."
            valid = false
        } else {
            register_password_input.error = null
        }

        return valid
    }

    private fun createAccount(name: String, email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }


        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val userId = auth!!.currentUser!!.uid

                    val currentUserDb = mDatabaseReference!!.child(userId)
                    currentUserDb.child("Name").setValue(name)
                    currentUserDb.child("E-mail").setValue(email)

                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(activity, "Registration success!",
                        Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.nav_fragment_car)


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(activity, "Registration failed.",
                        Toast.LENGTH_SHORT).show()
                }

                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        // [END create_user_with_email]
    }

}