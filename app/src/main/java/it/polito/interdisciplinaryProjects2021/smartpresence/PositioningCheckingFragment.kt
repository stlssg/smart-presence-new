package it.polito.interdisciplinaryProjects2021.smartpresence

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaActionSound
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class PositioningCheckingFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_positioning_checking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)

        val positioningStart = view.findViewById<Button>(R.id.positioningStart)
        val positioningStop = view.findViewById<Button>(R.id.positioningStop)
        val positioningRestart = view.findViewById<Button>(R.id.positioningRestart)
        val positioningShowInfo = view.findViewById<Button>(R.id.positioningShowInfo)

        val wifiCheckingStatus = sharedPreferences.getString("positioningCheckingStatus", "false")
        if (wifiCheckingStatus.toBoolean()) {
            positioningStart.isVisible = false
            positioningRestart.isVisible = true
            positioningStop.isEnabled = true
        } else {
            positioningStart.isVisible = true
            positioningRestart.isVisible = false
            positioningStop.isEnabled = false
        }

        val sound = MediaActionSound()

        positioningStart.setOnClickListener {
            positioningStart.isVisible = false
            positioningRestart.isVisible = true
            positioningStop.isEnabled = true
            Toast.makeText(requireContext(), getString(R.string.startServiceMessage), Toast.LENGTH_SHORT).show()
            with(sharedPreferences.edit()) {
                putString("positioningCheckingStatus", "true")
                apply()
            }

            val notificationOnOffCondition = sharedPreferences.getString("notificationOnOffCondition", "true").toBoolean()
            if (notificationOnOffCondition) {
                Firebase.messaging.subscribeToTopic("RemindingManuallyRestartService")
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Toast.makeText(requireContext(), getString(R.string.subscribeNotSuccess), Toast.LENGTH_LONG).show()
                        }
                    }

                val frequentNotificationOnOffCondition = sharedPreferences.getString("frequentNotificationOnOffCondition", "false").toBoolean()
                if (frequentNotificationOnOffCondition) {
                    Firebase.messaging.subscribeToTopic("RemindingManuallyRestartServiceAdditionalMorning")
                    Firebase.messaging.subscribeToTopic("RemindingManuallyRestartServiceAdditionalEvening")
                }
            }

            sound.play(MediaActionSound.START_VIDEO_RECORDING)
        }

        positioningStop.setOnClickListener {
            positioningStart.isVisible = true
            positioningRestart.isVisible = false
            positioningStop.isEnabled = false
            Toast.makeText(requireContext(), getString(R.string.stopServiceMessage), Toast.LENGTH_SHORT).show()
            with(sharedPreferences.edit()) {
                putString("positioningCheckingStatus", "false")
                apply()
            }

            Firebase.messaging.unsubscribeFromTopic("RemindingManuallyRestartService")

            sound.play(MediaActionSound.STOP_VIDEO_RECORDING)
        }

        positioningRestart.setOnClickListener {
            sound.play(MediaActionSound.START_VIDEO_RECORDING)
            Toast.makeText(requireContext(), getString(R.string.restartServiceMessage), Toast.LENGTH_SHORT).show()
        }

        positioningShowInfo.setOnClickListener {
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
                findNavController().navigate(R.id.positioningConfigurationFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}