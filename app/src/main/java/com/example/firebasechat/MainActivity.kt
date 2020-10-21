package com.example.firebasechat

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.example.firebasechat.activities.LoginActivity
import com.example.firebasechat.activities.SettingActivity
import com.example.firebasechat.adapters.TabAccessorAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {
    private var mToolbar: Toolbar? = null
    private var myViewPager: ViewPager? = null
    private var myTabLayout: TabLayout? = null
    private var myTabsAccessorAdapter: TabAccessorAdapter? = null
    private var mCurrentUser: FirebaseUser? = null
    private lateinit var mAuth: FirebaseAuth

    private lateinit var mDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mToolbar = findViewById<Toolbar>(R.id.app_toolbar)
        mAuth = FirebaseAuth.getInstance()
        mCurrentUser = mAuth.currentUser
        mDatabaseReference = FirebaseDatabase.getInstance().getReference()
        setSupportActionBar(mToolbar);
        supportActionBar?.title = "WhatsApp"

        myViewPager = findViewById<ViewPager>(R.id.view_tabs_pager)
        myTabLayout = findViewById<TabLayout>(R.id.main_tab)

        val myTabsAccessorAdapter = TabAccessorAdapter(getSupportFragmentManager())
        myViewPager!!.adapter = myTabsAccessorAdapter
        myTabLayout!!.setupWithViewPager(myViewPager);
    }

    override fun onStart() {
        super.onStart()
        if (mCurrentUser == null) {
            sendUserToLoginActivity()
        } else {
            verifyUserExistence()
        }
    }

    private fun sendUserToLoginActivity() {
        val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(loginIntent)
    }

    private fun sendUseToSettingActivity() {
        val settingIntent = Intent(this@MainActivity, SettingActivity::class.java)
        settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(settingIntent)
    }

    private fun requestNewGroup() {
        val builder =
            AlertDialog.Builder(this@MainActivity, R.style.AlertDialog)
        builder.setTitle("Enter Group Name :")

        val groupNameField = EditText(this@MainActivity)
        groupNameField.hint = "sample chat"
        builder.setView(groupNameField)
        builder.setPositiveButton(
            "Create"
        ) { dialogInterface, i ->
            val groupName = groupNameField.text.toString()
            if (TextUtils.isEmpty(groupName)) {
                Toast.makeText(
                    this@MainActivity,
                    "Please write Group Name...",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                createNewGroup(groupName)
            }
        }

        builder.setNegativeButton(
            "Cancel"
        ) { dialogInterface, i -> dialogInterface.cancel() }

        builder.show()
    }

    private fun createNewGroup(groupName: String) {
        mDatabaseReference.child("Groups").child(groupName).setValue("")
            .addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    Toast.makeText(
                        this@MainActivity,
                        "$groupName group is Created Successfully...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun verifyUserExistence() {
        val mCurrentUserID = mAuth.currentUser?.uid
        mDatabaseReference.child("Users").child(mCurrentUserID!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if ((snapshot.child("name").exists())) {
                        Toast.makeText(
                            this@MainActivity,
                            "Welcome..",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        sendUseToSettingActivity()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        if (item.itemId == R.id.menu_logout) {
            mAuth.signOut()
            sendUserToLoginActivity()

        }
        if (item.itemId == R.id.menu_settings) {
            sendUseToSettingActivity()

        }
        if (item.itemId == R.id.main_friends_options) {


        }

        if (item.itemId == R.id.menu_group) {
            requestNewGroup()


        }
        return true
    }
}
