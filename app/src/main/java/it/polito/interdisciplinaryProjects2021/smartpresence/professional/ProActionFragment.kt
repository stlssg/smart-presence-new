package it.polito.interdisciplinaryProjects2021.smartpresence.professional

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import it.polito.interdisciplinaryProjects2021.smartpresence.R

class ProActionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pro_action, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)

        val address = sharedPreferences.getString("address", "nothing")
        val latitude = sharedPreferences.getString("latitude", "nothing")
        val longitude = sharedPreferences.getString("longitude", "nothing")
        val ssid = sharedPreferences.getString("ssid", "nothing")
        val bssid = sharedPreferences.getString("bssid", "nothing")
        val maxOccupancy = sharedPreferences.getString("maxOccupancy", "nothing")

        val infoProAddress = view.findViewById<TextView>(R.id.infoProAddress)
        val infoProLat = view.findViewById<TextView>(R.id.infoProLat)
        val infoProLon = view.findViewById<TextView>(R.id.infoProLon)
        val infoProSSID = view.findViewById<TextView>(R.id.infoProSSID)
        val infoProBSSID = view.findViewById<TextView>(R.id.infoProBSSID)
        val infoProMax = view.findViewById<TextView>(R.id.infoProMax)

        assignContentForBuildingInfo(address?.replace("_", " "), getString(R.string.configurationAlertAddressName), infoProAddress)
        assignContentForBuildingInfo(latitude, getString(R.string.configurationAlertLatName), infoProLat)
        assignContentForBuildingInfo(longitude, getString(R.string.configurationAlertLonName), infoProLon)
        assignContentForBuildingInfo(ssid, "SSID", infoProSSID)
        assignContentForBuildingInfo(bssid, "BSSID", infoProBSSID)
        assignContentForBuildingInfo(maxOccupancy, getString(R.string.configurationAlertMaxName), infoProMax)

        // need change
        val maxString = if (maxOccupancy == "nothing") {
            getString(R.string.pro_action_info_not_available)
        } else {
            maxOccupancy
        }
        val currentNumberOccupantsTitle = view.findViewById<TextView>(R.id.currentNumberOccupantsTitle)
        currentNumberOccupantsTitle.text =
            getString(R.string.pro_action_info_current_number) +
            " (" +
            getString(R.string.pro_action_info_max_string) +
            ": " +
            maxString +
            "):"

        val currentNumberOccupants = view.findViewById<TextView>(R.id.currentNumberOccupants)
        currentNumberOccupants.text = "2"
        val selectedHourlyOccupancy = view.findViewById<TextView>(R.id.selectedHourlyOccupancy)
        selectedHourlyOccupancy.text = "0.8"
        val allHourlyOccupancy = view.findViewById<TextView>(R.id.allHourlyOccupancy)
        allHourlyOccupancy.text = "0.9"

        val selectedHourlyOccupancyBar = view.findViewById<CircularProgressBar>(R.id.selectedHourlyOccupancyBar)
        setProgressBar(selectedHourlyOccupancyBar, 0.8f)

        val allHourlyOccupancyBar = view.findViewById<CircularProgressBar>(R.id.allHourlyOccupancyBar)
        setProgressBar(allHourlyOccupancyBar, 0.9f)

    }

    private fun setProgressBar(progressBarView: CircularProgressBar, progressInput: Float) {
        progressBarView.apply {
            // Set Progress progress = 2f or with animation
            setProgressWithAnimation(progressInput, 1000) // milli sec => 1s

            // Set Progress Max
            progressMax = 1f

            // Set ProgressBar Color and background ProgressBar Color
            progressBarColor = Color.BLUE
            backgroundProgressBarColor = Color.GRAY

            // Set Width
            progressBarWidth = 6f // in DP
            backgroundProgressBarWidth = 12f // in DP

            // Other
            roundBorder = true
            startAngle = 0f
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT
        }
    }

    @SuppressLint("SetTextI18n")
    private fun assignContentForBuildingInfo(stringInput: String?, additionalString: String, tv: TextView) {
        if (stringInput == "nothing") {
            tv.text = "$additionalString: " + getString(R.string.pro_action_info_not_available)
        } else {
            tv.text = "$additionalString: $stringInput"
        }
    }

}