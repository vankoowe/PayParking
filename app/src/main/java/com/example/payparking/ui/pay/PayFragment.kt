package com.example.payparking.ui.pay

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.braintreepayments.api.models.PayPalConfiguration
import com.example.payparking.R
import com.example.payparking.ui.home.ReminderBroadcast
import com.example.payparking.viewmodel.PaymentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.pay_fragment.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class PayFragment : Fragment() {
//    val API_URL = "YOUR_API_URL"
//    val REQUEST_CODE = 101
//    var token = ""

    private lateinit var viewModel: PaymentViewModel

    private val requestSendSms: Int = 2

    val PAYPAL_REQUEST_CODE = 7171;
//    val config = PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
//        .clientId("Afg3kt8Mh7TaSYrnPdA3SaTZXx2vDYkVQmrHTQYx99tlyJSpJzWvTeS3NMD2qYo9hEqToG1X2zYrdlsK")

    lateinit var config: PayPalConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        config = PayPalConfiguration()
//            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
//            // or live (ENVIRONMENT_PRODUCTION)
//            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
//            .clientId(PayPalConfig.PAYPAL_CLIENT_ID)

    }



    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabaseReference2: DatabaseReference? = null
    private var mDatabaseReference3: DatabaseReference? = null

    private var mDatabase: FirebaseDatabase? = null

    override fun onStart() {
        super.onStart()

        payment_sms.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.SEND_SMS),
                    requestSendSms
                )
            } else {
                sendSMS()
                val sec = 3000
                val i = Intent(context, ReminderBroadcast::class.java)
                val pi = PendingIntent.getBroadcast(context, 111, i, 0)

                val alarmMgr: AlarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmMgr.set(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + (sec * 1000), pi
                )
                Toast.makeText(context, "Paid", Toast.LENGTH_LONG).show()
            }
        }

        payment_paypal.setOnClickListener {

        }
    }

    companion object {
        fun newInstance() = PayFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pay_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == requestSendSms) sendSMS()
    }

    private fun sendSMS() {
        auth = Firebase.auth
        val userId = auth.currentUser!!.uid
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users").child(userId).child("Cars")

        var registrationNumber: String? = null
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                registrationNumber = dataSnapshot.child("active").value.toString()

                mDatabaseReference2 = mDatabase!!.reference.child("Heat_Map").child(userId)

                val postListener2 = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var obj = SmsManager.getDefault()
                        val currentDateTime =
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                LocalDateTime.now()
                            } else {
                                TODO("VERSION.SDK_INT < O")
                            }
                        val time =
                            currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                .toString()

                        val lat = dataSnapshot.child("lat").value
                        val long = dataSnapshot.child("long").value
                        var address: String? = null
                        if (long!=null && lat !=null) {

                            val geocoder: Geocoder
                            val addresses: List<Address>
                            geocoder = Geocoder(context, Locale.getDefault())

                            addresses = geocoder.getFromLocation(
                                lat as Double, long as Double,
                                1
                            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                            address =
                                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        }



                        mDatabaseReference3 = mDatabase!!.reference.child("Users").child(userId).child("Payments")
                            .child(time)
                        if(dataSnapshot.child("zone").value.toString().equals("2")) {
                            obj.sendTextMessage("1302", null, registrationNumber, null, null)
                            mDatabaseReference3!!.child("Number").setValue("1302")
                            mDatabaseReference3!!.child("Address").setValue(address)

                        }
                        if(dataSnapshot.child("zone").value.toString().equals("1")) {
                            //obj.sendTextMessage("1303", null, registrationNumber, null, null)
                            mDatabaseReference3!!.child("Number").setValue("1303")
                            mDatabaseReference3!!.child("Address").setValue(address)

                        }

                        Toast.makeText(activity, "SMS sent", Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
                        Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                    }
                }

                mDatabaseReference2!!.addValueEventListener(postListener2)
            }


            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        mDatabaseReference!!.addValueEventListener(postListener)


    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        getTokenFromServer()
//
//
//        pay.setOnClickListener {
//            if (!token.isEmpty())
//                payNow()
//        }
//    }



//    fun onBraintreeSubmit(v: View?) {
//        var clientToken = "cd92ry75h5qw9m4s"
//        val dropInRequest = DropInRequest()
//            .clientToken(clientToken)
//        startActivityForResult(dropInRequest.getIntent(requireContext()), REQUEST_CODE)
//    }
//
//    internal final fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        if (requestCode == REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                val result: DropInResult =
//                    data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)
//                // use the result to update your UI and send the payment method nonce to your server
//            } else if (resultCode == RESULT_CANCELED) {
//                // the user canceled
//            } else {
//                // handle errors here, an exception may be available in
//                val error = data.getSerializableExtra(DropInActivity.EXTRA_ERROR) as Exception
//            }
//        }
//    }

//    protected override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        try {
//            var mBraintreeFragment = BraintreeFragment.newInstance(activity, mAuthorization)
//            // mBraintreeFragment is ready to use!
//        } catch (e: InvalidArgumentException) {
//            // There was an issue with your authorization string.
//        }
//    }
}