package com.example.aboutme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Welcome : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        // Initialize Firebase Aut0h
        auth = FirebaseAuth.getInstance()
        val getStartedButton: Button = findViewById(R.id.getStartedButton);
        getStartedButton.setOnClickListener {
            val intent = Intent(this, EnterPhoneNumber::class.java)
            startActivity(intent)
        }
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            afterLogIn(currentUser)
        }
    }
    fun afterLogIn(currentUser: FirebaseUser) {
        val intent = Intent(this, previewActivity::class.java)
        startActivity(intent)
        finish()
    }
}