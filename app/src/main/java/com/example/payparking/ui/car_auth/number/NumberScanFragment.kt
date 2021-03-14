package com.example.payparking.ui.car_auth.number

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.payparking.R
import com.example.payparking.viewmodel.CarViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.number_scan_fragment.*

class NumberScanFragment : Fragment() {

    companion object {
        fun newInstance() = NumberScanFragment()
    }

    private lateinit var auth: FirebaseAuth
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.number_scan_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        selectImageBtn?.setOnClickListener{
            pickImage()
        }

        captureImageBtn.setOnClickListener {
            capturePhoto()
        }

        processImageBtn?.setOnClickListener{
            processImage(processImageBtn)
        }

        scan_car?.setOnClickListener{
            //Navigation.createNavigateOnClickListener(R.id.nav_fragment_car)
            auth = Firebase.auth
            mDatabase = FirebaseDatabase.getInstance()
            mDatabaseReference = mDatabase!!.reference.child("Users")
            val userId = auth.currentUser!!.uid

            val currentUserDb = mDatabaseReference!!.child(userId)
            currentUserDb.child("Cars").child(ocrResultEt.text.toString()).setValue("1")
            findNavController().navigate(R.id.nav_fragment_car)

        }

    }

    fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            ocrImageView.setImageURI(data!!.data)
        }
            if (resultCode == Activity.RESULT_OK && requestCode == 200 && data != null){
                ocrImageView.setImageBitmap(data.extras?.get("data") as Bitmap)
            }
    }
    fun capturePhoto() {

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 200)
    }

    fun processImage(v: View) {
        if (ocrImageView.drawable != null) {
            ocrResultEt.setText("")
            v.isEnabled = false
            val bitmap = (ocrImageView.drawable as BitmapDrawable).bitmap
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    v.isEnabled = true
                    processResultText(firebaseVisionText)
                }
                .addOnFailureListener {
                    v.isEnabled = true
                    ocrResultEt.setText("Failed")
                }
        } else {
            Toast.makeText(activity, "Select an Image First", Toast.LENGTH_LONG).show()
        }

    }


    private fun processResultText(resultText: FirebaseVisionText) {
        if (resultText.textBlocks.size == 0) {
            ocrResultEt.setText("No Text Found")
            return
        }
        for (block in resultText.textBlocks) {
            val blockText = block.text
            if(blockText.get(0).equals('C') && blockText.get(0).isUpperCase()) ocrResultEt.append(blockText + "\n")
        }
    }
}
