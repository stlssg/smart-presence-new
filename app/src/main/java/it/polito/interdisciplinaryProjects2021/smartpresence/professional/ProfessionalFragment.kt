package it.polito.interdisciplinaryProjects2021.smartpresence.professional

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.tabs.TabLayout
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import it.polito.interdisciplinaryProjects2021.smartpresence.R

class ProfessionalFragment : Fragment() {

    private lateinit var blurView: BlurView
    private lateinit var grant_access_layout: LinearLayout
    private lateinit var building_list_card: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_professional, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        blurView = view.findViewById<BlurView>(R.id.proFragmentBlurView)
        blurBackground(blurView)
        blurView.setOnClickListener{
            blurView.visibility = View.GONE
            grant_access_layout.visibility = View.GONE
            building_list_card.visibility = View.GONE
        }

        grant_access_layout = view.findViewById<LinearLayout>(R.id.grant_access_layout)
        building_list_card = view.findViewById<LinearLayout>(R.id.building_list_card)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.professional_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_access_grant -> {
                if (blurView.visibility == View.VISIBLE) {
                    blurView.visibility = View.GONE
                    grant_access_layout.visibility = View.GONE
                    building_list_card.visibility = View.GONE
                } else {
                    blurView.visibility = View.VISIBLE
                    grant_access_layout.visibility = View.VISIBLE
                }

                return true
            }
            R.id.action_list_building -> {
                if (blurView.visibility == View.VISIBLE) {
                    blurView.visibility = View.GONE
                    building_list_card.visibility = View.GONE
                    grant_access_layout.visibility = View.GONE
                } else {
                    blurView.visibility = View.VISIBLE
                    building_list_card.visibility = View.VISIBLE
                }

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun blurBackground(blurView: BlurView) {
        val decorView = activity?.window?.decorView
        val rootView = decorView?.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = decorView?.background

        blurView.setupWith(rootView!!)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(requireContext()))
            .setBlurRadius(10f)
            .setBlurAutoUpdate(true)
            .setHasFixedTransformationMatrix(true)
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