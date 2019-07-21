package com.treasure.loopang.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.treasure.loopang.RecordFragment
import com.treasure.loopang.LoopManageFragment

class LoopstationPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int = 2

    override fun getItem(p0: Int): Fragment? {
        when (p0) {
            0 -> return RecordFragment()
            1 -> return LoopManageFragment()
        }
        return null
    }
}