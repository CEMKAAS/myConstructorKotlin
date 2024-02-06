package com.zaroslikov.myconstruction.project

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MenuAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(FragmentActivity()) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> ArhiveProjectFragment()
            else -> HomeProjectFragment()
        }
    }
}