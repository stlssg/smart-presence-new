package it.polito.interdisciplinaryProjects2021.smartpresence.presenceDetection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.card.MaterialCardView
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

//        val blurView = view.findViewById<BlurView>(R.id.blurView)
//        blurBackground(blurView)
//
//        val wifiCheckingGuidanceCard = view.findViewById<MaterialCardView>(R.id.wifiCheckingGuidanceCard)
//        val positioningCheckingGuidanceCard = view.findViewById<MaterialCardView>(R.id.positioningCheckingGuidanceCard)
//        val manualCheckingGuidanceCard = view.findViewById<MaterialCardView>(R.id.manualCheckingGuidanceCard)

//        val wifiCheckingGuidance = view.findViewById<LinearLayout>(R.id.wifiCheckingGuidance)
//        val positioningCheckingGuidance = view.findViewById<LinearLayout>(R.id.positioningCheckingGuidance)
//        val manualCheckingGuidance = view.findViewById<LinearLayout>(R.id.manualCheckingGuidance)

        val goToWifiChecking = view.findViewById<MaterialCardView>(R.id.wifiChecking)
        val goToPositioningChecking = view.findViewById<MaterialCardView>(R.id.positioningChecking)
        val goToManualChecking = view.findViewById<MaterialCardView>(R.id.manualChecking)

//        val wifiCheckingGuidanceCancelButton = view.findViewById<ImageButton>(R.id.wifiCheckingGuidanceCancelButton)
//        val positioningCheckingGuidanceCancelButton = view.findViewById<ImageButton>(R.id.positioningCheckingGuidanceCancelButton)
//        val manualCheckingGuidanceCancelButton = view.findViewById<ImageButton>(R.id.manualCheckingGuidanceCancelButton)

//        fun onClickActionsForHiddenViewRemovingOthers() = cancelBlurEffectAndMakeInvisible(
//            wifiCheckingGuidanceCard,
//            positioningCheckingGuidanceCard,
//            manualCheckingGuidanceCard,
//            blurView,
//            wifiCheckingGuidance,
//            positioningCheckingGuidance,
//            manualCheckingGuidance
//        )

//        blurView.setOnClickListener {
//            onClickActionsForHiddenViewRemovingOthers()
//        }
//
//        wifiCheckingGuidanceCancelButton.setOnClickListener {
//            onClickActionsForHiddenViewRemovingOthers()
//        }
//
//        positioningCheckingGuidanceCancelButton.setOnClickListener {
//            onClickActionsForHiddenViewRemovingOthers()
//        }
//
//        manualCheckingGuidanceCancelButton.setOnClickListener {
//            onClickActionsForHiddenViewRemovingOthers()
//        }
//
//        goToWifiChecking.setOnClickListener {
//            TransitionManager.beginDelayedTransition(wifiCheckingGuidanceCard, AutoTransition())
//            blurView.visibility = View.VISIBLE
//            wifiCheckingGuidance.visibility = View.VISIBLE
//        }
//        goToWifiChecking.setOnLongClickListener {
//            goToWifiChecking.isChecked = !goToWifiChecking.isChecked
//            true
//        }
//
//        goToPositioningChecking.setOnClickListener {
//            TransitionManager.beginDelayedTransition(positioningCheckingGuidanceCard, AutoTransition())
//            blurView.visibility = View.VISIBLE
//            positioningCheckingGuidance.visibility = View.VISIBLE
//        }
//        goToPositioningChecking.setOnLongClickListener {
//            goToPositioningChecking.isChecked = !goToPositioningChecking.isChecked
//            true
//        }
//
//        goToManualChecking.setOnClickListener {
//            TransitionManager.beginDelayedTransition(manualCheckingGuidanceCard, AutoTransition())
//            blurView.visibility = View.VISIBLE
//            manualCheckingGuidance.visibility = View.VISIBLE
//        }
//        goToManualChecking.setOnLongClickListener {
//            goToManualChecking.isChecked = !goToManualChecking.isChecked
//            true
//        }

        goToWifiChecking.setOnClickListener {
            findNavController().navigate(R.id.wifiCheckingFragment)
        }
        goToPositioningChecking.setOnClickListener {
            findNavController().navigate(R.id.positioningCheckingFragment)
        }
        goToManualChecking.setOnClickListener {
            findNavController().navigate(R.id.manualCheckingFragment)
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

    private fun cancelBlurEffectAndMakeInvisible(
        card1: MaterialCardView,
        card2: MaterialCardView,
        card3: MaterialCardView,
        blurView: BlurView,
        linearLayout1: LinearLayout,
        linearLayout2: LinearLayout,
        linearLayout3: LinearLayout
    ) {
        TransitionManager.beginDelayedTransition(card1, AutoTransition())
        TransitionManager.beginDelayedTransition(card2, AutoTransition())
        TransitionManager.beginDelayedTransition(card3, AutoTransition())
        blurView.visibility = View.GONE
        linearLayout1.visibility = View.GONE
        linearLayout2.visibility = View.GONE
        linearLayout3.visibility = View.GONE
    }
}