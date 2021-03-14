package com.example.payparking.ui.friends.search

import android.content.ContentValues
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.payparking.R
import com.example.payparking.viewmodel.SearchFriendsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.search_friends_fragment.*

class SearchFriendsFragment : Fragment() {

    companion object {
        fun newInstance() = SearchFriendsFragment()
    }

    private lateinit var viewModel: SearchFriendsViewModel
    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference2: DatabaseReference? = null

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    lateinit var adapter: Search_Adapter
    //lateinit var userName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_friends_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchFriendsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()

        search_rv.layoutManager = LinearLayoutManager(search_rv.context)
        search_rv.setHasFixedSize(true)

        search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })
        getListOfNames()
    }

    private fun getListOfNames() {
        auth = Firebase.auth
        val userId = auth.currentUser!!.uid
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mDatabaseReference2 = mDatabase!!.reference.child("Friends").child(userId)
        val users: ArrayList<String> = ArrayList()
        val usersId: ArrayList<String> = ArrayList()
        var userName: String? = null
        val friends: ArrayList<String> = ArrayList()
        val friends_requests: ArrayList<String> = ArrayList()
        val flags: ArrayList<Boolean> = ArrayList()

        val postListener2 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                for (childSnapshot in dataSnapshot.child("Friends").children) {
                        friends.add(childSnapshot.key.toString())
                }
                for (childSnapshot in dataSnapshot.child("Sent_Requests").children) {
                    friends_requests.add(childSnapshot.key.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference2!!.addValueEventListener(postListener2)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                for (childSnapshot in dataSnapshot.children) {
                    if(!childSnapshot.key.toString().equals(userId) && !friends.contains(childSnapshot.key.toString())){
                        users.add(childSnapshot.child("Name").value.toString())
                        usersId.add(childSnapshot.key.toString())
                        flags.add(friends_requests.contains(childSnapshot.key.toString()))
                    }else{
                        userName=childSnapshot.child("Name").value.toString()
                    }

                }
                if(search_rv!=null) {
                    adapter = Search_Adapter(users, usersId, userName, flags)
                    search_rv.adapter = adapter
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

