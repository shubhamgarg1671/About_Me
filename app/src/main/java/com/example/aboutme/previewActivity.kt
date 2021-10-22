package com.example.aboutme

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class previewActivity : AppCompatActivity() {
    val TAG = "previewActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        val uid = FirebaseAuth.getInstance().uid!!
        val database = FirebaseDatabase.getInstance()
        var facebookLink:String = ""
        var instagramLink:String = ""
        var linkedinLink:String = ""
        var otherSocialLink:String = ""
        var emailAddress:String = ""
        var Address:String = ""
        var phoneNumber:String = ""
        var website:String = ""
        val profileName2:TextView = findViewById(R.id.profileName2)
        val bio_text2:TextView = findViewById(R.id.bio_text2)
        val myRef = database.getReference("user/$uid")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue()
                Log.d(TAG, "Value is: $value")

                facebookLink = dataSnapshot.child("facebook").getValue().toString()
                instagramLink = dataSnapshot.child("instagram").getValue().toString()
                linkedinLink = dataSnapshot.child("linkedin").getValue().toString()
                otherSocialLink = dataSnapshot.child("others").getValue().toString()
                emailAddress = dataSnapshot.child("email").getValue().toString()
                Address = dataSnapshot.child("address").getValue().toString()
                phoneNumber = dataSnapshot.child("phone number").getValue().toString()
                website = dataSnapshot.child("website").getValue().toString()

                profileName2.setText(dataSnapshot.child("profileName").getValue().toString())
                bio_text2.setText(dataSnapshot.child("bio").getValue().toString())
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })
        val facebookButton:ImageButton = findViewById(R.id.facebookButton)
        facebookButton.setOnClickListener { redirecttoURL(facebookLink) }
        val instagramButton:ImageButton = findViewById(R.id.instagramButton)
        instagramButton.setOnClickListener { redirecttoURL(instagramLink) }
        val linkedinButton:ImageButton = findViewById(R.id.linkedinButton)
        linkedinButton.setOnClickListener { redirecttoURL(linkedinLink) }
        val moreProfileButton:ImageButton = findViewById(R.id.moreProfileButton)
        moreProfileButton.setOnClickListener { redirecttoURL(otherSocialLink) }
        val emailButton:ImageButton = findViewById(R.id.emailButton)
        emailButton.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", emailAddress, null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddress)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body")
            startActivity(Intent.createChooser(emailIntent, "Send email..."))

        }
        val addressButton:ImageButton = findViewById(R.id.addressButton)
        addressButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("geo:0,0?q=$Address")
            }
            //Log.d(TAG, "if statement mapButton.setOnClickListener ")
            startActivity(intent)
        }
        val phoneButton:ImageButton = findViewById(R.id.phoneButton)
        phoneButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${phoneNumber}")
            startActivity(intent)
        }
        val websiteButton:ImageButton = findViewById(R.id.websiteButton)
        websiteButton.setOnClickListener { redirecttoURL(website) }

        val storage = FirebaseStorage.getInstance()
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val storageReference: StorageReference = storage.reference
        val photoReference: StorageReference = storageReference.child("image/$uid/profilePicture")
        val ONE_MEGABYTE = (1024 * 1024 * 5).toLong()
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            profileImage.setImageBitmap(bmp)
        }.addOnFailureListener {
            Log.e(TAG, "onCreate() called ${it.message}")
            Toast.makeText(
                applicationContext,
                "No Such file or Path found!!",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    fun redirecttoURL(url:String) {
        val httpIntent = Intent(Intent.ACTION_VIEW)
        httpIntent.data = Uri.parse(url)
        startActivity(httpIntent)
    }
}