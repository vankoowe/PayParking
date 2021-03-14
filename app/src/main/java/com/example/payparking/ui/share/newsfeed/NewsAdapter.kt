package com.example.payparking.ui.share.newsfeed

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.payparking.R
import kotlinx.android.synthetic.main.news_row.view.*

class NewsAdapter(news_dates: ArrayList<String>, descriptions: ArrayList<String>, address: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var newsDatesFilterList = ArrayList<String>()
    var descriptionsFilterList = ArrayList<String>()
    var addressFilterList = ArrayList<String>()
    lateinit var mcontext: Context

    class NewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        newsDatesFilterList = news_dates
        descriptionsFilterList = descriptions
        addressFilterList = address
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val newsListView =
            LayoutInflater.from(parent.context).inflate(R.layout.news_row, parent, false)
        val sch = NewsHolder(newsListView)
        mcontext = parent.context
        return sch
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.date_news_row.text = newsDatesFilterList[position]
        holder.itemView.description_news_row.text = descriptionsFilterList[position]
        holder.itemView.name_address_news_row.text = addressFilterList[position]

        holder.itemView.setOnClickListener {
            Toast.makeText(mcontext, newsDatesFilterList[position], Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int {
        return newsDatesFilterList.size
    }

}