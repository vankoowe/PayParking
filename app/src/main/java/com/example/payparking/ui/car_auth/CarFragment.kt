package com.example.payparking.ui.car_auth

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.payparking.R
import com.example.payparking.viewmodel.CarViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.car_fragment.*
import kotlinx.android.synthetic.main.login_fragment.*


class CarFragment : Fragment() {

    companion object {
        fun newInstance() = CarFragment()
    }

    private lateinit var viewModel: CarViewModel

    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.car_fragment, container, false)
        return view
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
        }
        auth = Firebase.auth
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("Users")
        val mUser = auth.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activity, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                    auth = Firebase.auth
                    mDatabase = FirebaseDatabase.getInstance()
                    mDatabaseReference = mDatabase!!.reference.child("Users")
                    val userId = auth.currentUser!!.uid

                    val currentUserDb = mDatabaseReference!!.child(userId)
                    currentUserDb.child("Cars").child(result.contents).child("active").setValue("1")
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        val userId = auth.currentUser!!.uid
        val currentUserDb3 = mDatabaseReference!!.child(userId).child("Cars")
        val cars: ArrayList<String> = ArrayList()

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                for (childSnapshot in dataSnapshot.children) {
                        cars.add(childSnapshot.key.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        currentUserDb3.addValueEventListener(postListener)

        car_choose.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Choose car")

            builder.setItems(cars.toArray(arrayOfNulls<String>(0))
            ) { dialogInterface, i ->
                car_number.text = cars.toArray().get(i).toString()
            }

            val mDialog = builder.create()
            mDialog.show()
        }
        car_add_number_scan?.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_scan))
        car_add_number_qr?.setOnClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this@CarFragment)

            integrator.setPrompt("Scan QR code")
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.initiateScan()

        }
        car_add_number_link?.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.nav_fragment_link))
        car_next?.setOnClickListener {
            if (car_number.text.isNullOrEmpty()) {
                Toast.makeText(activity, "Choose car! ", Toast.LENGTH_LONG).show()
            } else {
                findNavController().navigate(R.id.nav_fragment_home)
                auth = Firebase.auth
                mDatabase = FirebaseDatabase.getInstance()
                mDatabaseReference = mDatabase!!.reference.child("Users")
                val userId = auth.currentUser!!.uid

                val currentUserDb = mDatabaseReference!!.child(userId)
                //val car: String
                for(car in cars){
                    if(car.equals(car_number.text.toString())) {
                        currentUserDb.child("Cars").child(car).child("active").setValue("1")
                    }else{
                        currentUserDb.child("Cars").child(car).child("active").setValue("0")
                    }
                }
                //currentUserDb.child("Cars").child(result.contents).child("active").setValue("1")
            }
        }
        car_log_out?.setOnClickListener{signOut()}

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CarViewModel::class.java)
        // TODO: Use the ViewModel
    }


    private fun signOut() {
        auth.signOut()
        findNavController().navigate(R.id.nav_fragment_login)
    }

}
