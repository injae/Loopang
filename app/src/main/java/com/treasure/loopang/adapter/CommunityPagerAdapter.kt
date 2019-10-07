package com.treasure.loopang.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.treasure.loopang.CommunityFeedFragment
import com.treasure.loopang.CommunitySearchFragment
import com.treasure.loopang.CommunityUserPageFragment

class CommunityPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
    private val fragmentList: ArrayList<Fragment> = arrayListOf()

    init{
        fragmentList.add(CommunityFeedFragment())
        fragmentList.add(CommunityUserPageFragment())
        fragmentList.add(CommunitySearchFragment())
    }
    override fun getCount(): Int = fragmentList.size

    override fun getItem(p0: Int): Fragment? {
        return if(fragmentList.size != 0 && (0 <= p0 && p0 < fragmentList.size ))
            fragmentList[p0]
        else null
    }
}
