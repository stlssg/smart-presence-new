package it.polito.interdisciplinaryProjects2021.smartpresence

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class ProfessionalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_professional, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myViewPager = view.findViewById<ViewPager>(R.id.myViewPager)
        myViewPager.adapter = PageAdapter(childFragmentManager, getString(R.string.fragment_name_graph), getString(R.string.proActionTabName))
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.setupWithViewPager(myViewPager)
        tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_baseline_bar_chart_24)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_baseline_lightbulb_24)
    }

}

class PageAdapter(fm: FragmentManager, private val tabName_1: String, private val tabName_2: String) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                GraphFragment()
            }
            1 -> {
                ProActionFragment()
            }
            else -> {
                GraphFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> { return tabName_1 }
            1 -> { return tabName_2 }
        }
        return super.getPageTitle(position)
    }

    override fun getCount(): Int {
        return 2
    }

}