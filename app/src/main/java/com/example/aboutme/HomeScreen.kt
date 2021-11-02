package com.example.aboutme

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class HomeScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        val edittextUid:EditText = findViewById(R.id.edittextUid)
        val uid:String = edittextUid.text.toString()
        val editProfile:Button = findViewById(R.id.editProfile)
        editProfile.setOnClickListener {
            val intent:Intent = Intent(this,previewActivity::class.java)
            startActivity(intent)
        }
        val button:Button = findViewById(R.id.Button)
        button.setOnClickListener {
            val intent:Intent = Intent(this, previewActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, uid)
            }
            startActivity(intent)
        }
    }
}