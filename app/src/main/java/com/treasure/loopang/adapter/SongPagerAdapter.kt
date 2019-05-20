package com.treasure.loopang.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.treasure.loopang.RecordFragment
import com.treasure.loopang.SongManageFragment

class SongPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = 2

    override fun getItem(p0: Int): Fragment? {
        when (p0) {
            0 -> return RecordFragment()
            1 -> return SongManageFragment()
        }
        return null
    }

}