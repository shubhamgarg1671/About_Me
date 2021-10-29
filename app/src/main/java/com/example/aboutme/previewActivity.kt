package com.example.aboutme

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
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
import io.github.ponnamkarthik.richlinkpreview.RichLinkView
import io.github.ponnamkarthik.richlinkpreview.ViewListener


class previewActivity : AppCompatActivity() {
    val TAG = "previewActivity"
    lateinit var richLinkView:RichLinkView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)
        val uid = FirebaseAuth.getInstance().uid!!
        val database = FirebaseDatabase.getInstance()
        var facebookLink:String? = null
        var instagramLink:String? = null
        var linkedinLink:String? = null
        val facebookButton:ImageButton = findViewById(R.id.facebookButton)
        facebookButton.setOnClickListener { redirecttoURL(facebookLink!!) }
        val instagramButton:ImageButton = findViewById(R.id.instagramButton)
        instagramButton.setOnClickListener { redirecttoURL(instagramLink!!) }
        val linkedinButton:ImageButton = findViewById(R.id.linkedinButton)
        linkedinButton.setOnClickListener { redirecttoURL(linkedinLink!!) }

        var otherSocialLink:String = ""

        var emailAddress:String? = null
        var Address:String? = null
        var phoneNumber:String? = null


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

        var website:String? = null

        val websiteButton:ImageButton = findViewById(R.id.websiteButton)
        websiteButton.setOnClickListener { redirecttoURL(website!!) }

        var yourLink:String = ""
        val profileName2:TextView = findViewById(R.id.profileName2)
        val bio_text2:TextView = findViewById(R.id.bio_text2)
        val myRef = database.getReference("user/$uid")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue()
                Log.d(TAG, "Value is: $value")

                facebookLink = dataSnapshot.child("facebook").getValue() as String?
                if (facebookLink == null) {
                    facebookButton.visibility = View.GONE
                }
                instagramLink = dataSnapshot.child("instagram").getValue() as String?
                if (instagramLink == null) {
                    instagramButton.visibility = View.GONE
                }
                linkedinLink = dataSnapshot.child("linkedin").getValue() as String?
                if (linkedinLink == null) {
                    instagramButton.visibility = View.GONE
                }
                otherSocialLink = dataSnapshot.child("others").getValue().toString()
                emailAddress = dataSnapshot.child("email").getValue() as String?
                if (emailAddress == null) {
                    emailButton.visibility = View.GONE
                }
                Address = dataSnapshot.child("address").getValue() as String?
                if (Address == null) {
                    addressButton.visibility = View.GONE
                }
                phoneNumber = dataSnapshot.child("phone number").getValue() as String?
                if (phoneNumber == null) {
                    phoneButton.visibility = View.GONE
                }
                website = dataSnapshot.child("website").getValue() as String?

                if (website == null) {
                    websiteButton.visibility = View.GONE
                }
                yourLink = dataSnapshot.child("yourLink").getValue().toString()
                profileName2.setText(dataSnapshot.child("profileName").getValue().toString())
                bio_text2.setText(dataSnapshot.child("bio").getValue().toString())
//                copyToClipBoard(yourLink)
//                Log.d(TAG, "yourLink ${yourLink}")
//                richLinkView = findViewById<RichLinkView>(R.id.richLinkView)
//        richLinkView.setLink(
//            yourLink,
//            object : ViewListener {
//                override fun onSuccess(status: Boolean) {
//                    Log.d(TAG, "onSuccess() called with: status = $status")
//                }
//
//                override fun onError(e: Exception) {
//                    Log.e(TAG, "onError() called with: e = $e")
//                }
//            }
//        )
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })
        val moreProfileButton:ImageButton = findViewById(R.id.moreProfileButton)
        moreProfileButton.setOnClickListener { redirecttoURL(otherSocialLink) }

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
        val backButtonPreview:ImageButton = findViewById(R.id.backButtonPreview)
        backButtonPreview.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    fun redirecttoURL(url:String) {
        Log.d(TAG, "redirecttoURL() called with: url = $url")
        val httpIntent = Intent(Intent.ACTION_VIEW)
        httpIntent.data = Uri.parse(url)
        startActivity(httpIntent)
    }
    private fun copyToClipBoard(str: String) {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label.toString(), str)
        clipboard.setPrimaryClip(clip)
    }

}