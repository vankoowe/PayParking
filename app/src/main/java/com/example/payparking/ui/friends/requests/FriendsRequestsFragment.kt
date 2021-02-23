package com.example.payparking.ui.friends.requests

import android.content.ContentValues
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.payparking.R
import com.example.payparking.ui.friends.search.Search_Adapter
import com.example.payparking.viewmodel.FriendsRequestsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.friends_requests_fragment.*
import kotlinx.android.synthetic.main.search_friends_fragment.*

class FriendsRequestsFragment : Fragment() {

    companion object {
        fun newInstance() = FriendsRequestsFragment()
    }

    private lateinit var viewModel: FriendsRequestsViewModel
    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabaseReference2: DatabaseReference? = null

    private var mDatabase: FirebaseDatabase? = null
    lateinit var adapter: Requests_Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.friends_requests_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FriendsRequestsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()
        requests_rv.layoutManager = LinearLayoutManager(requests_rv.context)
        requests_rv.setHasFixedSize(true)
        getListOfNames()
    }

    private fun getListOfNames() {
        auth = Firebase.auth
        val userId = auth.currentUser!!.uid
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Friends").child(userId).child("Friends_Requests")
        val users: ArrayList<String> = ArrayList()
        val usersId: ArrayList<String> = ArrayList()

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {

                    usersId.add(childSnapshot.key.toString())
                    users.add(childSnapshot.value.toString())
                }
                adapter = Requests_Adapter(users, usersId, userId)
                requests_rv.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference!!.addValueEventListener(postListener)


    }

}