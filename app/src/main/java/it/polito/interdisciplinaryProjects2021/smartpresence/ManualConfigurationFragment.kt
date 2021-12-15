package it.polito.interdisciplinaryProjects2021.smartpresence

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import it.polito.interdisciplinaryProjects2021.smartpresence.R

class ManualConfigurationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manual_configuration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        val address_input = view.findViewById<TextInputLayout>(R.id.address_input)
        val max_occupancy_input = view.findViewById<TextInputLayout>(R.id.max_occupancy_input)

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)
        val address = sharedPreferences.getString("address", "nothing")
        val maxOccupancy = sharedPreferences.getString("maxOccupancy", "nothing")

        if (address != "nothing") {
            address_input.editText?.setText(address?.replace("_", " "))
        }
        if (maxOccupancy != "nothing") {
            max_occupancy_input.editText?.setText(maxOccupancy?.replace("_", " "))
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
                val max_occupancy_input = requireView().findViewById<TextInputLayout>(R.id.max_occupancy_input).editText?.text.toString()

                if (address_input == "" || max_occupancy_input == "") {
                    Toast.makeText(requireContext(), getString(R.string.configuration_empty_input_message), Toast.LENGTH_LONG).show()
                } else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.configuration_confirm_title))
                        .setMessage("${getString(R.string.configurationAlertAddressName)}: ${address_input}\n" +
                                "${getString(R.string.configurationAlertMaxName)}: ${max_occupancy_input}\n")
                        .setNeutralButton(getString(R.string.setting_alert_cancel)) { _, _ -> }
                        .setPositiveButton(getString(R.string.configuration_alert_confirm_button)) { _, _ ->
                            findNavController().popBackStack()
                            Toast.makeText(requireContext(), getString(R.string.configuration_alert_toast), Toast.LENGTH_LONG).show()
                            with(sharedPreferences.edit()) {
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