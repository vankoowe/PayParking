package com.example.payparking

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private lateinit var progressLayout: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uri = intent.data
        if (uri != null) {
            // if the uri is not null then we are getting the
            // path segments and storing it in list.
            val parameters = uri.pathSegments

            // after that we are extracting string from that parameters.
            val param = parameters[parameters.size - 1]

            // on below line we are setting
            // that string to our text view
            // which we got as params.
            //messageTV.setText(param)
            auth = Firebase.auth
            mDatabase = FirebaseDatabase.getInstance()
            mDatabaseReference = mDatabase!!.reference.child("Users")
            val userId = auth.currentUser!!.uid

            val currentUserDb = mDatabaseReference!!.child(userId)
            currentUserDb.child("Cars").child(param).setValue("1")
            currentUserDb.child("Cars").child("active").setValue(param)
        }
        //progressLayout = findViewById(R.id.progress_layout)

    }

    fun showProgress() {
        progressLayout.visibility = View.VISIBLE
    }

    fun hideProgress() {
        progressLayout.visibility = View.GONE
    }


}