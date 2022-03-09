package it.polito.interdisciplinaryProjects2021.smartpresence.presenceDetection.manualBased

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur
import it.polito.interdisciplinaryProjects2021.smartpresence.R
import java.util.*

class ManualConfigurationFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manual_configuration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)

        if (sharedPreferences.getString("newAddressSaved", "false").toBoolean()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.address_remind_title))
                .setMessage(getString(R.string.address_remind_msg))
                .setPositiveButton(getString(R.string.energySavingModeAlertButton)) { _, _ ->
                    with(sharedPreferences.edit()) {
                        putString("newAddressSaved", "false")
                        apply()
                    }
                }
                .show()
        }

        val professionalAccessGranted = sharedPreferences.getString("professionalAccessGranted", "false").toBoolean()
        val uploadConfManualFab = view.findViewById<FloatingActionButton>(R.id.uploadConfManualFab)
        if (professionalAccessGranted) {
            uploadConfManualFab.visibility = View.VISIBLE
        } else {
            uploadConfManualFab.visibility = View.GONE
        }

        val address_input = view.findViewById<TextInputLayout>(R.id.address_input)
        val max_occupancy_input = view.findViewById<TextInputLayout>(R.id.max_occupancy_input)
        val update_address_button = view.findViewById<Button>(R.id.update_address_button)

        val positioningCheckingStatus = sharedPreferences.getString("positioningCheckingStatus", "false").toBoolean()
        val wifiCheckingStatus = sharedPreferences.getString("wifiCheckingStatus", "false").toBoolean()
        if (positioningCheckingStatus || wifiCheckingStatus) {
            blurBackground(view)
            setHasOptionsMenu(false)
            address_input.isEnabled = false
            max_occupancy_input.isEnabled = false
            update_address_button.isEnabled = false
        } else {
            setHasOptionsMenu(true)
            val blurViewText = view.findViewById<TextView>(R.id.blurViewText)
            blurViewText.visibility = View.GONE
        }

//        val address_required = view.findViewById<TextView>(R.id.address_required)
//        val maxOccupancy_required = view.findViewById<TextView>(R.id.maxOccupancy_required)

//        makeLayoutGone(address_required)
//        makeLayoutGone(maxOccupancy_required)

        address_input.editText?.doAfterTextChanged {
//            makeLayoutGone(address_required)
            address_input.error = null
        }
        max_occupancy_input.editText?.doAfterTextChanged {
//            makeLayoutGone(maxOccupancy_required)
            max_occupancy_input.error = null
        }

        val address = sharedPreferences.getString("address", "nothing")
        val maxOccupancy = sharedPreferences.getString("maxOccupancy", "nothing")

        if (address != "nothing") {
            address_input.editText?.setText(address?.replace("_", " "))
        }
        if (maxOccupancy != "nothing") {
            max_occupancy_input.editText?.setText(maxOccupancy?.replace("_", " "))
        }

        update_address_button.setOnClickListener {
            address_input.error = null
            max_occupancy_input.error = null

            findNavController().navigate(R.id.mapFragment)
        }

        uploadConfManualFab.setOnClickListener {
            dealWithSavingOrUploadingConfiguration("uploading")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.save_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_button -> {
                dealWithSavingOrUploadingConfiguration("saving")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dealWithSavingOrUploadingConfiguration(action: String) {
        val sharedCode = sharedPreferences.getString("sharedCode", "nothing")
        if (sharedCode != "nothing" && action == "uploading") {
            Toast.makeText(requireContext(), getString(R.string.already_upload_msg) + " " + sharedCode, Toast.LENGTH_SHORT).show()
        } else {
            val address_input = requireView().findViewById<TextInputLayout>(R.id.address_input).editText?.text.toString()
            val max_occupancy_input = requireView().findViewById<TextInputLayout>(R.id.max_occupancy_input).editText?.text.toString()

            if (address_input == "" || max_occupancy_input == "") {
//                    val address_required = requireView().findViewById<TextView>(R.id.address_required)
//                    val maxOccupancy_required = requireView().findViewById<TextView>(R.id.maxOccupancy_required)

//                    if (address_input == "") { getLayoutBack(address_required) }
//                    if (max_occupancy_input == "") { getLayoutBack(maxOccupancy_required) }

                if (address_input == "") { view?.findViewById<TextInputLayout>(R.id.address_input)?.error = " " }
                if (max_occupancy_input == "") { view?.findViewById<TextInputLayout>(R.id.max_occupancy_input)?.error = getString(R.string.fieldRequiredMessage) }

                Toast.makeText(requireContext(), getString(R.string.configuration_empty_input_message), Toast.LENGTH_LONG).show()
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getString(R.string.configuration_confirm_title))
                    .setMessage("${getString(R.string.configurationAlertAddressName)}: ${address_input}\n" +
                            "${getString(R.string.configurationAlertMaxName)}: ${max_occupancy_input}\n")
                    .setNeutralButton(getString(R.string.setting_alert_cancel)) { _, _ -> }
                    .setPositiveButton(getString(R.string.configuration_alert_confirm_button)) { _, _ ->
                        Toast.makeText(requireContext(), getString(R.string.configuration_alert_toast), Toast.LENGTH_LONG).show()
                        with(sharedPreferences.edit()) {
                            putString("address", address_input.replace(" ", "_"))
                            putString("maxOccupancy", max_occupancy_input)
                            putString("addressConfigurationFinished", "true")
                            putString("maxOccupancyConfigurationFinished", "true")
                            apply()
                        }

                        val db = Firebase.firestore
                        val user = sharedPreferences.getString("keyCurrentAccount", "noEmail")
                        val docRef = user?.let { db.collection("RegisteredUser").document(it) }
                        val input = hashMapOf("targetBuilding" to address_input.replace(" ", "_"))
                        docRef?.set(input, SetOptions.merge())

                        if (action == "saving") {
                            findNavController().popBackStack()
                        } else if (action == "uploading") {
                            val allowedCharacters = "0123456789QWERTYUIOPASDFGHJKLZXCVBNM"
                            val random = Random()
                            val sb = StringBuilder(6)
                            for (i in 0 until 6)
                                sb.append(allowedCharacters[random.nextInt(allowedCharacters.length)])
                            val finalCode = sb.toString()
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle(getString(R.string.upload_alert_title))
                                .setMessage(getString(R.string.upload_alert_content) + " " + finalCode)
                                .setNeutralButton(getString(R.string.setting_alert_cancel)) { _, _ -> }
                                .setPositiveButton(getString(R.string.upload_alert_btn)) { _, _ ->
                                    Toast.makeText(requireContext(), getString(R.string.upload_success_msg), Toast.LENGTH_SHORT).show()

                                    with(sharedPreferences.edit()) {
                                        putString("sharedCode", finalCode)
                                        apply()
                                    }

                                    val buildingInput = address_input.replace(" ", "_")
                                    val inputBuildingList = hashMapOf("BuildingName" to buildingInput, "sharedCode" to finalCode)
                                    val inputBuildingInfo = hashMapOf(
                                        "Address" to buildingInput,
                                        "Maximum_expected_number" to max_occupancy_input,
                                        "detectionMethod" to "MANUAL"
                                    )
                                    db.collection("BuildingNameList").document(buildingInput).set(inputBuildingList, SetOptions.merge())
                                    db.collection(buildingInput).document("Building_Information").set(inputBuildingInfo, SetOptions.merge())

                                    findNavController().popBackStack()
                                }
                                .show()
                        }
                    }
                    .show()
            }
        }
    }

//    private fun makeLayoutGone(view: TextView) {
//        view.isVisible = false
//        val para_layout = view.layoutParams
//        para_layout.height = 0
//        view.layoutParams = para_layout
//    }
//
//    private fun getLayoutBack(view: TextView) {
//        view.isVisible = true
//        val para_layout = view.layoutParams
//        para_layout.height = ViewGroup.LayoutParams.WRAP_CONTENT
//        view.layoutParams = para_layout
//    }

    private fun blurBackground(view: View) {
        val blurView = view.findViewById<BlurView>(R.id.blurView)
        val decorView = activity?.window?.decorView
        val rootView = decorView?.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = decorView?.background

        blurView.setupWith(rootView!!)
            .setFrameClearDrawable(windowBackground)
            .setBlurAlgorithm(RenderScriptBlur(requireContext()))
            .setBlurRadius(3f)
            .setBlurAutoUpdate(true)
            .setHasFixedTransformationMatrix(true)
    }

}