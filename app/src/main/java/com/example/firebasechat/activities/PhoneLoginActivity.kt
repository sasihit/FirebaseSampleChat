package com.example.firebasechat.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasechat.MainActivity
import com.example.firebasechat.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

class PhoneLoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var sendVerificationCodeButton: Button
    private lateinit var verifyButton: Button
    private lateinit var mInputPhoneNumber: EditText
    private lateinit var mInputVerificationCode: EditText

    private  var callbacks: OnVerificationStateChangedCallbacks?=null
    private lateinit var storedVerificationId: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private lateinit var mAuth: FirebaseAuth

    private lateinit var verificationId: String
    private lateinit var loadingBar: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)
        mAuth = FirebaseAuth.getInstance()
        initializeFields()
        onClickListener()
    }

    private fun initializeFields() {
        sendVerificationCodeButton = findViewById(R.id.send_ver_code_button)
        verifyButton = findViewById(R.id.verify_button)
        mInputPhoneNumber = findViewById(R.id.phone_number_input)
        mInputVerificationCode = findViewById(R.id.verification_code_input)
        loadingBar = ProgressDialog(this)


    }

    private fun onClickListener() {
        sendVerificationCodeButton.setOnClickListener(this)
        verifyButton.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.send_ver_code_button -> {

                val phoneNumber = mInputPhoneNumber.text.toString()
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(
                        this@PhoneLoginActivity,
                        "please enter your phone number first",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    loadingBar.setTitle("Phone verification")
                    loadingBar.setMessage("Please wait, while we are authenticating your phone")
                    loadingBar.setCanceledOnTouchOutside(false)
                    loadingBar.show()
                    callbacks?.let {
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber, // Phone number to verify
                            60, // Timeout duration
                            TimeUnit.SECONDS, // Unit of timeout
                            this, // Activity (for callback binding)
                            it
                        )
                    } // OnVerificationStateChangedCallbacks

                }
            }

            R.id.verify_button -> {
                sendVerificationCodeButton.visibility = View.INVISIBLE
                mInputPhoneNumber.visibility = View.INVISIBLE

                val verificationCode = mInputVerificationCode.text.toString()

                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(
                        this@PhoneLoginActivity,
                        "Please write code first",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    loadingBar.setTitle("Code verification")
                    loadingBar.setMessage("Please wait, while we are verifying")
                    loadingBar.setCanceledOnTouchOutside(false)
                    loadingBar.show()
                    val credential =
                        PhoneAuthProvider.getCredential(verificationId, verificationCode)
                    signInWithPhoneAuthCredential(credential)

                }


            }
        }
        callbacks = object : OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(p0)


            }

            override fun onVerificationFailed(p0: FirebaseException) {
                loadingBar.dismiss()
                Toast.makeText(
                    this@PhoneLoginActivity,
                    "invalid phone number,please enter the correct phone number with your country code",
                    Toast.LENGTH_SHORT
                ).show()
                sendVerificationCodeButton.visibility = View.VISIBLE
                mInputPhoneNumber.visibility = View.VISIBLE
                verifyButton.visibility = View.INVISIBLE
                mInputVerificationCode.visibility = View.INVISIBLE
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                loadingBar.dismiss()


                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
                Toast.makeText(
                    this@PhoneLoginActivity,
                    "code has been sent, please enter it",
                    Toast.LENGTH_SHORT
                ).show()
                sendVerificationCodeButton.visibility = View.INVISIBLE
                mInputPhoneNumber.visibility = View.INVISIBLE
                verifyButton.visibility = View.VISIBLE
                mInputVerificationCode.visibility = View.VISIBLE

            }

        }


    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    loadingBar.dismiss()
                    Toast.makeText(
                        this@PhoneLoginActivity,
                        "Congratulations, you're logged in successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    sendUserToMainActivity()


                } else {
                    val message = task.exception.toString()
                    Toast.makeText(
                        this@PhoneLoginActivity,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun sendUserToMainActivity() {
        val mainIntent = Intent(this@PhoneLoginActivity, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()


    }
}