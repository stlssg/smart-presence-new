package it.polito.interdisciplinaryProjects2021.smartpresence

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.recreate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import java.util.*

class SettingFragment : Fragment() {

    private var working_interval: Int = 15
    private var language: String = "English"
    private var signOutCondition: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    @SuppressLint("SimpleDateFormat", "UseSwitchCompatOrMaterialCode", "LongLogTag")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)

        val currentAccount = sharedPreferences.getString("keyCurrentAccount", "noEmail")
        view.findViewById<TextView>(R.id.setting_current_account).text = currentAccount

        val working_interval_list = resources.getStringArray(R.array.working_interval)
        val workingIntervalSpinner = view.findViewById<Spinner>(R.id.workingIntervalSpinner)
        if (workingIntervalSpinner != null) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, working_interval_list)
            workingIntervalSpinner.adapter = adapter
        }
        val workingIntervalSpinnerPosition = sharedPreferences.getString("workingIntervalSpinnerPosition", "0")?.toInt()
        workingIntervalSpinnerPosition?.let { workingIntervalSpinner.setSelection(it) }
        workingIntervalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                Toast.makeText(requireContext(), working_interval_list[position], Toast.LENGTH_SHORT).show()
                working_interval = working_interval_list[position].toInt()
                Log.d("working_interval", working_interval.toString())
                with(sharedPreferences.edit()) {
                    putString( "workingIntervalSpinnerPosition", position.toString())
                    commit()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) { }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("892318613323-qr7gmle90ur1qh9g52laui0so1583tak.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val signOutButton = view.findViewById<Button>(R.id.signOut)
        signOutButton.setOnClickListener{
            with(sharedPreferences.edit()) {
                putString("logInOrNot", "false")
                putString("keyCurrentAccount", "noEmail")
                putString("setting_start_time", "07:00")
                putString("setting_stop_time", "23:00")
                putString("workingIntervalSpinnerPosition", "0")
//                putString("languageSpinnerPosition", "0")
                putString("notificationOnOffCondition", "true")
                putString("ssid", "nothing")
                putString("bssid", "nothing")
                putString("address", "nothing")
                putString("maxOccupancy", "nothing")
                putString("latitude", "nothing")
                putString("longitude", "nothing")
                putString("energySavingMode", "nothing")
                putString("wifiCheckingStatus", "false")
                putString("positioningCheckingStatus", "false")
                putString("ssidConfigurationFinished", "false")
                putString("bssidConfigurationFinished", "false")
                putString("addressConfigurationFinished", "false")
                putString("maxOccupancyConfigurationFinished", "false")
                commit()
            }
            signOutCondition = true

            googleSignInClient.signOut()
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.signInFragment)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }

        val cal = Calendar.getInstance()
        val setting_start_time = view.findViewById<TextView>(R.id.setting_start_time)
        val setting_start_time_input = sharedPreferences.getString("setting_start_time", "07:00")
        Log.d("setting_start_time_input", setting_start_time_input.toString())
        setting_start_time.text = setting_start_time_input
        val setting_stop_time = view.findViewById<TextView>(R.id.setting_stop_time)
        val setting_stop_time_input = sharedPreferences.getString("setting_stop_time", "23:00")
        setting_stop_time.text = setting_stop_time_input
        setting_start_time.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                setting_start_time.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            val timePickerDialog = TimePickerDialog(requireContext(), timeSetListener, cal.get(
                Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
            timePickerDialog.show()
        }
        setting_stop_time.setOnClickListener {
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                setting_stop_time.text = SimpleDateFormat("HH:mm").format(cal.time)
            }
            val timePickerDialog = TimePickerDialog(requireContext(), timeSetListener, cal.get(
                Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true)
            timePickerDialog.show()
        }

        val deleteDataButton = view.findViewById<Button>(R.id.deleteData)
        deleteDataButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.setting_alert_title))
                .setMessage(getString(R.string.setting_alert_content))
                .setNeutralButton(getString(R.string.setting_alert_cancel)) { _, _ -> }
                .setPositiveButton(getString(R.string.setting_alert_confirm)) { _, _ ->
                    // Respond to positive button press
                    Toast.makeText(requireContext(), getString(R.string.deleteDataMessage), Toast.LENGTH_LONG).show()
                }
                .show()
        }

        val notificationOnOff = view.findViewById<Switch>(R.id.notificationOnOff)
        val notificationOnOffCondition = sharedPreferences.getString("notificationOnOffCondition", "true")
        notificationOnOff.isChecked = notificationOnOffCondition.toBoolean()
        notificationOnOff?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                with(sharedPreferences.edit()) {
                    putString( "notificationOnOffCondition", "true")
                    commit()
                }

                val wifiCheckingStatus = sharedPreferences.getString("wifiCheckingStatus", "false").toBoolean()
                val positioningCheckingStatus = sharedPreferences.getString("positioningCheckingStatus", "false").toBoolean()
                if (wifiCheckingStatus || positioningCheckingStatus) {
                    Firebase.messaging.subscribeToTopic("RemindingManuallyRestartService")
                        .addOnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Toast.makeText(requireContext(), getString(R.string.subscribeNotSuccess), Toast.LENGTH_LONG).show()
                            }
                        }
                }

                Toast.makeText(requireContext(), getString(R.string.notificationOnMessage), Toast.LENGTH_LONG).show()
            } else {
                with(sharedPreferences.edit()) {
                    putString( "notificationOnOffCondition", "false")
                    commit()
                }
                Toast.makeText(requireContext(), getString(R.string.notificationOffMessage), Toast.LENGTH_LONG).show()
                Firebase.messaging.unsubscribeFromTopic("RemindingManuallyRestartService")
            }
        }

        val language_list = resources.getStringArray(R.array.language)
        val languageSpinner = view.findViewById<Spinner>(R.id.languageSpinner)
        if (languageSpinner != null) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, language_list)
            languageSpinner.adapter = adapter
        }
        val languageSpinnerPosition = sharedPreferences.getString("languageSpinnerPosition", "0")?.toInt()
        languageSpinnerPosition?.let { languageSpinner.setSelection(it) }
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                language = language_list[position]
                if (languageSpinnerPosition != position) {
                    when (position) {
                        0 -> {
                            Toast.makeText(requireContext(), "Please restart the app!", Toast.LENGTH_SHORT).show()
                        }
                        1 -> {
                            Toast.makeText(requireContext(), "Please restart the app!", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(requireContext(), "请重启应用！", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                with(sharedPreferences.edit()) {
                    putString( "languageSpinnerPosition", position.toString())
                    commit()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) { }
        }
    }

    override fun onPause() {
        super.onPause()

        if (!signOutCondition) {
            val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)
            val startTime = requireView().findViewById<TextView>(R.id.setting_start_time)?.text
            val stopTime = requireView().findViewById<TextView>(R.id.setting_stop_time)?.text
            with(sharedPreferences.edit()) {
                putString( "setting_start_time", startTime.toString())
                putString( "setting_stop_time", stopTime.toString())
                commit()
            }
        }
    }

//    private fun recreateFragment() {
//        fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commit()
//    }

}