package com.example.sl.ui.main.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sl.ui.main.ShopsFragment

class SectionsPagerAdapter(lifecycle: Lifecycle, fm: FragmentManager) :
    FragmentStateAdapter(fm,lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return ShopsFragment.newInstance(position + 1)
    }
}