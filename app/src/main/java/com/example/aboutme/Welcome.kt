package com.example.aboutme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Welcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val getStartedButton: Button = findViewById(R.id.getStartedButton);
        getStartedButton.setOnClickListener {
            val intent = Intent(this, EnterPhoneNumber::class.java)
            startActivity(intent)
        }
    }
}