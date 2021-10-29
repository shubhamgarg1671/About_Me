package com.example.aboutme

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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

            val factory = LayoutInflater.from(this)
            val dialogView: View = factory.inflate(R.layout.facebook_dialogbox, null)
            val dialogImageCardFb:CardView = dialogView.findViewById(R.id.dialogImageCardFb)
            dialogImageCardFb.bringToFront()
            val dialog = AlertDialog.Builder(this).create()
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            dialog.setView(dialogView)
            dialogView.findViewById<Button>(R.id.dialog_fb).setOnClickListener{
                val fblink:EditText = dialogView.findViewById<EditText>(R.id.dialog_fb_link)
                val link:String = fblink.text.toString()
                if( Patterns.WEB_URL.matcher(link).matches()) {
                        myRef = database.getReference("user/$uid/Facebook")
                        myRef.setValue(link)
                        Log.d(TAG, "onDialog $link")
                        dialog.cancel()
                } else {
                        Toast.makeText(this, "Link is not correct", Toast.LENGTH_LONG).show()
                }
            }
            dialogView.findViewById<TextView>(R.id.fb_dialog_cancel).setOnClickListener {
                dialog.cancel()
            }
            dialog.show()

        }
        val addInstagramButton:ImageView = findViewById(R.id.addInstagramButton)
        addInstagramButton.setOnClickListener {
            val factory = LayoutInflater.from(this)
            val dialogView: View = factory.inflate(R.layout.instagram_dialogue_box, null)
            val dialogImageCardInsta:CardView = dialogView.findViewById(R.id.dialogImageCardInsta)
            dialogImageCardInsta.bringToFront()
            val dialog = AlertDialog.Builder(this).create()
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            dialog.setView(dialogView)
            dialogView.findViewById<Button>(R.id.dialog_insta).setOnClickListener{
                val instalink:EditText = dialogView.findViewById<EditText>(R.id.dialog_insta_link)
                val link:String = instalink.text.toString()
                if (Patterns.WEB_URL.matcher(link).matches()) {
                    myRef = database.getReference("user/$uid/Instagram")
                    myRef.setValue(link)
                    Log.d(TAG, "onDialog $link")
                    dialog.cancel()
                } else {
                    Toast.makeText(this, "Link is not correct", Toast.LENGTH_LONG).show()
                }
            }
            dialogView.findViewById<TextView>(R.id.insta_dialog_cancel).setOnClickListener {
                dialog.cancel()
            }
            dialog.show()
        }
        val addLinkedinButton:ImageView = findViewById(R.id.addLinkedinButton)
        addLinkedinButton.setOnClickListener {
            val factory = LayoutInflater.from(this)
            val dialogView: View = factory.inflate(R.layout.instagram_dialogue_box, null)
            val dialogImageCardInsta:CardView = dialogView.findViewById(R.id.dialogImageCardInsta)
            dialogImageCardInsta.bringToFront()
            val dialog = AlertDialog.Builder(this).create()
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            dialog.setView(dialogView)
            dialogView.findViewById<Button>(R.id.dialog_insta).setOnClickListener{
                val instalink:EditText = dialogView.findViewById<EditText>(R.id.dialog_insta_link)
                val link:String = instalink.text.toString()
                if (Patterns.WEB_URL.matcher(link).matches()) {
                    myRef = database.getReference("user/$uid/Instagram")
                    myRef.setValue(link)
                    Log.d(TAG, "onDialog $link")
                    dialog.cancel()
                } else {
                    Toast.makeText(this, "Link is not correct", Toast.LENGTH_LONG).show()
                }
            }
            dialogView.findViewById<TextView>(R.id.insta_dialog_cancel).setOnClickListener {
                dialog.cancel()
            }
            dialog.show()
//            dialogBoxwithEdittext("Linkedin","Add Profile Link")
        }
        val addMoreProfileButton:ImageView = findViewById(R.id.addMoreProfileButton)
        addMoreProfileButton.setOnClickListener {
            dialogBoxwithEdittext("Others","Add Profile Link")
        }
        val addEmailButton:ImageView = findViewById(R.id.addEmailButton)
        addEmailButton.setOnClickListener {
            dialogBoxwithEdittext("Email","Enter Email address")
        }
        val addAddressButton:ImageView = findViewById(R.id.addAddressButton)
        addAddressButton.setOnClickListener {
            dialogBoxwithEdittext("Address","Enter address")
        }
        val addPhoneButton:ImageView = findViewById(R.id.addPhoneButton)
        addPhoneButton.setOnClickListener {
            dialogBoxwithEdittext("Phone Number","Enter Phone Number")
        }
        val addWebsiteButton:ImageView = findViewById(R.id.addWebsiteButton)
        addWebsiteButton.setOnClickListener {
            dialogBoxwithEdittext("Website","Enter Website link")
        }

        val publishButton: Button = findViewById(R.id.publishButton)
        publishButton.setOnClickListener {
            val yourLink:String = findViewById<EditText>(R.id.yourLink).text.toString()
            myRef = database.getReference("user/$uid/yourLink")
            myRef.setValue(yourLink)
            val linkTitle:String = findViewById<EditText>(R.id.linkTitle).text.toString()
            myRef = database.getReference("user/$uid/linkTitle")
            myRef.setValue(linkTitle)
            val linkDescription:String = findViewById<EditText>(R.id.linkDescription).text.toString()
            myRef = database.getReference("user/$uid/linkDescription")
            myRef.setValue(linkDescription)
            val intent = Intent(this, previewActivity::class.java)
            startActivity(intent)
        }
        val previewButton:ImageButton = findViewById(R.id.previewButton)
        previewButton.setOnClickListener {
            val intent = Intent(this, previewActivity::class.java)
            startActivity(intent)
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

    fun dialogBoxwithEdittext(title:String, message:String) {
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)

        val edittext = EditText(this)
        alert.setMessage(message)
        alert.setTitle(title)

        alert.setCancelable(false)
        alert.setView(edittext)

        alert.setPositiveButton("Ok"
        ) { dialog, whichButton -> //What ever you want to do with the value
            val temporaryString = edittext.text.toString()
            myRef = database.getReference("user/$uid/${title.lowercase()}")
            myRef.setValue(temporaryString)
        }

        alert.setNegativeButton("Cancel"
        ) { dialog, whichButton ->
            // what ever you want to do with No option.
        }

        alert.show()
    }
}