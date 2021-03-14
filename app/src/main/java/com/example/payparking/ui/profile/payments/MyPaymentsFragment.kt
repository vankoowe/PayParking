package com.example.payparking.ui.profile.payments

import android.content.ContentValues
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.payparking.R
import com.example.payparking.viewmodel.MyPaymentsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.all_fragment.*
import kotlinx.android.synthetic.main.my_payments_fragment.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MyPaymentsFragment : Fragment() {

    companion object {
        fun newInstance() = MyPaymentsFragment()
    }

    private lateinit var viewModel: MyPaymentsViewModel
    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabaseReference2: DatabaseReference? = null
    private var mDatabaseReference3: DatabaseReference? = null

    private var mDatabase: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_payments_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyPaymentsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()
        val payments = ArrayList<String>()
        auth = Firebase.auth
        val userId = auth.currentUser!!.uid
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users").child(userId).child("Payments")

        //var registrationNumber: String? = null
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(childSnapshot in dataSnapshot.children){
                    val date = childSnapshot.key.toString()
                    val address = childSnapshot.child("Address").value.toString()
                    val number = childSnapshot.child("Number").value.toString()
                    payments.add("Number: "+number+" Date: "+date+"\nAddress: "+address)
                }
                val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                    requireContext(), android.R.layout.simple_expandable_list_item_1, payments)

                payments_rv.adapter = arrayAdapter

                payments_rv.setOnItemClickListener{ adapter, view, i, l ->
                    Toast.makeText(activity, adapter.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show()
                }
            }


            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference!!.addValueEventListener(postListener)




    }

}