package com.example.payparking.ui.friends.all

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
import com.example.payparking.R
import com.example.payparking.viewmodel.AllViewModel
import com.example.payparking.viewmodel.FriendsRequestsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.all_fragment.*

class AllFragment : Fragment() {

    companion object {
        fun newInstance() = AllFragment()
    }

    private lateinit var viewModel: AllViewModel
    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabaseReference2: DatabaseReference? = null

    private var mDatabase: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.all_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AllViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        val userId = auth.currentUser!!.uid
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Friends").child(userId).child("Friends")
        val users = ArrayList<String>()

        val usersId: ArrayList<String> = ArrayList()
        var title: String? = null
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                for (childSnapshot in dataSnapshot.children) {

                    usersId.add(childSnapshot.key.toString())
                    //title=childSnapshot.child("active").value.toString()
                    //usersId.add(childSnapshot.key.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference!!.addValueEventListener(postListener)

        mDatabaseReference2 = mDatabase!!.reference.child("Users")
        val postListener2 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                for (childSnapshot in dataSnapshot.children) {
                    if(childSnapshot.key.toString() in usersId) {
                        users.add(childSnapshot.child("Name").value.toString())
                    }
                }
                val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                    requireContext(), android.R.layout.simple_expandable_list_item_1, users)

                all_rv.adapter = arrayAdapter

                all_rv.setOnItemClickListener{ adapter, view, i, l ->
                    Toast.makeText(activity, adapter.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference2!!.addValueEventListener(postListener2)


    }

}