package com.example.aboutme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class EnterPhoneNumber : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    val TAG = "EnterPhoneNumber"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_phone_number)

        var storedVerificationId = ""
        var resendToken: PhoneAuthProvider.ForceResendingToken


        // Initialize Firebase Aut0h
        auth = FirebaseAuth.getInstance()

        // callbacks for Phone uth vie Otp
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(applicationContext, "Incorrect Phone Number", Toast.LENGTH_LONG).show()

                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(applicationContext, "Server Error", Toast.LENGTH_LONG).show()
                }

                // Show a message and update the UI

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

//                otpView.requestFocus();

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
                val intent = Intent(this@EnterPhoneNumber, LoginActivity::class.java).apply() {
                    putExtra(EXTRA_MESSAGE, verificationId)
                }
                startActivity(intent)
            }
        }

        val button_enter_phone:Button = findViewById(R.id.button_enter_phone)
        button_enter_phone.setOnClickListener {
            val phoneNumber: String = findViewById<EditText>(R.id.country_code).text.toString() + findViewById<EditText>(R.id.phone_number).text.toString()

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)

        }
    }
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    afterLogIn(user!!)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(applicationContext, "Invalid OTP", Toast.LENGTH_LONG).show()

                    }
                    // Update UI
                }
            }
    }
    fun afterLogIn(currentUser: FirebaseUser) {
        Log.d(TAG, "afterLogIn() called with: currentUser = $currentUser")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}