package it.polito.interdisciplinaryProjects2021.smartpresence.presenceDetection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import it.polito.interdisciplinaryProjects2021.smartpresence.R

class PresenceDetectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_presence_detection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val blurView = view.findViewById<BlurView>(R.id.blurView)
        blurBackground(blurView)

        val wifiCheckingGuidanceCard = view.findViewById<CardView>(R.id.wifiCheckingGuidanceCard)
        val positioningCheckingGuidanceCard = view.findViewById<CardView>(R.id.positioningCheckingGuidanceCard)
        val manualCheckingGuidanceCard = view.findViewById<CardView>(R.id.manualCheckingGuidanceCard)

        val wifiCheckingGuidance = view.findViewById<ScrollView>(R.id.wifiCheckingGuidance)
        val positioningCheckingGuidance = view.findViewById<ScrollView>(R.id.positioningCheckingGuidance)
        val manualCheckingGuidance = view.findViewById<ScrollView>(R.id.manualCheckingGuidance)

        val goToWifiChecking = view.findViewById<CardView>(R.id.wifiChecking)
        val goToPositioningChecking = view.findViewById<CardView>(R.id.positioningChecking)
        val goToManualChecking = view.findViewById<CardView>(R.id.manualChecking)

        blurView.setOnClickListener {
            TransitionManager.beginDelayedTransition(wifiCheckingGuidanceCard, AutoTransition())
            TransitionManager.beginDelayedTransition(positioningCheckingGuidanceCard, AutoTransition())
            TransitionManager.beginDelayedTransition(manualCheckingGuidanceCard, AutoTransition())
            it.visibility = View.GONE
            wifiCheckingGuidance.visibility = View.GONE
            positioningCheckingGuidance.visibility = View.GONE
            manualCheckingGuidance.visibility = View.GONE
        }

        goToWifiChecking.setOnClickListener {
            findNavController().navigate(R.id.wifiCheckingFragment)
        }
        goToWifiChecking.setOnLongClickListener {
            TransitionManager.beginDelayedTransition(wifiCheckingGuidanceCard, AutoTransition())
            blurView.visibility = View.VISIBLE
            wifiCheckingGuidance.visibility = View.VISIBLE
            true
        }

        goToPositioningChecking.setOnClickListener {
            findNavController().navigate(R.id.positioningCheckingFragment)
        }
        goToPositioningChecking.setOnLongClickListener {
            TransitionManager.beginDelayedTransition(positioningCheckingGuidanceCard, AutoTransition())
            blurView.visibility = View.VISIBLE
            positioningCheckingGuidance.visibility = View.VISIBLE
            true
        }

        goToManualChecking.setOnClickListener {
            findNavController().navigate(R.id.manualCheckingFragment)
        }
        goToManualChecking.setOnLongClickListener {
            TransitionManager.beginDelayedTransition(manualCheckingGuidanceCard, AutoTransition())
            blurView.visibility = View.VISIBLE
            manualCheckingGuidance.visibility = View.VISIBLE
            true
        }
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