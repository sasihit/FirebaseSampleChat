package com.example.firebasechat.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasechat.MainActivity
import com.example.firebasechat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegisterActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var registerTextView: TextView
    private lateinit var userEmail: EditText
    private lateinit var userPassword: EditText
    private lateinit var registerButton: Button

    private lateinit var mAuth: FirebaseAuth
    private lateinit var loadingBar: ProgressDialog
    private lateinit var mCurrentUser: FirebaseUser

    private lateinit var mDatabaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        mAuth = FirebaseAuth.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().getReference()

        initializeFields()
        clickListener()
    }

    private fun clickListener() {
        registerTextView.setOnClickListener(this)
        registerButton.setOnClickListener(this)

    }

    private fun initializeFields() {
        registerTextView = findViewById(R.id.already_have_account_link)
        userEmail = findViewById(R.id.register_email)
        userPassword = findViewById(R.id.register_password)
        registerButton = findViewById(R.id.register_button);
        loadingBar = ProgressDialog(this);


    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.already_have_account_link -> {
                sendToLoginActivity()
            }
            R.id.register_button -> {
                createNewAccount()
            }
        }
    }

    private fun createNewAccount() {
        var email: String = userEmail.text.toString()
        var password: String = userPassword.text.toString()
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Sign In");
            loadingBar.setMessage("Please wait....");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful()) {
                        //sendToLoginActivity()
                        val currentUserID = mAuth.currentUser!!.uid
                        mDatabaseReference.child("Users").child(currentUserID).setValue("")
                        sendUserToMainActivity()
                        Toast.makeText(
                            this@RegisterActivity,
                            "Account Created Successfully...",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadingBar.dismiss()
                    } else {
                        val message: String = task.getException().toString()
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error : $message",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadingBar.dismiss()
                    }
                }
        }
    }


    private fun sendToLoginActivity() {
        val loginIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    private fun sendUserToMainActivity() {
        val mainIntent = Intent(this@RegisterActivity, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()
    }


}