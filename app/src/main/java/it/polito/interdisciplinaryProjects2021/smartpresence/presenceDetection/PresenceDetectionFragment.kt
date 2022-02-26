package it.polito.interdisciplinaryProjects2021.smartpresence.presenceDetection

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaActionSound
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import androidx.work.*
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import it.polito.interdisciplinaryProjects2021.smartpresence.R
import it.polito.interdisciplinaryProjects2021.smartpresence.presenceDetection.positioningBased.GeofenceBroadcastReceiver
import it.polito.interdisciplinaryProjects2021.smartpresence.presenceDetection.positioningBased.MyPeriodicBackgroundPositioningCheckingWork
import it.polito.interdisciplinaryProjects2021.smartpresence.presenceDetection.wifiBased.MyPeriodicWifiCheckingWork
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit

class PresenceDetectionFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var geofencingClient: GeofencingClient

    companion object{
        private const val GEOFENCE_LOCATION_REQUEST_CODE = 999
        private const val GEOFENCE_KEY = "myGeofenceKeyForSmartPresence"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_presence_detection, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "NAME_SHADOWING")
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        val sound = MediaActionSound()

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

        val wifiCheckingStatus = sharedPreferences.getString("wifiCheckingStatus", "false")
        val positioningCheckingStatus = sharedPreferences.getString("positioningCheckingStatus", "false")

        val startButton = view.findViewById<Button>(R.id.startButton)
        val restartButton = view.findViewById<Button>(R.id.restartButton)
        val stopButton = view.findViewById<Button>(R.id.stopButton)
        val showStatusButton = view.findViewById<Button>(R.id.showStatusButton)
        val checkInButton = view.findViewById<Button>(R.id.checkInButton)
        val checkOutButton = view.findViewById<Button>(R.id.checkOutButton)

        if (wifiCheckingStatus.toBoolean() or positioningCheckingStatus.toBoolean()) {
            buttonStatusWhenWorking(startButton, restartButton, stopButton)
        } else {
            buttonStatusWhenNoWorking(startButton, restartButton, stopButton)
        }

        val ssidConfigurationFinished = sharedPreferences.getString("ssidConfigurationFinished", "false").toBoolean()
        val bssidConfigurationFinished = sharedPreferences.getString("bssidConfigurationFinished", "false").toBoolean()
        val addressConfigurationFinished = sharedPreferences.getString("addressConfigurationFinished", "false").toBoolean()
        val maxOccupancyConfigurationFinished = sharedPreferences.getString("maxOccupancyConfigurationFinished", "false").toBoolean()
        val latitudeConfigurationFinished = sharedPreferences.getString("latitudeConfigurationFinished", "false").toBoolean()
        val longitudeConfigurationFinished = sharedPreferences.getString("longitudeConfigurationFinished", "false").toBoolean()

        val energySavingMode = sharedPreferences.getString("energySavingMode", "off")
        val address = sharedPreferences.getString("address", "nothing")
        val maxOccupancy = sharedPreferences.getString("maxOccupancy", "nothing")
        val user = sharedPreferences.getString("keyCurrentAccount", "noEmail")
        val startTimestamp = sharedPreferences.getString("setting_start_time", "07:00")
        val endTimestamp = sharedPreferences.getString("setting_stop_time", "23:00")
        val workingIntervalList = resources.getStringArray(R.array.working_interval)
        val workingIntervalListPosition = sharedPreferences.getString("workingIntervalSpinnerPosition", "0")
        val workingInterval = workingIntervalList[workingIntervalListPosition!!.toInt()].toInt()
        val radiusSpinnerPosition = sharedPreferences.getString("radiusSpinnerPosition", "2")?.toInt()
        val radius = resources.getStringArray(R.array.radius_list)[radiusSpinnerPosition!!].toFloat()

        val latitudeString = sharedPreferences.getString("latitude", "nothing")
        val longitudeString = sharedPreferences.getString("longitude", "nothing")
        val latitude = if (latitudeString == "nothing") {
            0.toDouble()
        } else {
            latitudeString!!.toDouble()
        }
        val longitude = if (longitudeString == "nothing") {
            0.toDouble()
        } else {
            longitudeString!!.toDouble()
        }

        val intent = Intent(activity, GeofenceBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        geofencingClient = LocationServices.getGeofencingClient(activity)

        startButton.setOnClickListener {
            val detectionMethodSelectionTemp = sharedPreferences.getString("detectionMethodSelection", "nothing")
            if (detectionMethodSelectionTemp == "nothing" || detectionMethodSelectionTemp == "manualChecking") {
                Toast.makeText(requireContext(), getString(R.string.no_selection_click_start), Toast.LENGTH_SHORT).show()
            } else {
                if (detectionMethodSelectionTemp == "wifiChecking") {
                    if (ssidConfigurationFinished &&
                        bssidConfigurationFinished &&
                        addressConfigurationFinished &&
                        maxOccupancyConfigurationFinished) {
                        buttonStatusWhenWorking(startButton, restartButton, stopButton)
                        Toast.makeText(requireContext(), getString(R.string.startServiceMessage), Toast.LENGTH_SHORT).show()
                        with(sharedPreferences.edit()) {
                            putString("wifiCheckingStatus", "true")
                            apply()
                        }

                        manageMyPeriodicWorkForWiFiBasedMethod()

                        startNotificationAndSubscribeTopic()
                        sound.play(MediaActionSound.START_VIDEO_RECORDING)
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.configurationNotFinishedMessage), Toast.LENGTH_SHORT).show()
                    }
                } else if (detectionMethodSelectionTemp == "positioningChecking"){
                    if (addressConfigurationFinished &&
                        maxOccupancyConfigurationFinished &&
                        latitudeConfigurationFinished &&
                        longitudeConfigurationFinished) {
                        buttonStatusWhenWorking(startButton, restartButton, stopButton)
                        Toast.makeText(requireContext(), getString(R.string.startServiceMessage), Toast.LENGTH_SHORT).show()
                        with(sharedPreferences.edit()) {
                            putString("positioningCheckingStatus", "true")
                            apply()
                        }
                        startNotificationAndSubscribeTopic()
                        sound.play(MediaActionSound.START_VIDEO_RECORDING)

                        val db = Firebase.firestore

                        val inputBuildingInfo = hashMapOf(
                            "Address" to address,
                            "Maximum_expected_number" to maxOccupancy,
                            "latitude" to latitude.toString(),
                            "longitude" to longitude.toString()
                        )
                        val inputUserInfo = hashMapOf("UserName" to user, "startTime" to startTimestamp, "stopTime" to endTimestamp, "working_interval" to workingInterval)
                        db.collection(address!!).document("Building_Information").set(inputBuildingInfo, SetOptions.merge())
                        db.collection(address).document(user!!).set(inputUserInfo, SetOptions.merge())

                        if (energySavingMode == "on") {
                            createAndStartGeofence(latitude, longitude, radius, pendingIntent)
                        } else if (energySavingMode == "off") {
                            startPeriodicWorkForBackgroundLocationTracking(address, user, startTimestamp, endTimestamp, latitude, longitude, radius, workingInterval)
                        }
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.configurationNotFinishedMessage), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        stopButton.setOnClickListener{
            sound.play(MediaActionSound.STOP_VIDEO_RECORDING)
            Toast.makeText(requireContext(), getString(R.string.stopServiceMessage), Toast.LENGTH_SHORT).show()
            buttonStatusWhenNoWorking(startButton, restartButton, stopButton)
            stopNotificationAndUnsubscribeTopic()
            when (sharedPreferences.getString("detectionMethodSelection", "nothing")) {
                "wifiChecking" -> {
                    with(sharedPreferences.edit()) {
                        putString("wifiCheckingStatus", "false")
                        apply()
                    }
                    stopWork()
                }
                "positioningChecking" -> {
                    with(sharedPreferences.edit()) {
                        putString("positioningCheckingStatus", "false")
                        apply()
                    }

                    if (energySavingMode == "on") {
                        removeAndStopGeofence(pendingIntent)
                    } else if (energySavingMode == "off") {
                        stopWork()
                    }
                }
            }
        }

        restartButton.setOnClickListener{
            Toast.makeText(requireContext(), getString(R.string.restartServiceMessage), Toast.LENGTH_SHORT).show()
            sound.play(MediaActionSound.START_VIDEO_RECORDING)

            when (sharedPreferences.getString("detectionMethodSelection", "nothing")) {
                "wifiChecking" -> {
                    stopWork()
                    manageMyPeriodicWorkForWiFiBasedMethod()
                }
                "positioningChecking" -> {
                    if (energySavingMode == "on") {
                        removeAndStopGeofence(pendingIntent)
                        createAndStartGeofence(latitude, longitude, radius, pendingIntent)
                    } else if (energySavingMode == "off") {
                        stopWork()
                        startPeriodicWorkForBackgroundLocationTracking(address, user, startTimestamp, endTimestamp, latitude, longitude, radius, workingInterval)
                    }
                }
            }
        }

        showStatusButton.setOnClickListener {
            when (sharedPreferences.getString("detectionMethodSelection", "nothing")) {
                "wifiChecking" -> {
                    val workInfo = WorkManager.getInstance(requireContext()).getWorkInfosByTag("wifiCheckingPeriodicWork")
                    val listInfo = workInfo.get()
                    if (listInfo == null || listInfo.size == 0) {
                        Snackbar.make(view, getString(R.string.workStatus) + " " + getString(R.string.workStatusString_NOTHING), Snackbar.LENGTH_LONG)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                            .show()
                    } else {
                        for (info in listInfo) {
                            val workStateString = when (info.state.toString()) {
                                "ENQUEUED" -> {
                                    getString(R.string.workStatusString_ENQUEUED)
                                }
                                "RUNNING" -> {
                                    getString(R.string.workStatusString_RUNNING)
                                }
                                else -> {
                                    getString(R.string.workStatusString_CANCELLED)
                                }
                            }
                            Snackbar.make(view, getString(R.string.workStatus) + " $workStateString", Snackbar.LENGTH_LONG)
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                                .show()
                        }
                    }
                }
                "positioningChecking" -> {
                    val positioningCheckingStatus = sharedPreferences.getString("positioningCheckingStatus", "false").toBoolean()

                    if (energySavingMode == "on") {
                        if (positioningCheckingStatus) {
                            Snackbar.make(view, getString(R.string.workStatus) + " " + getString(R.string.geofenceWorkingStatus), Snackbar.LENGTH_LONG)
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                                .show()
                        } else {
                            Snackbar.make(view, getString(R.string.workStatus) + " " + getString(R.string.geofenceNotWorkingStatus), Snackbar.LENGTH_LONG)
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                                .show()
                        }
                    } else if (energySavingMode == "off") {
                        val workInfo = WorkManager.getInstance(requireContext()).getWorkInfosByTag("backgroundPeriodicLocationUpdate")
                        val listInfo = workInfo.get()
                        if (listInfo == null || listInfo.size == 0) {
                            Snackbar.make(view, getString(R.string.workStatus) + " " + getString(R.string.workStatusString_NOTHING), Snackbar.LENGTH_LONG)
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                                .show()
                        } else {
                            for (info in listInfo) {
                                val workStateString = when (info.state.toString()) {
                                    "ENQUEUED" -> {
                                        getString(R.string.workStatusString_ENQUEUED)
                                    }
                                    "RUNNING" -> {
                                        getString(R.string.workStatusString_RUNNING)
                                    }
                                    else -> {
                                        getString(R.string.workStatusString_CANCELLED)
                                    }
                                }
                                Snackbar.make(view, getString(R.string.workStatus) + " $workStateString", Snackbar.LENGTH_LONG)
                                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                                    .show()
                            }
                        }
                    }
                }
                "manualChecking" -> {
                    Snackbar.make(view, getString(R.string.workStatus) + " " + getString(R.string.show_status_for_manual_method), Snackbar.LENGTH_LONG)
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                        .show()
                }
                else -> {
                    Snackbar.make(view, getString(R.string.workStatus) + " " + getString(R.string.show_status_for_no_selection), Snackbar.LENGTH_LONG)
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                        .show()
                }
            }
        }

        checkInButton.setOnClickListener {
            val detectionMethodSelectionTemp = sharedPreferences.getString("detectionMethodSelection", "nothing")
            if (detectionMethodSelectionTemp != "manualChecking") {
                Toast.makeText(requireContext(), getString(R.string.no_selection_manual_click_button), Toast.LENGTH_SHORT).show()
            } else {
                if (addressConfigurationFinished && maxOccupancyConfigurationFinished) {
                    Toast.makeText(requireContext(), getString(R.string.checkInMessage), Toast.LENGTH_SHORT).show()
                    processingCheck("IN")
                } else {
                    Toast.makeText(requireContext(), getString(R.string.configurationNotFinishedMessage), Toast.LENGTH_SHORT).show()
                }
            }
        }

        checkOutButton.setOnClickListener {
            val detectionMethodSelectionTemp = sharedPreferences.getString("detectionMethodSelection", "nothing")
            if (detectionMethodSelectionTemp != "manualChecking") {
                Toast.makeText(requireContext(), getString(R.string.no_selection_manual_click_button), Toast.LENGTH_SHORT).show()
            } else {
                if (addressConfigurationFinished && maxOccupancyConfigurationFinished) {
                    Toast.makeText(requireContext(), getString(R.string.checkInMessage), Toast.LENGTH_SHORT).show()
                    processingCheck("OUT")
                } else {
                    Toast.makeText(requireContext(), getString(R.string.configurationNotFinishedMessage), Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun manageMyPeriodicWorkForWiFiBasedMethod() {
        val user = sharedPreferences.getString("keyCurrentAccount", "noEmail")
        val address = sharedPreferences.getString("address", "nothing")
        val ssid = sharedPreferences.getString("ssid", "nothing")
        val bssid = sharedPreferences.getString("bssid", "nothing")
        val maxOccupancy = sharedPreferences.getString("maxOccupancy", "nothing")
        val startTimestamp = sharedPreferences.getString("setting_start_time", "07:00")
        val endTimestamp = sharedPreferences.getString("setting_stop_time", "23:00")
        val workingIntervalList = resources.getStringArray(R.array.working_interval)
        val workingIntervalListPosition = sharedPreferences.getString("workingIntervalSpinnerPosition", "0")
        val workingInterval = workingIntervalList[workingIntervalListPosition!!.toInt()].toInt()

        initializationForWiFiBasedMethod(user, address, ssid, bssid, maxOccupancy, startTimestamp, endTimestamp, workingInterval.toString())
        startMyPeriodicWorkForWiFiBasedMethod(address, user, ssid, bssid, startTimestamp, endTimestamp, workingInterval)
    }

    private fun initializationForWiFiBasedMethod(
        user: String?,
        address: String?,
        ssid: String?,
        bssid: String?,
        maxOccupancy: String?,
        start_timestamp: String?,
        end_timestamp: String?,
        working_interval:String
    ) {
        val db = Firebase.firestore
        val inputBuildingInfo = hashMapOf("Address" to address, "SSID" to ssid, "BSSID" to bssid, "Maximum_expected_number" to maxOccupancy)
        val inputUserInfo = hashMapOf("UserName" to user, "startTime" to start_timestamp, "stopTime" to end_timestamp, "working_interval" to working_interval)
        db.collection(address!!).document("Building_Information").set(inputBuildingInfo, SetOptions.merge())
        db.collection(address).document("$user").set(inputUserInfo, SetOptions.merge())
    }

    private fun startMyPeriodicWorkForWiFiBasedMethod(
        address: String?,
        user: String?,
        ssid: String?,
        bssid: String?,
        start_timestamp: String?,
        end_timestamp: String?,
        working_interval: Int
    ) {
        val constraints = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(false)
                .setRequiresDeviceIdle(false)
                .build()
        } else {
            Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(false)
                .build()
        }

        val myRequest = PeriodicWorkRequestBuilder<MyPeriodicWifiCheckingWork>(working_interval.toLong(), TimeUnit.MINUTES)
            .setInputData(
                workDataOf("collection" to address,
                "document" to user,
                "Time_Start" to start_timestamp,
                "Time_End" to end_timestamp,
                "ssid" to ssid,
                "bssid" to bssid)
            )
            .setConstraints(constraints)
            .addTag("wifiCheckingPeriodicWork")
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "myPeriodicWorkForWifiChecking",
            ExistingPeriodicWorkPolicy.KEEP,
            myRequest
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun createAndStartGeofence(
        lat: Double,
        lon: Double,
        radius: Float,
        pendingIntent: PendingIntent
    ) {
        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_KEY)
            .setCircularRegion(lat, lon, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()

        val geofenceRequest = GeofencingRequest.Builder().setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).addGeofence(geofence).build()

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                GEOFENCE_LOCATION_REQUEST_CODE
            )
        } else {
            geofencingClient.addGeofences(geofenceRequest, pendingIntent)
        }
    }

    private fun removeAndStopGeofence(pendingIntent: PendingIntent) {
        geofencingClient.removeGeofences(pendingIntent)
    }

    private fun startPeriodicWorkForBackgroundLocationTracking(
        address: String?,
        user: String?,
        start_timestamp: String?,
        end_timestamp: String?,
        lat: Double,
        lon: Double,
        radius: Float,
        working_interval: Int
    ) {
        val constraints = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .setRequiresDeviceIdle(false)
                .build()
        } else {
            Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiresBatteryNotLow(false)
                .build()
        }

        val myWorker = PeriodicWorkRequestBuilder<MyPeriodicBackgroundPositioningCheckingWork>(working_interval.toLong(), TimeUnit.MINUTES)
            .setInputData(
                workDataOf("collection" to address,
                    "document" to user,
                    "time_Start" to start_timestamp,
                    "time_End" to end_timestamp,
                    "latitude" to lat.toString(),
                    "longitude" to lon.toString(),
                    "radius" to radius.toString())
            )
            .addTag("backgroundPeriodicLocationUpdate")
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "myBackgroundPeriodicLocationUpdate",
            ExistingPeriodicWorkPolicy.REPLACE,
            myWorker
        )

    }

    @Suppress("DEPRECATION")
    private fun stopWork() {
        when (sharedPreferences.getString("detectionMethodSelection", "nothing")) {
            "wifiChecking" -> {
                WorkManager.getInstance().cancelAllWorkByTag("wifiCheckingPeriodicWork")
            }
            "positioningChecking" -> {
                WorkManager.getInstance().cancelAllWorkByTag("backgroundPeriodicLocationUpdate")
            }
        }
    }

    private fun startNotificationAndSubscribeTopic() {
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
    }

    private fun stopNotificationAndUnsubscribeTopic() {
        Firebase.messaging.unsubscribeFromTopic("RemindingManuallyRestartService")
        Firebase.messaging.unsubscribeFromTopic("RemindingManuallyRestartServiceAdditionalMorning")
        Firebase.messaging.unsubscribeFromTopic("RemindingManuallyRestartServiceAdditionalEvening")
    }

    private fun processingCheck(action: String) {
        val db = Firebase.firestore

        val address = sharedPreferences.getString("address", "nothing")
        val maxOccupancy = sharedPreferences.getString("maxOccupancy", "nothing")
        val user = sharedPreferences.getString("keyCurrentAccount", "noEmail")

        val now = DateTime.now()

        val inputBuildingInfo = hashMapOf("Address" to address, "Maximum_expected_number" to maxOccupancy)
        val input = hashMapOf("presence" to action, "timestamp" to now.toString())

        db.collection(address!!).document("Building_Information").set(inputBuildingInfo, SetOptions.merge())
        db.collection(address).document(user!!).set(hashMapOf("UserName" to user), SetOptions.merge())
        db.collection(address)
            .document(user)
            .collection("MANUAL")
            .document(now.toString())
            .set(input, SetOptions.merge())
    }

    private fun buttonStatusWhenWorking(startBtn: Button, restartBtn: Button, stopBtn: Button) {
        startBtn.visibility = View.GONE
        restartBtn.visibility = View.VISIBLE
        stopBtn.isEnabled = true
    }

    private fun buttonStatusWhenNoWorking(startBtn: Button, restartBtn: Button, stopBtn: Button) {
        startBtn.visibility = View.VISIBLE
        restartBtn.visibility = View.GONE
        stopBtn.isEnabled = false
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

        if (sharedPreferences.getString("wifiCheckingStatus", "false").toBoolean() ||
            sharedPreferences.getString("positioningCheckingStatus", "false").toBoolean()) {
            Toast.makeText(requireContext(), getString(R.string.long_click_card_no_stop_service), Toast.LENGTH_SHORT).show()
        } else {
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
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.configuration_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.configuration_button -> {
                when (sharedPreferences.getString("detectionMethodSelection", "nothing")) {
                    "wifiChecking" -> {
                        findNavController().navigate(R.id.wifiConfigurationFragment)
                    }
                    "positioningChecking" -> {
                        findNavController().navigate(R.id.positioningConfigurationFragment)
                    }
                    "manualChecking" -> {
                        findNavController().navigate(R.id.manualConfigurationFragment)
                    }
                    else -> {
                        Toast.makeText(requireContext(), getString(R.string.no_selection_go_conf_message), Toast.LENGTH_SHORT).show()
                    }
                }

                return true
            }
        }
        return super.onOptionsItemSelected(item)
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