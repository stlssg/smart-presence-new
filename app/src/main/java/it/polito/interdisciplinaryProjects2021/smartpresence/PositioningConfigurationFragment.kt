package it.polito.interdisciplinaryProjects2021.smartpresence

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

class PositioningConfigurationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_positioning_configuration, container, false)
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val address_input = view.findViewById<TextInputLayout>(R.id.address_input)
        val max_occupancy_input = view.findViewById<TextInputLayout>(R.id.max_occupancy_input)
        val latitude_input = view.findViewById<TextInputLayout>(R.id.latitude_input)
        val longitude_input = view.findViewById<TextInputLayout>(R.id.longitude_input)
        val energySavingModeOnOff = view.findViewById<Switch>(R.id.energySavingModeOnOff)

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)
        val address = sharedPreferences.getString("address", "nothing")
        val latitude = sharedPreferences.getString("latitude", "nothing")
        val longitude = sharedPreferences.getString("longitude", "nothing")
        val maxOccupancy = sharedPreferences.getString("maxOccupancy", "nothing")
        val energySavingMode = sharedPreferences.getString("energySavingMode", "nothing")

        if (latitude != "nothing") {
            latitude_input.editText?.setText(latitude?.replace("_", " "))
        }
        if (longitude != "nothing") {
            longitude_input.editText?.setText(longitude?.replace("_", " "))
        }
        if (address != "nothing") {
            address_input.editText?.setText(address?.replace("_", " "))
        }
        if (maxOccupancy != "nothing") {
            max_occupancy_input.editText?.setText(maxOccupancy?.replace("_", " "))
        }

        energySavingModeOnOff.isChecked = !(energySavingMode == "nothing" || energySavingMode == "off")

        energySavingModeOnOff?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.energySavingModeAlertTitle) + ": ON")
                    .setMessage(getString(R.string.energySavingModeOnMessage))
                    .setNegativeButton(getString(R.string.energySavingModeAlertNoButton)) { _, _ ->
                        energySavingModeOnOff.isChecked = false
                    }
                    .setPositiveButton(getString(R.string.energySavingModeAlertButton)) { _, _ ->
                        with(sharedPreferences.edit()) {
                            putString( "energySavingMode", "on")
                            apply()
                        }
                    }
                    .show()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.energySavingModeAlertTitle) + ": OFF")
                    .setMessage(getString(R.string.energySavingModeOffMessage))
                    .setNegativeButton(getString(R.string.energySavingModeAlertNoButton)) { _, _ ->
                        energySavingModeOnOff.isChecked = true
                    }
                    .setPositiveButton(getString(R.string.energySavingModeAlertButton)) { _, _ ->
                        with(sharedPreferences.edit()) {
                            putString( "energySavingMode", "off")
                            apply()
                        }
                    }
                    .show()
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

                val address_input = requireView().findViewById<TextInputLayout>(R.id.address_input).editText?.text.toString()
                val latitude_input = requireView().findViewById<TextInputLayout>(R.id.latitude_input).editText?.text.toString()
                val longitude_input = requireView().findViewById<TextInputLayout>(R.id.longitude_input).editText?.text.toString()
                val max_occupancy_input = requireView().findViewById<TextInputLayout>(R.id.max_occupancy_input).editText?.text.toString()

                if (latitude_input == "" || longitude_input == "" || address_input == "" || max_occupancy_input == "") {
                    Toast.makeText(requireContext(), getString(R.string.configuration_empty_input_message), Toast.LENGTH_LONG).show()
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.configuration_confirm_title))
                        .setMessage("Address: ${address_input}\nLatitude: ${latitude_input}\nLongitude: ${longitude_input}\nMaximum expected occupancy: ${max_occupancy_input}\n")
                        .setNeutralButton(getString(R.string.setting_alert_cancel)) { _, _ -> }
                        .setPositiveButton(getString(R.string.configuration_alert_confirm_button)) { _, _ ->
                            findNavController().popBackStack()
                            Toast.makeText(requireContext(), getString(R.string.configuration_alert_toast), Toast.LENGTH_LONG).show()
                            with(sharedPreferences.edit()) {
                                putString("latitude", latitude_input)
                                putString("longitude", longitude_input)
                                putString("address", address_input.replace(" ", "_"))
                                putString("maxOccupancy", max_occupancy_input)
                                apply()
                            }
                        }
                        .show()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}