package com.example.payparking.ui.profile.cars

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.payparking.R
import com.example.payparking.viewmodel.MyCarsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.my_cars_fragment.*

class MyCarsFragment : Fragment() {

    companion object {
        fun newInstance() = MyCarsFragment()
    }

    private lateinit var viewModel: MyCarsViewModel
    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_cars_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyCarsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        mDatabase = FirebaseDatabase.getInstance()
        val userId = auth.currentUser!!.uid
        mDatabaseReference = mDatabase!!.reference.child("Users").child(userId).child("Cars")
        val cars: ArrayList<String> = ArrayList()

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                for (childSnapshot in dataSnapshot.children) {
                    if(childSnapshot.key.toString().equals("active") == false) {
                        cars.add(childSnapshot.key.toString())
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference!!.addValueEventListener(postListener)

        my_car_choose.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Choose car")

            builder.setItems(cars.toArray(arrayOfNulls<String>(0))
            ) { dialogInterface, i ->
                my_car_number.setText(cars.toArray().get(i).toString())
            }

            val mDialog = builder.create()
            mDialog.show()
        }
        bt_generate!!.setOnClickListener {
            val sText = my_car_number!!.text.toString().trim { it <= ' ' }
            try {
                iv_output!!.setImageBitmap(BarcodeEncoder().createBitmap(MultiFormatWriter().encode(sText, BarcodeFormat.QR_CODE, 350, 350)))
                (requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(my_car_number!!.applicationWindowToken, 0)
            } catch (e: WriterException) {
                e.printStackTrace()
            }
            sharingText.text = "http://www.pay-parking.com/car/" + my_car_number.text.toString()
        }

        shareTxtBtn.setOnClickListener {
            val text = sharingText.text.toString()

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, text)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "PayParking deep link")
            startActivity(Intent.createChooser(shareIntent, "Share text via"))
        }
    }

}