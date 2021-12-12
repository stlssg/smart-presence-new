package it.polito.interdisciplinaryProjects2021.smartpresence

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import androidx.work.*
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

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

        val wifiConfigurationFinished = sharedPreferences.getString("wifiConfigurationFinished", "false").toBoolean()
        wifiStart.setOnClickListener {
            if (wifiConfigurationFinished) {
                wifiStart.isVisible = false
                wifiRestart.isVisible = true
                wifiStop.isEnabled = true
                Toast.makeText(requireContext(), getString(R.string.startServiceMessage), Toast.LENGTH_SHORT).show()
                with(sharedPreferences.edit()) {
                    putString("wifiCheckingStatus", "true")
                    apply()
                }

                manageMyPeriodicWork()
            } else {
                Toast.makeText(requireContext(), getString(R.string.configurationNotFinishedMessage), Toast.LENGTH_LONG).show()
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

            stopWork()
        }

        wifiRestart.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.restartServiceMessage), Toast.LENGTH_SHORT).show()
            stopWork()
            manageMyPeriodicWork()
        }

        wifiShowInfo.setOnClickListener {
            val workInfo = WorkManager.getInstance(requireContext()).getWorkInfosByTag("wifiCheckingPeriodicWork")
            val listInfo = workInfo.get()
            if (listInfo == null || listInfo.size == 0) {
                Snackbar.make(view, getString(R.string.workStatus) + " " + getString(R.string.workStatusString_NOTHING), Snackbar.LENGTH_LONG)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                    .show()
            } else {
                for (info in listInfo) {
                    val workState = info.state.toString()
                    val workStateString = when (workState) {
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

    private fun manageMyPeriodicWork() {
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)

        val user = sharedPreferences.getString("keyCurrentAccount", "noEmail")
        val address = sharedPreferences.getString("address", "nothing")
        val ssid = sharedPreferences.getString("ssid", "nothing")
        val bssid = sharedPreferences.getString("bssid", "nothing")
        val maxOccupancy = sharedPreferences.getString("maxOccupancy", "nothing")
        val start_timestamp = sharedPreferences.getString("setting_start_time", "07:00")
        val end_timestamp = sharedPreferences.getString("setting_stop_time", "23:00")
        val working_interval_list = resources.getStringArray(R.array.working_interval)
        val working_interval_list_position = sharedPreferences.getString("workingIntervalSpinnerPosition", "0")
        val working_interval = working_interval_list[working_interval_list_position!!.toInt()].toInt()

        initialization(user, address, ssid, bssid, maxOccupancy, start_timestamp, end_timestamp, working_interval.toString())
        startMyPeriodicWork(address, user, ssid, bssid, start_timestamp, end_timestamp, working_interval)
    }

    private fun initialization(
        user: String?,
        address: String?,
        ssid: String?,
        bssid: String?,
        maxOccupancy: String?,
        start_timestamp: String?,
        end_timestamp: String?,
        working_interval:String) {

        val db = Firebase.firestore
        val inputBuildingInfo = hashMapOf("Address" to address, "SSID" to ssid, "BSSID" to bssid, "MaxOccupancy" to maxOccupancy)
        val inputUserInfo = hashMapOf("UserName" to user, "start" to start_timestamp, "end" to end_timestamp, "working_interval" to working_interval)
        db.collection(address!!).document("Building_Information").set(inputBuildingInfo, SetOptions.merge())
        db.collection(address).document("$user+WIFI").set(inputUserInfo, SetOptions.merge())
    }

    private fun startMyPeriodicWork(address: String?,
                                    user: String?,
                                    ssid: String?,
                                    bssid: String?,
                                    start_timestamp: String?,
                                    end_timestamp: String?,
                                    working_interval: Int) {

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
            .setInputData(workDataOf("collection" to address,
                                            "document" to user,
                                            "Time_Start" to start_timestamp,
                                            "Time_End" to end_timestamp,
                                            "ssid" to ssid,
                                            "bssid" to bssid))
            .setConstraints(constraints)
            .addTag("wifiCheckingPeriodicWork")
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "myPeriodicWorkForWifiChecking",
            ExistingPeriodicWorkPolicy.KEEP,
            myRequest
        )
    }

    private fun stopWork() {
        WorkManager.getInstance().cancelAllWorkByTag("wifiCheckingPeriodicWork")
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
