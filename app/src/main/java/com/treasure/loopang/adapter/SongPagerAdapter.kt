package com.treasure.loopang.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.treasure.loopang.RecordFragment
import com.treasure.loopang.SongManageFragment

class SongPagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = 2

    override fun getItem(p0: Int): androidx.fragment.app.Fragment? {
        when (p0) {
            0 -> return RecordFragment()
            1 -> return SongManageFragment()
        }
        return null
    }
}