package com.example.aboutme

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream

class LoginActivity : AppCompatActivity() {
    private val PICK_FROM_GALLARY = 2
    lateinit var addProfileImage:ImageView
    lateinit var LoginCompletedButton:Button
    private lateinit var database:FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    lateinit var signInProgress:ProgressBar
    var imageUploaded:Boolean = false
    val TAG = "logInActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val storage = Firebase.storage
        var myref = database.getReference("user/${auth.uid}/profileImage")
        // Create a storage reference from our app
        val storageRef = storage.reference
        auth = FirebaseAuth.getInstance()
        addProfileImage = findViewById(R.id.addProfileImage)
        LoginCompletedButton = findViewById(R.id.LoginCompletedButton)
        LoginCompletedButton.setOnClickListener {
            // Get the data from an ImageView as bytes
//            if (imageUploaded) {
                addProfileImage.isDrawingCacheEnabled = true
                addProfileImage.buildDrawingCache()
                val bitmap:Bitmap = (addProfileImage.drawable as BitmapDrawable).bitmap
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
//            }
            val username:String = findViewById<EditText>(R.id.username_edittext).text.toString()
            myref = database.getReference("user/${auth.uid}/username")
            myref.setValue(username)
            val profileName:String = findViewById<EditText>(R.id.profileName_edittext).text.toString()
            myref = database.getReference("user/${auth.uid}/profileName")
            myref.setValue(profileName)
            val bio:String = findViewById<EditText>(R.id.bio_edittext).text.toString()
            myref = database.getReference("user/${auth.uid}/bio")
            myref.setValue(bio)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
        addProfileImage.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_FROM_GALLARY)
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
}