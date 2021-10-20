package com.example.aboutme

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.FileNotFoundException
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    private val PICK_FROM_GALLARY = 2
    lateinit var addProfileImage:ImageView
    lateinit var profileName:TextView
    lateinit var bio_text:TextView
    lateinit var myRef:DatabaseReference
    lateinit var database:FirebaseDatabase
    lateinit var uid:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val auth = FirebaseAuth.getInstance()
        uid = auth.uid!!
        database = FirebaseDatabase.getInstance()
        profileName = findViewById(R.id.profileName)
        bio_text = findViewById(R.id.bio_text)
        myRef = database.getReference("user/$uid")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
//                val value = dataSnapshot.getValue()
//                Log.d(TAG, "Value is: $value")

                profileName.setText(dataSnapshot.child("profileName").getValue<String>())
                bio_text.setText(dataSnapshot.child("bio").getValue<String>())
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", error.toException())
            }
        })


        val storage = FirebaseStorage.getInstance()
        addProfileImage = findViewById<ImageView>(R.id.addProfileImage)
        val storageReference: StorageReference = storage.reference
        val photoReference:StorageReference = storageReference.child("image/$uid/profilePicture")
        val ONE_MEGABYTE = (1024 * 1024 * 5).toLong()
        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            addProfileImage.setImageBitmap(bmp)
        }.addOnFailureListener {
            Log.e(TAG, "onCreate() called ${it.message}")
            Toast.makeText(
                applicationContext,
                "No Such file or Path found!!",
                Toast.LENGTH_LONG
            ).show()
        }
        addProfileImage.setOnClickListener{
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_FROM_GALLARY)
        }
        val addFacebookButton:ImageView = findViewById(R.id.addFacebookButton)
        addFacebookButton.setOnClickListener {
            dialogBoxwithEdittext("Facebook")
        }
        val addInstagramButton:ImageView = findViewById(R.id.addInstagramButton)
        addInstagramButton.setOnClickListener {
            dialogBoxwithEdittext("Instagram")
        }
        val addLinkedinButton:ImageView = findViewById(R.id.addLinkedinButton)
        addLinkedinButton.setOnClickListener {
            dialogBoxwithEdittext("Linkedin")
        }
        val addMoreProfileButton:ImageView = findViewById(R.id.addMoreProfileButton)
        addMoreProfileButton.setOnClickListener {
            dialogBoxwithEdittext("Facebook")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_FROM_GALLARY) {
            if (resultCode == RESULT_OK) {
                try {
                    val imageUri: Uri? = data?.getData()
                    val imageStream: InputStream? = imageUri?.let {
                        contentResolver.openInputStream(
                            it
                        )
                    }
                    val selectedImage = BitmapFactory.decodeStream(imageStream)
                    addProfileImage.setImageBitmap(selectedImage)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun dialogBoxwithEdittext(title:String) {
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)

        val edittext = EditText(this)
        alert.setMessage("Add Profile Link")
        alert.setTitle(title)

        alert.setCancelable(false)
        alert.setView(edittext)

        alert.setPositiveButton("Ok"
        ) { dialog, whichButton -> //What ever you want to do with the value
            val temporaryString = edittext.text.toString()
            myRef = database.getReference("user/$uid/${title.lowercase()}Link")
            myRef.setValue(temporaryString)
        }

        alert.setNegativeButton("Cancel"
        ) { dialog, whichButton ->
            // what ever you want to do with No option.
        }

        alert.show()
    }
}