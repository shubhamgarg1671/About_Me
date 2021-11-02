package com.example.aboutme

import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class HomeScreen : AppCompatActivity() {
    val TAG = "HomeScreen"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        val edittextUid:EditText = findViewById(R.id.edittextUid)
        val editProfile:Button = findViewById(R.id.editProfile)
        editProfile.setOnClickListener {
            val intent:Intent = Intent(this,previewActivity::class.java)
            startActivity(intent)
        }
        val button:Button = findViewById(R.id.Button)
        button.setOnClickListener {
            val uid:String = edittextUid.text.toString()
            val intent:Intent = Intent(this, previewActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, uid)
//                Log.d(TAG, "uid : $uid")
            }
            startActivity(intent)
        }
    }
}