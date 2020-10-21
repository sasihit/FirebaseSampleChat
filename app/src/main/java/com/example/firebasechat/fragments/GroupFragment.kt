package com.example.firebasechat.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.firebasechat.R
import com.example.firebasechat.activities.GroupChatActivity
import com.google.firebase.database.*

class GroupFragment : Fragment() {
    private var groupFragmentView: View? = null
    private var listView: ListView? = null
    private var arrayAdapter: ArrayAdapter<String?>? = null
    private val listOfGroups =
        ArrayList<String?>()
    private var GroupRef: DatabaseReference? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        groupFragmentView = inflater.inflate(R.layout.fragment_group, container, false);

        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups")
        initializeFields()
        retrieveAndDisplayGroups()


        listView!!.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, position, id ->
                val currentGroupName =
                    adapterView.getItemAtPosition(position).toString()
                val groupChatIntent = Intent(getContext(), GroupChatActivity::class.java)
                groupChatIntent.putExtra("groupName", currentGroupName)
                context?.startActivity(groupChatIntent)
            }
        return groupFragmentView
    }

    private fun initializeFields() {
        listView =
            groupFragmentView!!.findViewById<View>(R.id.list_view) as ListView
        arrayAdapter = this.context?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_list_item_1,
                listOfGroups
            )
        }
        listView!!.adapter = arrayAdapter
    }

    private fun retrieveAndDisplayGroups() {
        GroupRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val set: MutableSet<String?> =
                    HashSet()
                val iterator: Iterator<*> = dataSnapshot.children.iterator()
                while (iterator.hasNext()) {
                    set.add((iterator.next() as DataSnapshot).key)
                }
                listOfGroups.clear()
                listOfGroups.addAll(set)
                arrayAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}