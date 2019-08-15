package com.treasure.loopang.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.treasure.loopang.ui.fragments.RecordFragment
import com.treasure.loopang.ui.fragments.LoopManageFragment

class LoopStationPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
    private val fragmentList: ArrayList<Fragment> = arrayListOf()

    init{
        fragmentList.add(RecordFragment())
        fragmentList.add(LoopManageFragment())
    }
    override fun getCount(): Int = fragmentList.size

    override fun getItem(p0: Int): Fragment? {
        return if(fragmentList.size != 0 && (0 <= p0 && p0 < fragmentList.size ))
            fragmentList[p0]
        else null
    }
}