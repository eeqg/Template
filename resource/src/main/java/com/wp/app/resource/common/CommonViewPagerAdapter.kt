package com.wp.app.resource.common

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Created by wp on 2019/11/20.
 */
class CommonViewPagerAdapter(
    fm: FragmentManager,
    fragments: List<Fragment>
) : FragmentStatePagerAdapter(fm) {

    constructor(
        fm: FragmentManager,
        fragments: List<Fragment>,
        titles: List<String>
    ) : this(fm, fragments) {
        mTitles = titles
    }

    private var mFragments: List<Fragment> = fragments
    private var mTitles: List<String>? = null

    override fun getItem(position: Int): Fragment? {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitles?.get(position) ?: ""
    }
}
