package it.polito.interdisciplinaryProjects2021.smartpresence.professional

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import it.polito.interdisciplinaryProjects2021.smartpresence.R
import org.joda.time.DateTime
import java.text.DecimalFormat
import java.util.*

@Suppress("DEPRECATION", "NAME_SHADOWING")
class ProActionFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notAvailableString: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pro_action, container, false)
    }

    @Suppress("NAME_SHADOWING")
    @SuppressLint("SetTextI18n", "LongLogTag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)
        val targetBuildingForPro = sharedPreferences.getString("targetBuildingForPro", "nothing")
        val languageSpinnerPosition = sharedPreferences.getString("languageSpinnerPosition", "0")?.toInt()
        val noTargetBuildingText1 = view.findViewById<TextView>(R.id.noTargetBuildingText1)
        val proInfoScrollView = view.findViewById<ScrollView>(R.id.proInfoScrollView)

        if (targetBuildingForPro == "nothing") {
            noTargetBuildingText1.visibility = View.VISIBLE
            when (languageSpinnerPosition) {
                1 -> {
                    noTargetBuildingText1.text = "Non hai ancora specificato il tuo edificio di destinazione."
                }
                2 -> {
                    noTargetBuildingText1.text = "您尚未指定目标建筑."
                }
                else -> {
                    noTargetBuildingText1.text = "You haven't specify your target building yet."
                }
            }
        } else {
            val db = Firebase.firestore

            notAvailableString = when (languageSpinnerPosition) {
                1 -> {
                    "Non disponibile"
                }
                2 -> {
                    "无法获取"
                }
                else -> {
                    "Not available"
                }
            }

            val infoProAddress = view.findViewById<TextView>(R.id.infoProAddress)
            val infoProLat = view.findViewById<TextView>(R.id.infoProLat)
            val infoProLon = view.findViewById<TextView>(R.id.infoProLon)
            val infoProSSID = view.findViewById<TextView>(R.id.infoProSSID)
            val infoProBSSID = view.findViewById<TextView>(R.id.infoProBSSID)
            val infoProMax = view.findViewById<TextView>(R.id.infoProMax)
            val avgOccupancySelectedTitle = view.findViewById<TextView>(R.id.avgOccupancySelectedTitle)
            val avgOccupancyAllTitle = view.findViewById<TextView>(R.id.avgOccupancyAllTitle)
            val infoTitle = view.findViewById<TextView>(R.id.textView5)
            val statisticsTitle = view.findViewById<TextView>(R.id.textView3)
            val endDescription = view.findViewById<TextView>(R.id.endDescription)
            val currentNumberOccupantsTitle = view.findViewById<TextView>(R.id.currentNumberOccupantsTitle)
            val currentNumberOccupants = view.findViewById<TextView>(R.id.currentNumberOccupants)
            var stringMax: String
            var stringCurrentOccupants: String
            val selectedHourlyOccupancy = view.findViewById<TextView>(R.id.selectedHourlyOccupancy)
            val allHourlyOccupancy = view.findViewById<TextView>(R.id.allHourlyOccupancy)
            val selectedHourlyOccupancyBar = view.findViewById<CircularProgressBar>(R.id.selectedHourlyOccupancyBar)
            val allHourlyOccupancyBar = view.findViewById<CircularProgressBar>(R.id.allHourlyOccupancyBar)

            when (languageSpinnerPosition) {
                1 -> {
                    avgOccupancySelectedTitle.text = "Occupazione oraria media (intervallo selezionato):"
                    avgOccupancyAllTitle.text = "Occupazione oraria media (tutto il giorno):"
                    infoTitle.text = "Informazioni sull'edificio di destinazione:"
                    statisticsTitle.text = "Statistiche:"
                    endDescription.text = "Altre funzioni stanno arrivando."
                    stringMax = "max"
                    stringCurrentOccupants = "Numero attuale di occupanti nell'edificio di destinazione"
                }
                2 -> {
                    avgOccupancySelectedTitle.text = "平均每小时占用率（选定时间）:"
                    avgOccupancyAllTitle.text = "平均每小时人数（全天）:"
                    infoTitle.text = "目标建筑信息:"
                    statisticsTitle.text = "统计数据:"
                    endDescription.text = "更多功能即将推出."
                    stringMax = "最大"
                    stringCurrentOccupants = "目标建筑中的当前居住人数"
                }
                else -> {
                    avgOccupancySelectedTitle.text = "Average hourly occupancy (selected interval):"
                    avgOccupancyAllTitle.text = "Average hourly occupancy (all day):"
                    infoTitle.text = "Information of target building:"
                    statisticsTitle.text = "Statistics:"
                    endDescription.text = "More functions are coming."
                    stringMax = "max"
                    stringCurrentOccupants = "Current number of occupants in target building"
                }
            }

            if (targetBuildingForPro == sharedPreferences.getString("address", "nothing")) {
                noTargetBuildingText1.visibility = View.GONE
                proInfoScrollView.visibility = View.VISIBLE

                val address = sharedPreferences.getString("address", "nothing")
                val latitude = sharedPreferences.getString("latitude", "nothing")
                val longitude = sharedPreferences.getString("longitude", "nothing")
                val ssid = sharedPreferences.getString("ssid", "nothing")
                val bssid = sharedPreferences.getString("bssid", "nothing")
                val maxOccupancy = sharedPreferences.getString("maxOccupancy", "nothing")

                when (languageSpinnerPosition) {
                    1 -> {
                        assignContentForBuildingInfo(address?.replace("_", " "), "Indirizzo", infoProAddress)
                        assignContentForBuildingInfo(latitude, "Latitudine", infoProLat)
                        assignContentForBuildingInfo(longitude, "Longitudine", infoProLon)
                        assignContentForBuildingInfo(ssid, "SSID", infoProSSID)
                        assignContentForBuildingInfo(bssid, "BSSID", infoProBSSID)
                        assignContentForBuildingInfo(maxOccupancy, "Occupanti massimi previsti", infoProMax)
                    }
                    2 -> {
                        assignContentForBuildingInfo(address?.replace("_", " "), "地址", infoProAddress)
                        assignContentForBuildingInfo(latitude, "纬度", infoProLat)
                        assignContentForBuildingInfo(longitude, "经度", infoProLon)
                        assignContentForBuildingInfo(ssid, "SSID", infoProSSID)
                        assignContentForBuildingInfo(bssid, "BSSID", infoProBSSID)
                        assignContentForBuildingInfo(maxOccupancy, "最大预期人数", infoProMax)
                    }
                    else -> {
                        assignContentForBuildingInfo(address?.replace("_", " "), "Address", infoProAddress)
                        assignContentForBuildingInfo(latitude, "Latitude", infoProLat)
                        assignContentForBuildingInfo(longitude, "Longitude", infoProLon)
                        assignContentForBuildingInfo(ssid, "SSID", infoProSSID)
                        assignContentForBuildingInfo(bssid, "BSSID", infoProBSSID)
                        assignContentForBuildingInfo(maxOccupancy, "Maximum expected occupants", infoProMax)
                    }
                }

                val maxString = if (maxOccupancy == "nothing") {
                    notAvailableString
                } else {
                    maxOccupancy
                }

                currentNumberOccupantsTitle.text = "$stringCurrentOccupants ($stringMax: $maxString):"

                calculateCurrentOccupantsAndAssignToTextView(db, address, currentNumberOccupants)
                calculateAverageHourlyOccupancyAndAssignToView(db, address, selectedHourlyOccupancy, allHourlyOccupancy, selectedHourlyOccupancyBar, allHourlyOccupancyBar)
            } else {
                val docRef = db.collection(targetBuildingForPro!!).document("Building_Information")
                docRef.get()
                    .addOnSuccessListener { document ->
                        if (document.data?.get("Address") == targetBuildingForPro) {
                            noTargetBuildingText1.visibility = View.GONE
                            proInfoScrollView.visibility = View.VISIBLE

                            val latitude = if (document.data?.get("latitude") == null) {
                                "nothing"
                            } else {
                                document.data?.get("latitude")
                            }
                            val longitude = if (document.data?.get("longitude") == null) {
                                "nothing"
                            } else {
                                document.data?.get("longitude")
                            }
                            val ssid = if (document.data?.get("SSID") == null) {
                                "nothing"
                            } else {
                                document.data?.get("SSID")
                            }
                            val bssid = if (document.data?.get("BSSID") == null) {
                                "nothing"
                            } else {
                                document.data?.get("BSSID")
                            }
                            val maxOccupancy = if (document.data?.get("Maximum_expected_number") == null) {
                                "nothing"
                            } else {
                                document.data?.get("Maximum_expected_number")
                            }

                            when (languageSpinnerPosition) {
                                1 -> {
                                    assignContentForBuildingInfo(targetBuildingForPro.replace("_", " "), "Indirizzo", infoProAddress)
                                    assignContentForBuildingInfo(latitude as String?, "Latitudine", infoProLat)
                                    assignContentForBuildingInfo(longitude as String?, "Longitudine", infoProLon)
                                    assignContentForBuildingInfo(ssid as String?, "SSID", infoProSSID)
                                    assignContentForBuildingInfo(bssid as String?, "BSSID", infoProBSSID)
                                    assignContentForBuildingInfo(maxOccupancy as String?, "Occupanti massimi previsti", infoProMax)
                                }
                                2 -> {
                                    assignContentForBuildingInfo(targetBuildingForPro?.replace("_", " "), "地址", infoProAddress)
                                    assignContentForBuildingInfo(latitude as String?, "纬度", infoProLat)
                                    assignContentForBuildingInfo(longitude as String?, "经度", infoProLon)
                                    assignContentForBuildingInfo(ssid as String?, "SSID", infoProSSID)
                                    assignContentForBuildingInfo(bssid as String?, "BSSID", infoProBSSID)
                                    assignContentForBuildingInfo(maxOccupancy as String?, "最大预期人数", infoProMax)
                                }
                                else -> {
                                    assignContentForBuildingInfo(targetBuildingForPro?.replace("_", " "), "Address", infoProAddress)
                                    assignContentForBuildingInfo(latitude as String?, "Latitude", infoProLat)
                                    assignContentForBuildingInfo(longitude as String?, "Longitude", infoProLon)
                                    assignContentForBuildingInfo(ssid as String?, "SSID", infoProSSID)
                                    assignContentForBuildingInfo(bssid as String?, "BSSID", infoProBSSID)
                                    assignContentForBuildingInfo(maxOccupancy as String?, "Maximum expected occupants", infoProMax)
                                }
                            }

                            val maxString = if (maxOccupancy == "nothing") {
                                notAvailableString
                            } else {
                                maxOccupancy
                            }

                            currentNumberOccupantsTitle.text = "$stringCurrentOccupants ($stringMax: $maxString):"

                            calculateCurrentOccupantsAndAssignToTextView(db, targetBuildingForPro, currentNumberOccupants)
                            calculateAverageHourlyOccupancyAndAssignToView(db, targetBuildingForPro, selectedHourlyOccupancy, allHourlyOccupancy, selectedHourlyOccupancyBar, allHourlyOccupancyBar)
                        } else {
                            noTargetBuildingText1.visibility = View.VISIBLE
                            when (languageSpinnerPosition) {
                                1 -> {
                                    noTargetBuildingText1.text = "I risultati non sono ancora disponibili in questo momento, per favore prova più tardi."
                                }
                                2 -> {
                                    noTargetBuildingText1.text = "暂时还没有结果，请稍后再试。"
                                }
                                else -> {
                                    noTargetBuildingText1.text = "The results are still not available at this moment, please try it later."
                                }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("get failed with ", "$exception")
                    }
            }
        }

    }

    @SuppressLint("LongLogTag")
    private fun calculateCurrentOccupantsAndAssignToTextView(db: FirebaseFirestore, address: String?, tv: TextView) {
        db.collection("RegisteredUser")
            .whereEqualTo("targetBuilding", address)
            .get()
            .addOnSuccessListener { documents ->
                var numCurrentOccupants = 0
                val now = DateTime.now()
                var numUsefulDoc = 0
                for (document in documents) {
                    if (document.data["newestAction"] != null) {
                        numUsefulDoc += 1
                        val currentPresenceCondition = (document.data["newestAction"] as Map<*, *>)["presence"].toString()
                        val currentTimestamp = (document.data["newestAction"] as Map<*, *>)["timestamp"].toString()
                        val currentDateTime = DateTime.parse(currentTimestamp)
//                        Log.d("targetBuilding!!!", "$currentPresenceCondition and $currentTimestamp")
//                        Log.d("targetBuilding!!!", "${(now.millis - currentDateTime.millis) / 1000 / 60}")

                        if (currentPresenceCondition == "IN") {
                            numCurrentOccupants ++
                        } else if ((now.millis - currentDateTime.millis) / 1000 / 60 <= 90 && currentPresenceCondition == "CONNECTED") {
                            numCurrentOccupants ++
                        }
                    }
                }

                if (numUsefulDoc == 0) {
                    tv.text = "-"
                } else {
                    tv.text = numCurrentOccupants.toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error getting documents: ", "$exception")
            }
    }

    private fun calculateAverageHourlyOccupancyAndAssignToView(
        db:FirebaseFirestore,
        address: String?,
        tvSelected: TextView,
        tvAll: TextView,
        barSelected: CircularProgressBar,
        barAll: CircularProgressBar,
    ) {
        val docRef = db.collection(address!!).document("Results")
        docRef.get().addOnSuccessListener { document ->
            if (document.data?.get("daily_available") == true) {
                docRef.collection("daily_data")
                    .get()
                    .addOnSuccessListener { documents ->
                        var dataSelected = 0f
                        var dataAll = 0f
                        var numSelected = 0
                        for (document in documents) {
                            val tempValue = document.data.getValue("occupancy").toString()
                            Log.d("tempValue!!!!", tempValue)
                            if (tempValue == "1.0") {
                                dataAll += 1.0f
                            } else {
                                dataSelected += tempValue.toFloat()
                                numSelected += 1
                            }
                        }
                        dataAll += dataSelected

                        val avgAll = dataAll / 24
                        val avgSelected = dataSelected / numSelected
                        val df = DecimalFormat("#.##")
                        tvAll.text = df.format(avgAll)
                        tvSelected.text = df.format(avgSelected)
                        setProgressBar(barAll, avgAll)
                        setProgressBar(barSelected, avgSelected)
                    }
            } else {
                tvSelected.text = "-"
                tvAll.text = "-"
                setProgressBar(barAll, 0f)
                setProgressBar(barSelected, 0f)
            }
        }

    }

    private fun setProgressBar(progressBarView: CircularProgressBar, progressInput: Float) {
        progressBarView.apply {
            // Set Progress progress = 2f or with animation
            setProgressWithAnimation(progressInput, 1000) // milli sec => 1s

            // Set Progress Max
            progressMax = 1f

            // Set ProgressBar Color and background ProgressBar Color
            progressBarColor = Color.BLUE
            backgroundProgressBarColor = Color.GRAY

            // Set Width
            progressBarWidth = 6f // in DP
            backgroundProgressBarWidth = 12f // in DP

            // Other
            roundBorder = true
            startAngle = 0f
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT
        }
    }

    @SuppressLint("SetTextI18n")
    private fun assignContentForBuildingInfo(stringInput: String?, additionalString: String, tv: TextView) {
        if (stringInput == "nothing") {
            tv.text = "$additionalString: $notAvailableString"
        } else {
            tv.text = "$additionalString: $stringInput"
        }
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)

        when (sharedPreferences.getString("languageSpinnerPosition", "0")?.toInt()) {
            1 -> setLang("it")
            2 -> setLang("zh")
            else -> setLang("en")
        }
    }

    override fun onPause() {
        super.onPause()

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)

        when (sharedPreferences.getString("languageSpinnerPosition", "0")?.toInt()) {
            1 -> setLang("it")
            2 -> setLang("zh")
            else -> setLang("en")
        }
    }

    private fun setLang(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        (activity as AppCompatActivity).baseContext.resources.updateConfiguration(config, (activity as AppCompatActivity).baseContext.resources.displayMetrics)
    }

}