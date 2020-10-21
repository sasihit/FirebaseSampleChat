package com.example.firebasechat.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.firebasechat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class GroupChatActivity : AppCompatActivity() {
    private lateinit var mToolbar: Toolbar
    private lateinit var mImageButton: ImageButton
    private lateinit var mEditText: EditText
    private lateinit var mScrollView: ScrollView
    private lateinit var mTextView: TextView
    private lateinit var mCurrentGroupName: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCurrentUserId: String
    private lateinit var mCurrentUserName: String
    private lateinit var mCurrentDate: String
    private lateinit var mCurrentTime: String
    private lateinit var mUserReference: DatabaseReference
    private lateinit var mGroupNameReference: DatabaseReference
    private lateinit var mGroupMessageKeyRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)
        mCurrentGroupName = intent.extras?.get("groupName").toString()

        mAuth = FirebaseAuth.getInstance()
        mCurrentUserId = mAuth.currentUser?.uid.toString()
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users")
        mGroupNameReference =
            FirebaseDatabase.getInstance().getReference().child("Groups").child(mCurrentGroupName)
        initializeFields()
        getUserInfo()

        mImageButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                sendMessageInfoDatabase()
                mEditText.setText("");

                mScrollView.fullScroll(ScrollView.FOCUS_DOWN)
4


            }
        })

    }

    override fun onStart() {
        super.onStart()
        mGroupNameReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.exists()) {
                    DisplayMessages(dataSnapshot)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.exists()) {
                    DisplayMessages(dataSnapshot)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }


    private fun initializeFields() {
        mToolbar = findViewById(R.id.group_chat_bar)
        setSupportActionBar(mToolbar);
        supportActionBar?.title = mCurrentGroupName
        mImageButton = findViewById(R.id.send_message_button)
        mEditText = findViewById(R.id.input_group_msg)
        mTextView = findViewById(R.id.group_txt_display)
        mScrollView = findViewById(R.id.group_scroll)
    }

    private fun getUserInfo() {
        mUserReference.child(mCurrentUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    mCurrentUserName = snapshot.child("name").value.toString()
                    Log.e("mCurrentUserName", "mCurrentUserName" + mCurrentUserName)
                }
            }

            override fun onCancelled(error: DatabaseError) {


            }


        })

    }

    private fun sendMessageInfoDatabase() {
        val message = mEditText.text.toString()
        val messagekEY = mGroupNameReference.push().key
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(
                this@GroupChatActivity,
                "Please write message first...",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val calForDate: Calendar = Calendar.getInstance()
            val currentDateFormat = SimpleDateFormat("MMM dd, yyyy")
            mCurrentDate = currentDateFormat.format(calForDate.getTime())

            val calForTime: Calendar = Calendar.getInstance()
            val currentTimeFormat = SimpleDateFormat("hh:mm a")
            mCurrentTime = currentTimeFormat.format(calForTime.getTime())

            val groupMessageKey: HashMap<String, Any> = HashMap()
            mGroupNameReference.updateChildren(groupMessageKey)

            mGroupMessageKeyRef = mGroupNameReference.child(messagekEY.toString())

            val messageInfoMap: HashMap<String, Any> = HashMap()
            messageInfoMap["name"] = mCurrentUserName
            Log.e("mCurrentUserName", "mCurrentUserName" + mCurrentUserName)

            messageInfoMap["message"] = message
            messageInfoMap["date"] = mCurrentDate
            messageInfoMap["time"] = mCurrentTime
            mGroupMessageKeyRef.updateChildren(messageInfoMap)


        }

    }

    private fun DisplayMessages(dataSnapshot: DataSnapshot) {

        val iterator: Iterator<*> = dataSnapshot.children.iterator()
        while (iterator.hasNext()) {
            val chatDate =
                (iterator.next() as DataSnapshot).value as String?
            val chatMessage =
                (iterator.next() as DataSnapshot).value as String?
            val chatName =
                (iterator.next() as DataSnapshot).value as String?
            val chatTime =
                (iterator.next() as DataSnapshot).value as String?
            mTextView.append(chatName + " :\n" + chatMessage + "\n" + chatTime + "     " + chatDate + "\n\n\n")
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN)


        }
    }

}