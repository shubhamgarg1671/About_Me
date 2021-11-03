package com.example.aboutme

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    var imageUploaded:Boolean = false
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

        val socialLinksHeading:TextView = findViewById(R.id.socialLinksHeading)
        socialLinksHeading.run {
            bringToFront()
        }


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
                        myRef = database.getReference("user/$uid/facebook")
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
                    myRef = database.getReference("user/$uid/instagram")
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
            val dialogView: View = factory.inflate(R.layout.linkedin_dialog_box, null)
            val dialogImageCardLinkedin:CardView = dialogView.findViewById(R.id.dialogImageCardLinkedin)
            dialogImageCardLinkedin.bringToFront()
            val dialog = AlertDialog.Builder(this).create()
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

            dialog.setView(dialogView)
            dialogView.findViewById<Button>(R.id.dialog_linkedin).setOnClickListener{
                val instalink:EditText = dialogView.findViewById<EditText>(R.id.dialog_lkd_link)
                val link:String = instalink.text.toString()
                if (Patterns.WEB_URL.matcher(link).matches()) {
                    myRef = database.getReference("user/$uid/linkedin")
                    myRef.setValue(link)
                    Log.d(TAG, "onDialog $link")
                    dialog.cancel()
                } else {
                    Toast.makeText(this, "Link is not correct", Toast.LENGTH_LONG).show()
                }
            }
            dialogView.findViewById<TextView>(R.id.linkedin_dialog_cancel).setOnClickListener {
                dialog.cancel()
            }
            dialog.show()
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

        val addPaytm:ImageButton = findViewById(R.id.addPaytm)
        addPaytm.setOnClickListener {
            dialogBoxwithEdittext("Paytm","Enter paytm link")
        }
        val addPhonePe:ImageButton = findViewById(R.id.addPhonePe)
        addPhonePe.setOnClickListener {
            dialogBoxwithEdittext("Paytm","Enter paytm link")
        }
        val addMobiwik:ImageButton = findViewById(R.id.addMobiwik)
        addMobiwik.setOnClickListener {
            dialogBoxwithEdittext("Mobiwik", "Enter Mobiwik")
        }

        val storageRef = storage.reference
        val publishButton: Button = findViewById(R.id.publishButton)
        publishButton.setOnClickListener {

            if (imageUploaded) {
                addProfileImage.isDrawingCacheEnabled = true
                addProfileImage.buildDrawingCache()
                val bitmap: Bitmap = (addProfileImage.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                // Points to "images"

                val imagesRef = storageRef.child("image/${auth.uid}/profilePicture")
                val uploadTask = imagesRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    Log.e(TAG, "onCreate() called $it")
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // ...
                    Log.d(TAG, "onCreate() called with: taskSnapshot = $taskSnapshot")
                }
            }

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
        val add_to_bio:Button = findViewById(R.id.add_to_bio)
        add_to_bio.setOnClickListener {
            val intent:Intent = Intent(this,addToBio::class.java)
            startActivity(intent)
        }
        val share_button:Button = findViewById(R.id.share_button)
        share_button.setOnClickListener {
            copyToClipBoard(uid)
//            val share = Intent.createChooser(Intent().apply {
//                action = Intent.ACTION_SEND
//                putExtra(Intent.EXTRA_TEXT, "https://developer.android.com/training/sharing/")
//
//                // (Optional) Here we're setting the title of the content
//                putExtra(Intent.EXTRA_TITLE, "Introducing content previews")
//
//                // (Optional) Here we're passing a content URI to an image to be displayed
////                data = contentUri
//                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//            }, null)
//            startActivity(share)
            Toast.makeText(this, "UID copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }
    private fun copyToClipBoard(str: String) {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(android.R.attr.label.toString(), str)
        clipboard.setPrimaryClip(clip)
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
                    imageUploaded = true
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