package it.polito.interdisciplinaryProjects2021.smartpresence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class PresenceDetectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_presence_detection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goToWifiChecking = view.findViewById<CardView>(R.id.wifiChecking)
        goToWifiChecking.setOnClickListener {
            findNavController().navigate(R.id.wifiCheckingFragment)
        }

        val goToPositioningChecking = view.findViewById<CardView>(R.id.positioningChecking)
        goToPositioningChecking.setOnClickListener {
            findNavController().navigate(R.id.positioningCheckingFragment)
        }

        val goToManualChecking = view.findViewById<CardView>(R.id.manualChecking)
        goToManualChecking.setOnClickListener {
            findNavController().navigate(R.id.manualCheckingFragment)
        }
    }
}