package com.example.aboutme

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mukesh.OnOtpCompletionListener
import com.mukesh.OtpView
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var signInProgress:ProgressBar
    val TAG = "logInActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val storage = Firebase.storage
        // Create a storage reference from our app
        val storageRef = storage.reference
        // Points to "images"
        val imagesRef = storageRef.child("images")

    }
}