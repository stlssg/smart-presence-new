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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import it.polito.interdisciplinaryProjects2021.smartpresence.R
import org.joda.time.DateTime
import java.util.*

@Suppress("DEPRECATION")
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
            val docRef = db.collection(targetBuildingForPro!!).document("Results")
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.data?.get("daily_available") == true) {
                        noTargetBuildingText1.visibility = View.GONE
                        proInfoScrollView.visibility = View.VISIBLE

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

                        val address = sharedPreferences.getString("address", "nothing")
                        val latitude = sharedPreferences.getString("latitude", "nothing")
                        val longitude = sharedPreferences.getString("longitude", "nothing")
                        val ssid = sharedPreferences.getString("ssid", "nothing")
                        val bssid = sharedPreferences.getString("bssid", "nothing")
                        val maxOccupancy = sharedPreferences.getString("maxOccupancy", "nothing")

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
                        val stringMax: String
                        val stringCurrentOccupants: String

                        when (languageSpinnerPosition) {
                            1 -> {
                                assignContentForBuildingInfo(address?.replace("_", " "), "Indirizzo", infoProAddress)
                                assignContentForBuildingInfo(latitude, "Latitudine", infoProLat)
                                assignContentForBuildingInfo(longitude, "Longitudine", infoProLon)
                                assignContentForBuildingInfo(ssid, "SSID", infoProSSID)
                                assignContentForBuildingInfo(bssid, "BSSID", infoProBSSID)
                                assignContentForBuildingInfo(maxOccupancy, "Occupanti massimi previsti", infoProMax)
                                avgOccupancySelectedTitle.text = "Occupazione oraria media (intervallo selezionato):"
                                avgOccupancyAllTitle.text = "Occupazione oraria media (tutto il giorno):"
                                infoTitle.text = "Informazioni sull'edificio di destinazione:"
                                statisticsTitle.text = "Statistiche:"
                                endDescription.text = "Altre funzioni stanno arrivando."
                                stringMax = "max"
                                stringCurrentOccupants = "Numero attuale di occupanti nell'edificio di destinazione"
                            }
                            2 -> {
                                assignContentForBuildingInfo(address?.replace("_", " "), "地址", infoProAddress)
                                assignContentForBuildingInfo(latitude, "纬度", infoProLat)
                                assignContentForBuildingInfo(longitude, "经度", infoProLon)
                                assignContentForBuildingInfo(ssid, "SSID", infoProSSID)
                                assignContentForBuildingInfo(bssid, "BSSID", infoProBSSID)
                                assignContentForBuildingInfo(maxOccupancy, "最大预期人数", infoProMax)
                                avgOccupancySelectedTitle.text = "平均每小时占用率（选定时间）:"
                                avgOccupancyAllTitle.text = "平均每小时人数（全天）:"
                                infoTitle.text = "目标建筑信息:"
                                statisticsTitle.text = "统计数据:"
                                endDescription.text = "更多功能即将推出."
                                stringMax = "最大"
                                stringCurrentOccupants = "目标建筑中的当前居住人数"
                            }
                            else -> {
                                assignContentForBuildingInfo(address?.replace("_", " "), "Address", infoProAddress)
                                assignContentForBuildingInfo(latitude, "Latitude", infoProLat)
                                assignContentForBuildingInfo(longitude, "Longitude", infoProLon)
                                assignContentForBuildingInfo(ssid, "SSID", infoProSSID)
                                assignContentForBuildingInfo(bssid, "BSSID", infoProBSSID)
                                assignContentForBuildingInfo(maxOccupancy, "Maximum expected occupants", infoProMax)
                                avgOccupancySelectedTitle.text = "Average hourly occupancy (selected interval):"
                                avgOccupancyAllTitle.text = "Average hourly occupancy (all day):"
                                infoTitle.text = "Information of target building:"
                                statisticsTitle.text = "Statistics:"
                                endDescription.text = "More functions are coming."
                                stringMax = "max"
                                stringCurrentOccupants = "Current number of occupants in target building"
                            }
                        }

            //        assignContentForBuildingInfo(address?.replace("_", " "), getString(R.string.configurationAlertAddressName), infoProAddress)
            //        assignContentForBuildingInfo(latitude, getString(R.string.configurationAlertLatName), infoProLat)
            //        assignContentForBuildingInfo(longitude, getString(R.string.configurationAlertLonName), infoProLon)
            //        assignContentForBuildingInfo(ssid, "SSID", infoProSSID)
            //        assignContentForBuildingInfo(bssid, "BSSID", infoProBSSID)
            //        assignContentForBuildingInfo(maxOccupancy, getString(R.string.configurationAlertMaxName), infoProMax)

                        // need change !!!!!!!
                        val maxString = if (maxOccupancy == "nothing") {
                            notAvailableString
                        } else {
                            maxOccupancy
                        }
                        val currentNumberOccupantsTitle = view.findViewById<TextView>(R.id.currentNumberOccupantsTitle)
                        currentNumberOccupantsTitle.text =
//            getString(R.string.pro_action_info_current_number) +
                            stringCurrentOccupants +
                                    " (" +
//            getString(R.string.pro_action_info_max_string) +
                                    stringMax +
                                    ": " +
                                    maxString +
                                    "):"

                        val currentNumberOccupants = view.findViewById<TextView>(R.id.currentNumberOccupants)
                        //        currentNumberOccupants.text = "2"

                        db.collection("RegisteredUser")
                            .whereEqualTo("targetBuilding", address)
                            .get()
                            .addOnSuccessListener { documents ->
                                var numCurrentOccupants = 0
                                val now = DateTime.now()
                                for (document in documents) {
                                    val currentPresenceCondition = (document.data["newestAction"] as Map<*, *>)["presence"].toString()
                                    val currentTimestamp = (document.data["newestAction"] as Map<*, *>)["timestamp"].toString()
                                    val currentDateTime = DateTime.parse(currentTimestamp)
//                    Log.d("targetBuilding: ", "$currentPresenceCondition and $currentTimestamp")
//                    Log.d("targetBuilding: ", "${(now.millis - currentDateTime.millis) / 1000 / 60}")

                                    if (currentPresenceCondition == "IN") {
                                        numCurrentOccupants ++
                                    } else if ((now.millis - currentDateTime.millis) / 1000 / 60 <= 90 && currentPresenceCondition == "CONNECTED") {
                                        numCurrentOccupants ++
                                    }
                                }

                                currentNumberOccupants.text = numCurrentOccupants.toString()
                            }
                            .addOnFailureListener { exception ->
                                Log.d("Error getting documents: ", "$exception")
                            }

                        val selectedHourlyOccupancy = view.findViewById<TextView>(R.id.selectedHourlyOccupancy)
                        selectedHourlyOccupancy.text = "0.8"
                        val allHourlyOccupancy = view.findViewById<TextView>(R.id.allHourlyOccupancy)
                        allHourlyOccupancy.text = "0.9"

                        val selectedHourlyOccupancyBar = view.findViewById<CircularProgressBar>(R.id.selectedHourlyOccupancyBar)
                        setProgressBar(selectedHourlyOccupancyBar, 0.8f)

                        val allHourlyOccupancyBar = view.findViewById<CircularProgressBar>(R.id.allHourlyOccupancyBar)
                        setProgressBar(allHourlyOccupancyBar, 0.9f)
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