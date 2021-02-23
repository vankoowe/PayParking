package com.example.payparking.ui.friends.search

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
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.search_friends_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class Search_Adapter(private var userNames: ArrayList<String>, private var usersId: ArrayList<String>, private var name: String?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

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
            LayoutInflater.from(parent.context).inflate(R.layout.search_friends_row, parent, false)
        val sch = UsernameHolder(userNameListView)
        mcontext = parent.context
        return sch
    }

    override fun getItemCount(): Int {
        return userNamesFilterList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.srow_text.text = userNamesFilterList[position]

        holder.itemView.setOnClickListener {

            Toast.makeText(mcontext, userNamesFilterList[position], Toast.LENGTH_LONG).show()

        }
        holder.itemView.srow_toggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(mcontext,"SENT", Toast.LENGTH_SHORT).show()
                auth = Firebase.auth
                val userId = auth.currentUser!!.uid
                mDatabase = FirebaseDatabase.getInstance()
                mDatabaseReferenceS = mDatabase!!.reference.child("Friends").child(userId).child("Sent_Requests").child(usersId[position])
                //val currentUserDb3 = mDatabaseReference!!.child(userId).child("Cars")
                mDatabaseReferenceS!!.setValue(userNamesFilterList[position])
                mDatabaseReferenceR = mDatabase!!.reference.child("Friends").child(usersId[position]).child("Friends_Requests").child(userId)
                mDatabaseReferenceR!!.setValue(name)
            } else {
                Toast.makeText(mcontext, "UNSENT", Toast.LENGTH_SHORT).show()
                auth = Firebase.auth
                val userId = auth.currentUser!!.uid
                mDatabase = FirebaseDatabase.getInstance()
                mDatabaseReferenceS = mDatabase!!.reference.child("Friends").child(userId).child("Sent_Requests").child(usersId[position])
                mDatabaseReferenceS!!.removeValue()
                mDatabaseReferenceR = mDatabase!!.reference.child("Friends").child(usersId[position]).child("Friends_Requests").child(userId)
                mDatabaseReferenceR!!.removeValue()

            }
        }
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    userNamesFilterList = userNames
                } else {
                    val resultList = ArrayList<String>()
                    for (row in userNames) {
                        if (row.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    userNamesFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = userNamesFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                userNamesFilterList = results?.values as ArrayList<String>
                notifyDataSetChanged()
            }

        }
    }

}