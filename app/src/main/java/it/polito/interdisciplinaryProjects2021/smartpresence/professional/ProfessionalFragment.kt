package it.polito.interdisciplinaryProjects2021.smartpresence.professional

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import it.polito.interdisciplinaryProjects2021.smartpresence.R

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
        myViewPager.adapter = PageAdapter(childFragmentManager, getString(R.string.fragment_name_graph), getString(
            R.string.proActionTabName
        ))
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.setupWithViewPager(myViewPager)
        tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_baseline_bar_chart_24)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_baseline_list_24)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            (activity as AppCompatActivity).supportActionBar?.hide()
            tabLayout.visibility = View.GONE
        }
    }

    override fun onDetach() {
        super.onDetach()

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        (activity as AppCompatActivity).supportActionBar?.show()
        view?.findViewById<TabLayout>(R.id.tabLayout)?.visibility = View.VISIBLE
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