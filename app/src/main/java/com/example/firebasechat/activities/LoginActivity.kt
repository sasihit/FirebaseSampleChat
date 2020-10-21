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


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var loginButton: Button
    private lateinit var phoneLoginButton: Button
    private var userEmail: EditText? = null
    private var userPassword: EditText? = null
    private lateinit var needNewAccountLink: TextView
    private lateinit var forgetPasswordLink: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var loadingBar: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()

        initializeFields()
        clickListener()
    }

    private fun clickListener() {
        needNewAccountLink.setOnClickListener(this)
        phoneLoginButton.setOnClickListener(this)
        loginButton.setOnClickListener(this)
    }

    private fun initializeFields() {
        loginButton = findViewById(R.id.login_button)
        phoneLoginButton = findViewById(R.id.phone_login_button);
        needNewAccountLink = findViewById(R.id.need_new_account_link);
        forgetPasswordLink = findViewById(R.id.forget_password_link);
        userEmail = findViewById(R.id.login_email)
        userPassword = findViewById(R.id.login_password)
        loadingBar = ProgressDialog(this);


    }


    private fun sendUserToMainActivity() {
        val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.need_new_account_link -> {
                sendUserToRegisterActivity()
            }
            R.id.login_button -> {

                allowUserToLogin()
            }
            R.id.phone_login_button -> {
                phoneUserToLogin()
            }


        }
    }

    private fun phoneUserToLogin() {
        val phoneLoginIntent = Intent(this@LoginActivity, PhoneLoginActivity::class.java)
        startActivity(phoneLoginIntent)
        finish()
    }

    private fun allowUserToLogin() {

        val email: String = userEmail?.text.toString().trim()
        val password: String = userPassword?.text.toString().trim()


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
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendUserToMainActivity()
                    Toast.makeText(
                        this@LoginActivity,
                        "Logged Successfully...",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadingBar.dismiss()

                } else {
                    val message: String = task.getException().toString()
                    Toast.makeText(
                        this@LoginActivity,
                        "Error : $message",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadingBar.dismiss()
                }

            }


        }
    }

    private fun sendUserToRegisterActivity() {
        val registerIntent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(registerIntent)
    }

}