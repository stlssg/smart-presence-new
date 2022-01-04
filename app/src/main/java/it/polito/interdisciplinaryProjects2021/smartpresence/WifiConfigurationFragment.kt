package it.polito.interdisciplinaryProjects2021.smartpresence

import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.interdisciplinaryProjects2021.smartpresence.R

class WifiConfigurationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wifi_configuration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

//        view.findViewById<TextInputLayout>(R.id.latitude).isVisible = false
//        view.findViewById<TextInputLayout>(R.id.longitude).isVisible = false
//        val linearLayout = view.findViewById<LinearLayout>(R.id.linearLayout)
//        val layoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        layoutParams.setMargins(0, 0, 0, 0)
//        layoutParams.height = 0
//        linearLayout.layoutParams = layoutParams

        val ssid_required = view.findViewById<TextView>(R.id.ssid_required)
        val bssid_required = view.findViewById<TextView>(R.id.bssid_required)
        val address_required = view.findViewById<TextView>(R.id.address_required)
        val maxOccupancy_required = view.findViewById<TextView>(R.id.maxOccupancy_required)

        makeLayoutGone(ssid_required)
        makeLayoutGone(bssid_required)
        makeLayoutGone(address_required)
        makeLayoutGone(maxOccupancy_required)

        val ssid_input = view.findViewById<TextInputLayout>(R.id.ssid_input)
        val bssid_input = view.findViewById<TextInputLayout>(R.id.bssid_input)
        val address_input = view.findViewById<TextInputLayout>(R.id.address_input)
        val max_occupancy_input = view.findViewById<TextInputLayout>(R.id.max_occupancy_input)

        ssid_input.editText?.doAfterTextChanged {
            makeLayoutGone(ssid_required)
        }
        bssid_input.editText?.doAfterTextChanged {
            makeLayoutGone(bssid_required)
        }
        address_input.editText?.doAfterTextChanged {
            makeLayoutGone(address_required)
        }
        max_occupancy_input.editText?.doAfterTextChanged {
            makeLayoutGone(maxOccupancy_required)
        }

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)
        val ssid = sharedPreferences.getString("ssid", "nothing")
        val bssid = sharedPreferences.getString("bssid", "nothing")
        val address = sharedPreferences.getString("address", "nothing")
        val maxOccupancy = sharedPreferences.getString("maxOccupancy", "nothing")

        if (ssid != "nothing") {
            ssid_input.editText?.setText(ssid?.replace("_", " "))
        }
        if (bssid != "nothing") {
            bssid_input.editText?.setText(bssid?.replace("_", " "))
        }
        if (address != "nothing") {
            address_input.editText?.setText(address?.replace("_", " "))
        }
        if (maxOccupancy != "nothing") {
            max_occupancy_input.editText?.setText(maxOccupancy?.replace("_", " "))
        }

        val update_wifi_info_button = view.findViewById<Button>(R.id.update_wifi_info_button)
        update_wifi_info_button.setOnClickListener {
            val wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val ssidString = wifiInfo.ssid
            val bssidString = wifiInfo.bssid
            val netId = wifiInfo.networkId

            if (netId == -1) {
                Toast.makeText(requireContext(), getString(R.string.no_wifi_connection_message), Toast.LENGTH_LONG).show()
            } else {
                val ssidFinal = if (ssidString.startsWith("\"") && ssidString.endsWith("\"")) {
                    ssidString.substring(1, ssidString.length -1)
                } else {
                    ssidString
                }
                val bssidFinal = if (bssidString.startsWith("\"") && bssidString.endsWith("\"")) {
                    bssidString.substring(1, ssidString.length -1)
                } else {
                    bssidString
                }

                ssid_input.editText?.setText(ssidFinal)
                bssid_input.editText?.setText(bssidFinal)
            }
        }

        val update_address_button = view.findViewById<Button>(R.id.update_address_button)
        update_address_button.setOnClickListener {
            findNavController().navigate(R.id.mapFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_button -> {
                val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)

                val ssid_input = requireView().findViewById<TextInputLayout>(R.id.ssid_input).editText?.text.toString()
                val bssid_input = requireView().findViewById<TextInputLayout>(R.id.bssid_input).editText?.text.toString()
                val address_input = requireView().findViewById<TextInputLayout>(R.id.address_input).editText?.text.toString()
                val max_occupancy_input = requireView().findViewById<TextInputLayout>(R.id.max_occupancy_input).editText?.text.toString()

                if (ssid_input == "" || bssid_input == "" || address_input == "" || max_occupancy_input == "") {
                    val ssid_required = requireView().findViewById<TextView>(R.id.ssid_required)
                    val bssid_required = requireView().findViewById<TextView>(R.id.bssid_required)
                    val address_required = requireView().findViewById<TextView>(R.id.address_required)
                    val maxOccupancy_required = requireView().findViewById<TextView>(R.id.maxOccupancy_required)

                    if (ssid_input == "") { getLayoutBack(ssid_required) }
                    if (bssid_input == "") { getLayoutBack(bssid_required) }
                    if (address_input == "") { getLayoutBack(address_required) }
                    if (max_occupancy_input == "") { getLayoutBack(maxOccupancy_required) }

                    Toast.makeText(requireContext(), getString(R.string.configuration_empty_input_message), Toast.LENGTH_LONG).show()
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.configuration_confirm_title))
                        .setMessage("SSID: ${ssid_input}\n" +
                                "BSSID: ${bssid_input}\n" +
                                "${getString(R.string.configurationAlertAddressName)}: ${address_input}\n" +
                                "${getString(R.string.configurationAlertMaxName)}: ${max_occupancy_input}\n")
                        .setNeutralButton(getString(R.string.setting_alert_cancel)) { _, _ -> }
                        .setPositiveButton(getString(R.string.configuration_alert_confirm_button)) { _, _ ->
                            Toast.makeText(requireContext(), getString(R.string.configuration_alert_toast), Toast.LENGTH_LONG).show()
                            with(sharedPreferences.edit()) {
                                putString("ssid", ssid_input.replace(" ", "_"))
                                putString("bssid", bssid_input)
                                putString("address", address_input.replace(" ", "_"))
                                putString("maxOccupancy", max_occupancy_input)
                                putString("ssidConfigurationFinished", "true")
                                putString("bssidConfigurationFinished", "true")
                                putString("addressConfigurationFinished", "true")
                                putString("maxOccupancyConfigurationFinished", "true")
                                apply()
                            }

                            val db = Firebase.firestore
                            val user = sharedPreferences.getString("keyCurrentAccount", "noEmail")
                            val docRef = user?.let { db.collection("RegisteredUser").document(it) }
                            val input = hashMapOf("targetBuilding" to address_input.replace(" ", "_"))
                            docRef?.set(input, SetOptions.merge())

                            findNavController().popBackStack()
                        }
                        .show()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun makeLayoutGone(view: TextView) {
        view.isVisible = false
        val para_layout = view.layoutParams
        para_layout.height = 0
        view.layoutParams = para_layout
    }

    private fun getLayoutBack(view: TextView) {
        view.isVisible = true
        val para_layout = view.layoutParams
        para_layout.height = ViewGroup.LayoutParams.WRAP_CONTENT
        view.layoutParams = para_layout
    }

}