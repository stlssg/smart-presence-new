package it.polito.interdisciplinaryProjects2021.smartpresence

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class WifiCheckingFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wifi_checking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
//        val actionBar = (activity as AppCompatActivity).actionBar
//        actionBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)

        setHasOptionsMenu(true)
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)

        val wifiStart = view.findViewById<Button>(R.id.wifiStart)
        val wifiStop = view.findViewById<Button>(R.id.wifiStop)
        val wifiRestart = view.findViewById<Button>(R.id.wifiRestart)
        val wifiShowInfo = view.findViewById<Button>(R.id.wifiShowInfo)

        val wifiCheckingStatus = sharedPreferences.getString("wifiCheckingStatus", "false")
        if (wifiCheckingStatus.toBoolean()) {
            wifiStart.isVisible = false
            wifiRestart.isVisible = true
            wifiStop.isEnabled = true
        } else {
            wifiStart.isVisible = true
            wifiRestart.isVisible = false
            wifiStop.isEnabled = false
        }

        wifiStart.setOnClickListener {
            wifiStart.isVisible = false
            wifiRestart.isVisible = true
            wifiStop.isEnabled = true
            Toast.makeText(requireContext(), getString(R.string.startServiceMessage), Toast.LENGTH_SHORT).show()
            with(sharedPreferences.edit()) {
                putString("wifiCheckingStatus", "true")
                apply()
            }
        }

        wifiStop.setOnClickListener {
            wifiStart.isVisible = true
            wifiRestart.isVisible = false
            wifiStop.isEnabled = false
            Toast.makeText(requireContext(), getString(R.string.stopServiceMessage), Toast.LENGTH_SHORT).show()
            with(sharedPreferences.edit()) {
                putString("wifiCheckingStatus", "false")
                apply()
            }
        }

        wifiRestart.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.restartServiceMessage), Toast.LENGTH_SHORT).show()
        }

        wifiShowInfo.setOnClickListener {
            Snackbar.make(view, getString(R.string.workStatus), Snackbar.LENGTH_LONG)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.configuration_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.configuration_button -> {
                findNavController().navigate(R.id.wifiConfigurationFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
