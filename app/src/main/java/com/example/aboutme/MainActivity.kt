package com.example.aboutme

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val auth = FirebaseAuth.getInstance()
//        val database = FirebaseDatabase.getInstance()
        val storage = FirebaseStorage.getInstance()
        // Create a storage reference from our app
        val storageRef = storage.reference
        // Create a reference with an initial file path and name
        val pathReference = storageRef.child("image/bu5bUwzdJFc634rbBW2RKAwlfOI3/profilePicture.jpeg")
        // ImageView in your Activity
        val imageView = findViewById<ImageView>(R.id.imageview)
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference
        val photoReference:StorageReference = storageReference.child("image/bu5bUwzdJFc634rbBW2RKAwlfOI3/profilePicture")
        val ONE_MEGABYTE = (1024 * 1024).toLong()
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imageView.setImageBitmap(bmp)
        }.addOnFailureListener {
            Log.e(TAG, "onCreate() called ${it.message}")
            Toast.makeText(
                applicationContext,
                "No Such file or Path found!!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}