package com.example.payparking.ui.friends.requests

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.payparking.R
import com.example.payparking.viewmodel.SearchFriendsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.friends_requests_row.view.*
import kotlinx.android.synthetic.main.search_friends_row.view.*
import kotlinx.android.synthetic.main.search_friends_row.view.srow_text
import java.util.*
import kotlin.collections.ArrayList

class RequestsAdapter(private var userNames: ArrayList<String>, private var usersId: ArrayList<String>, private var userId: String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var userNamesFilterList = ArrayList<String>()
    private lateinit var viewModel: SearchFriendsViewModel
    private lateinit var auth: FirebaseAuth
    private var mDatabaseReferenceS: DatabaseReference? = null
    private var mDatabaseReferenceR: DatabaseReference? = null

    private var mDatabase: FirebaseDatabase? = null
    lateinit var mcontext: Context

    class UsernameHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        userNamesFilterList = userNames
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val userNameListView =
            LayoutInflater.from(parent.context).inflate(R.layout.friends_requests_row, parent, false)
        val sch = UsernameHolder(userNameListView)
        mcontext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return userNamesFilterList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.rrow_text.text = userNamesFilterList[position]

        holder.itemView.setOnClickListener {

            Toast.makeText(mcontext, userNamesFilterList[position], Toast.LENGTH_LONG).show()

        }
        holder.itemView.rrow_toggle_delete.setOnClickListener {
            //auth = Firebase.auth
            //val userId = auth.currentUser!!.uid
            mDatabase = FirebaseDatabase.getInstance()

            mDatabaseReferenceS = mDatabase!!.reference.child("Friends").child(userId).child("Friends_Requests").child(usersId[position])
            mDatabaseReferenceS!!.removeValue()
            mDatabaseReferenceR = mDatabase!!.reference.child("Friends").child(usersId[position]).child("Sent_Requests").child(userId)
            mDatabaseReferenceR!!.removeValue()

            userNamesFilterList.removeAt(position)
            notifyDataSetChanged()
        }
        holder.itemView.rrow_toggle_accept.setOnClickListener {

            mDatabase = FirebaseDatabase.getInstance()
            mDatabaseReferenceS = mDatabase!!.reference.child("Friends").child(userId).child("Friends").child(usersId[position])
            mDatabaseReferenceS!!.setValue("1")
            mDatabaseReferenceR = mDatabase!!.reference.child("Friends").child(usersId[position]).child("Friends").child(userId)
            mDatabaseReferenceR!!.setValue("1")
            mDatabaseReferenceS = mDatabase!!.reference.child("Friends").child(userId).child("Friends_Requests").child(usersId[position])
            mDatabaseReferenceS!!.removeValue()
            mDatabaseReferenceR = mDatabase!!.reference.child("Friends").child(usersId[position]).child("Sent_Requests").child(userId)
            mDatabaseReferenceR!!.removeValue()
            userNamesFilterList.removeAt(position)
            notifyDataSetChanged()
        }

    }
}