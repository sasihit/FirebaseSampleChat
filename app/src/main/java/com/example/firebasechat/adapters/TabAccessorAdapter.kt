package com.example.firebasechat.adapters

import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.firebasechat.fragments.ChatFragment
import com.example.firebasechat.fragments.ContactFragment
import com.example.firebasechat.fragments.GroupFragment


class TabAccessorAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return ChatFragment()
            }
            1 -> {
                return GroupFragment()
            }
            2 -> {
                return ContactFragment()
            }
            else -> return null!!
        }
    }

    override fun getCount(): Int {
        return 3
    }

    @Nullable
    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Chats"
            1 -> "Groups"
            2 -> "Contacts"
            else -> null
        }
    }
}