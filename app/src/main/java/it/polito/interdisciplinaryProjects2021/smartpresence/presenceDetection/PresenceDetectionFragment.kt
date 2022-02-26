package it.polito.interdisciplinaryProjects2021.smartpresence.presenceDetection

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_presence_detection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)
        val detectionMethodSelection = sharedPreferences.getString("detectionMethodSelection", "nothing")

        val blurView = view.findViewById<BlurView>(R.id.blurView)
        blurBackground(blurView)

        val wifiCheckingGuidanceCard = view.findViewById<MaterialCardView>(R.id.wifiCheckingGuidanceCard)
        val positioningCheckingGuidanceCard = view.findViewById<MaterialCardView>(R.id.positioningCheckingGuidanceCard)
        val manualCheckingGuidanceCard = view.findViewById<MaterialCardView>(R.id.manualCheckingGuidanceCard)

        val wifiCheckingGuidance = view.findViewById<LinearLayout>(R.id.wifiCheckingGuidance)
        val positioningCheckingGuidance = view.findViewById<LinearLayout>(R.id.positioningCheckingGuidance)
        val manualCheckingGuidance = view.findViewById<LinearLayout>(R.id.manualCheckingGuidance)

        val wifiChecking = view.findViewById<MaterialCardView>(R.id.wifiChecking)
        val positioningChecking = view.findViewById<MaterialCardView>(R.id.positioningChecking)
        val manualChecking = view.findViewById<MaterialCardView>(R.id.manualChecking)

        when (detectionMethodSelection) {
            "wifiChecking" -> {
                wifiChecking.isChecked = true
            }
            "positioningChecking" -> {
                positioningChecking.isChecked = true
            }
            "manualChecking" -> {
                manualChecking.isChecked = true
            }
            else -> {
                wifiChecking.isChecked = false
                positioningChecking.isChecked = false
                manualChecking.isChecked = false
            }
        }

        val wifiCheckingGuidanceCancelButton = view.findViewById<ImageButton>(R.id.wifiCheckingGuidanceCancelButton)
        val positioningCheckingGuidanceCancelButton = view.findViewById<ImageButton>(R.id.positioningCheckingGuidanceCancelButton)
        val manualCheckingGuidanceCancelButton = view.findViewById<ImageButton>(R.id.manualCheckingGuidanceCancelButton)

        fun onClickActionsForHiddenViewRemovingOthers() = cancelBlurEffectAndMakeInvisible(
            wifiCheckingGuidanceCard,
            positioningCheckingGuidanceCard,
            manualCheckingGuidanceCard,
            blurView,
            wifiCheckingGuidance,
            positioningCheckingGuidance,
            manualCheckingGuidance
        )

        blurView.setOnClickListener { onClickActionsForHiddenViewRemovingOthers() }
        wifiCheckingGuidanceCancelButton.setOnClickListener { onClickActionsForHiddenViewRemovingOthers() }
        positioningCheckingGuidanceCancelButton.setOnClickListener { onClickActionsForHiddenViewRemovingOthers() }
        manualCheckingGuidanceCancelButton.setOnClickListener { onClickActionsForHiddenViewRemovingOthers() }

        wifiChecking.setOnClickListener { showMethodGuidance(wifiCheckingGuidanceCard, blurView, wifiCheckingGuidance) }
        wifiChecking.setOnLongClickListener { longClickToSelectMethod(it as MaterialCardView, "wifiChecking") }

        positioningChecking.setOnClickListener { showMethodGuidance(positioningCheckingGuidanceCard, blurView, positioningCheckingGuidance) }
        positioningChecking.setOnLongClickListener { longClickToSelectMethod(it as MaterialCardView, "positioningChecking") }

        manualChecking.setOnClickListener { showMethodGuidance(manualCheckingGuidanceCard, blurView, manualCheckingGuidance) }
        manualChecking.setOnLongClickListener { longClickToSelectMethod(it as MaterialCardView, "manualChecking") }

        val startButton = view.findViewById<Button>(R.id.startButton)
        val restartButton = view.findViewById<Button>(R.id.restartButton)
        val stopButton = view.findViewById<Button>(R.id.stopButton)
        val showStatusButton = view.findViewById<Button>(R.id.showStatusButton)
        val checkInButton = view.findViewById<Button>(R.id.checkInButton)
        val checkOutButton = view.findViewById<Button>(R.id.checkOutButton)

        startButton.setOnClickListener {
            startButton.visibility = View.GONE
            restartButton.visibility = View.VISIBLE
            stopButton.isEnabled = true
        }

        stopButton.setOnClickListener{
            startButton.visibility = View.VISIBLE
            restartButton.visibility = View.GONE
            stopButton.isEnabled = false
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

    private fun showMethodGuidance(card: MaterialCardView, blurView: BlurView, guidance: LinearLayout) {
        TransitionManager.beginDelayedTransition(card, AutoTransition())
        blurView.visibility = View.VISIBLE
        guidance.visibility = View.VISIBLE
    }

    @Suppress("SameParameterValue")
    private fun longClickToSelectMethod(card: MaterialCardView, methodString: String): Boolean {
        val detectionMethodSelectionTemp = sharedPreferences.getString("detectionMethodSelection", "nothing")

        if (card.isChecked) {
            with(sharedPreferences.edit()) {
                putString( "detectionMethodSelection", "nothing")
                commit()
            }
            card.isChecked = !card.isChecked
        } else if (detectionMethodSelectionTemp != methodString && detectionMethodSelectionTemp != "nothing") {
            Toast.makeText(requireContext(), getString(R.string.already_selected_method_message), Toast.LENGTH_SHORT).show()
        } else {
            with(sharedPreferences.edit()) {
                putString( "detectionMethodSelection", methodString)
                commit()
            }
            card.isChecked = !card.isChecked
        }

        return true
    }
}


//        wifiChecking.setOnClickListener {
//            findNavController().navigate(R.id.wifiCheckingFragment)
//        }
//        positioningChecking.setOnClickListener {
//            findNavController().navigate(R.id.positioningCheckingFragment)
//        }
//        manualChecking.setOnClickListener {
//            findNavController().navigate(R.id.manualCheckingFragment)
//        }