package com.zaroslikov.myconstruction.project

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MenuAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> ArhiveProjectFragment()
            else -> HomeProjectFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}