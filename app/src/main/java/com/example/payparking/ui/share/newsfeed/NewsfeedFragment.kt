package com.example.payparking.ui.share.newsfeed

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
import com.example.payparking.viewmodel.NewsfeedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.friends_requests_fragment.*
import kotlinx.android.synthetic.main.newsfeed_fragment.*

class NewsfeedFragment : Fragment() {

    companion object {
        fun newInstance() = NewsfeedFragment()
    }

    private lateinit var viewModel: NewsfeedViewModel
    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.newsfeed_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NewsfeedViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()
        news_rv.layoutManager = LinearLayoutManager(news_rv.context)
        news_rv.setHasFixedSize(true)
        getNews()
    }

    fun getNews(){
        auth = Firebase.auth
        val userId = auth.currentUser!!.uid
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users").child(userId).child("News")
        val news_dates: ArrayList<String> = ArrayList()
        val descriptions: ArrayList<String> = ArrayList()
        val address: ArrayList<String> = ArrayList()

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (childSnapshot in dataSnapshot.children) {
                    news_dates.add(childSnapshot.key.toString())
                    descriptions.add(childSnapshot.child("Description").value.toString())
                    address.add(childSnapshot.child("Author").value.toString()
                            +" "+childSnapshot.child("Address").value.toString())

                }
                adapter = NewsAdapter(news_dates, descriptions, address)
                news_rv.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference!!.addValueEventListener(postListener)
    }

}