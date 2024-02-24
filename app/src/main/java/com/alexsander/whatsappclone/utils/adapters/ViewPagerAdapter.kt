/*
 * Copyright (c) 2024. Created by Alexsander Fernandes at 2/22. All rights reserved.
 * GitHub: https://github.com/alexsanderfer/
 * Portfolio: https://alexsanderfer.netlify.app/
 */

package com.alexsander.whatsappclone.utils.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alexsander.whatsappclone.fragments.ChatsFragment
import com.alexsander.whatsappclone.fragments.ContactsFragment

class ViewPagerAdapter(private val tabs: List<String>, fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return tabs.size
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            1 -> return ContactsFragment()
        }
        return ChatsFragment()
    }
}