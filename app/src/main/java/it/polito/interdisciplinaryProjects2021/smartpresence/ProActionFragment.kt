package it.polito.interdisciplinaryProjects2021.smartpresence

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class ProActionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pro_action, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            progressBarWidth = 5f // in DP
            backgroundProgressBarWidth = 8f // in DP

            // Other
            roundBorder = true
            startAngle = 0f
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT
        }
    }

}