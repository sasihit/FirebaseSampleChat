package com.example.firebasechat.activities

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


class SettingActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mButton: Button
    private lateinit var mUsername: EditText
    private lateinit var mUserStatus: EditText
    private lateinit var mCircularImage: CircleImageView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCurrentUserID: String
    private lateinit var mDatabaseReference: DatabaseReference
    private val galleryPick = 1
    private lateinit var mUserProfileImage: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        mAuth = FirebaseAuth.getInstance()
        mCurrentUserID = mAuth.currentUser!!.uid
        mDatabaseReference = FirebaseDatabase.getInstance().getReference()
        mUserProfileImage = FirebaseStorage.getInstance().reference.child("Profile Images")
        initializeFields()
        mUsername.visibility = View.INVISIBLE;
        onClickListener()
        retrieveUserInfo()

    }


    private fun initializeFields() {
        mButton = findViewById(R.id.update_settings_button)
        mUsername = findViewById(R.id.set_user_name);
        mUserStatus = findViewById(R.id.set_profile_status);
        mCircularImage = findViewById(R.id.set_profile_image);
    }

    private fun onClickListener() {
        mButton.setOnClickListener(this)
        mCircularImage.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.update_settings_button -> {
                updateSettings()
            }
            R.id.set_profile_image -> {
                profileGallery()

            }
        }

    }

    private fun profileGallery() {
        val galleryIntent = Intent()
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, galleryPick)
    }




    private fun updateSettings() {
        val userName = mUsername.text.toString()
        val userStatus = mUserStatus.text.toString()

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(
                this@SettingActivity,
                "Please write user name",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (TextUtils.isEmpty(userStatus)) {
            Toast.makeText(
                this@SettingActivity,
                "Please write status",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val profileMap: HashMap<String, Any> = HashMap()
            profileMap["uid"] = mCurrentUserID
            profileMap["name"] = userName
            profileMap["status"] = userStatus
            mDatabaseReference.child("Users").child(mCurrentUserID).updateChildren(profileMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful()) {
                        sendUserToMainActivity()
                        Toast.makeText(
                            this@SettingActivity,
                            "Profile Updated Successfully...",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val message: String = task.getException().toString()
                        Toast.makeText(
                            this@SettingActivity,
                            "Error: $message",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun retrieveUserInfo() {
        mDatabaseReference.child("Users").child(mCurrentUserID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if ((snapshot.exists()) && (snapshot.hasChild("name") && (snapshot.hasChild("image")))) {
                        val retrieveUexistsserName = snapshot.child("name").getValue().toString()
                        val retrieveUserStatus = snapshot.child("status").getValue().toString()
                        val retrieveUserProfileImg = snapshot.child("image").getValue().toString()
                        mUsername.setText(retrieveUexistsserName)
                        mUserStatus.setText(retrieveUserStatus)


                    } else if ((snapshot.exists()) && (snapshot.hasChild("name"))) {
                        val retrieveUexistsserName = snapshot.child("name").getValue().toString()
                        val retrieveUserStatus = snapshot.child("status").getValue().toString()
                        mUsername.setText(retrieveUexistsserName)
                        mUserStatus.setText(retrieveUserStatus)

                    } else {
                        mUsername.visibility = View.VISIBLE

                        Toast.makeText(
                            this@SettingActivity,
                            "Please set & update your profile information",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


    }

    private fun sendUserToMainActivity() {
        val mainIntent = Intent(this@SettingActivity, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mainIntent)
        finish()
    }
}