package it.polito.interdisciplinaryProjects2021.smartpresence

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AADataLabels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class GraphFragment : Fragment() {

    private val dailyData : MutableList<Float> = mutableListOf()
    private val weeklyData : MutableList<Float> = mutableListOf()
    private val monthlyData : MutableList<Float> = mutableListOf()
    private val yearlyData : MutableList<Float> = mutableListOf()
    private val totalData : MutableList<Float> = mutableListOf()

    private val dailyLabel : MutableList<String> = mutableListOf()
    private val weeklyLabel : MutableList<String> = mutableListOf()
    private val monthlyLabel : MutableList<String> = mutableListOf()
    private val yearlyLabel : MutableList<String> = mutableListOf()
    private val totalLabel : MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore
        val example_results = db.collection("example_result").document("Results")

        val aaChartView = view.findViewById<AAChartView>(R.id.aa_chart_view)
        aaChartView.visibility = GONE
        val errorTextView = view.findViewById<TextView>(R.id.errorTextView)
        errorTextView.visibility = GONE
        val loadingAnim = view.findViewById<LottieAnimationView>(R.id.loadingAnim)

        val dailyOption = view.findViewById<TextView>(R.id.dailyOption)
        val weeklyOption = view.findViewById<TextView>(R.id.weeklyOption)
        val monthlyOption = view.findViewById<TextView>(R.id.monthlyOption)
        val yearlyOption = view.findViewById<TextView>(R.id.yearlyOption)
        val totalOption = view.findViewById<TextView>(R.id.totalOption)
        dailyOption.text = getString(R.string.graphOptionDaily)
        weeklyOption.text = getString(R.string.graphOptionWeekly)
        monthlyOption.text = getString(R.string.graphOptionMonthly)
        yearlyOption.text = getString(R.string.graphOptionYearly)
        totalOption.text = getString(R.string.graphOptionTotal)

        dailyOption.setOnClickListener {
            resetData()
            setButton(dailyOption, weeklyOption, monthlyOption, yearlyOption, totalOption)
            errorTextView.visibility = GONE
            loadingAnim.visibility = VISIBLE

            example_results.get()
                .addOnSuccessListener { document ->
                    if (document.data?.get("daily_available") == true) {
                        example_results.collection("daily_data")
                            .orderBy("time_interval")
                            .get()
                            .addOnSuccessListener { documents ->
                                loadingAnim.visibility = GONE
                                aaChartView.visibility = VISIBLE
                                for (document in documents) {
                                    dailyLabel.add(document.data.getValue("time_interval").toString())
                                    dailyData.add(document.data.getValue("occupancy").toString().toFloat() * 100)
                                }
                                aaChartView.aa_drawChartWithChartModel(
                                    generateChart(dailyLabel, dailyData, getString(R.string.graphDailyTitle), getString(R.string.graphDailySubtitle))
                                )
                            }
                    } else {
                        loadingAnim.visibility = GONE
                        aaChartView.visibility = GONE
                        errorTextView.visibility = VISIBLE
                        errorTextView.text = getString(R.string.graphNoDataMessage)
                    }
                }
                .addOnFailureListener { _ ->
                    loadingAnim.visibility = GONE
                    aaChartView.visibility = GONE
                    errorTextView.visibility = VISIBLE
                    errorTextView.text = getString(R.string.graphErrorMessage)
                }
        }

        weeklyOption.setOnClickListener {
            resetData()
            setButton(weeklyOption, dailyOption, monthlyOption, yearlyOption, totalOption)
            errorTextView.visibility = GONE
            loadingAnim.visibility = VISIBLE

            example_results.get()
                .addOnSuccessListener { document ->
                    if (document.data?.get("weekly_available") == true) {
                        example_results.collection("weekly_data")
                            .orderBy("time_interval")
                            .get()
                            .addOnSuccessListener { documents ->
                                loadingAnim.visibility = GONE
                                aaChartView.visibility = VISIBLE
                                for (document in documents) {
                                    weeklyLabel.add(document.data.getValue("time_interval").toString())
                                    weeklyData.add(document.data.getValue("occupancy").toString().toFloat() * 100)
                                }
                                aaChartView.aa_drawChartWithChartModel(
                                    generateChart(weeklyLabel, weeklyData, getString(R.string.graphDailyTitle), getString(R.string.graphDailySubtitle))
                                )
                            }
                    } else {
                        loadingAnim.visibility = GONE
                        aaChartView.visibility = GONE
                        errorTextView.visibility = VISIBLE
                        errorTextView.text = getString(R.string.graphNoDataMessage)
                    }
                }
                .addOnFailureListener { _ ->
                    loadingAnim.visibility = GONE
                    aaChartView.visibility = GONE
                    errorTextView.visibility = VISIBLE
                    errorTextView.text = getString(R.string.graphErrorMessage)
                }
        }

        monthlyOption.setOnClickListener {
            resetData()
            setButton(monthlyOption, dailyOption, weeklyOption, yearlyOption, totalOption)
            errorTextView.visibility = GONE
            loadingAnim.visibility = VISIBLE

            example_results.get()
                .addOnSuccessListener { document ->
                    if (document.data?.get("monthly_available") == true) {
                        example_results.collection("monthly_data")
                            .orderBy("time_interval")
                            .get()
                            .addOnSuccessListener { documents ->
                                loadingAnim.visibility = GONE
                                aaChartView.visibility = VISIBLE
                                for (document in documents) {
                                    monthlyLabel.add(document.data.getValue("time_interval").toString())
                                    monthlyData.add(document.data.getValue("occupancy").toString().toFloat() * 100)
                                }
                                aaChartView.aa_drawChartWithChartModel(
                                    generateChart(monthlyLabel, monthlyData, getString(R.string.graphDailyTitle), getString(R.string.graphDailySubtitle))
                                )
                            }
                    } else {
                        loadingAnim.visibility = GONE
                        aaChartView.visibility = GONE
                        errorTextView.visibility = VISIBLE
                        errorTextView.text = getString(R.string.graphNoDataMessage)
                    }
                }
                .addOnFailureListener { _ ->
                    loadingAnim.visibility = GONE
                    aaChartView.visibility = GONE
                    errorTextView.visibility = VISIBLE
                    errorTextView.text = getString(R.string.graphErrorMessage)
                }
        }

        yearlyOption.setOnClickListener {
            resetData()
            setButton(yearlyOption, dailyOption, weeklyOption, monthlyOption, totalOption)
            errorTextView.visibility = GONE
            loadingAnim.visibility = VISIBLE

            example_results.get()
                .addOnSuccessListener { document ->
                    if (document.data?.get("yearly_available") == true) {
                        example_results.collection("yearly_data")
                            .orderBy("time_interval")
                            .get()
                            .addOnSuccessListener { documents ->
                                loadingAnim.visibility = GONE
                                aaChartView.visibility = VISIBLE
                                for (document in documents) {
                                    yearlyLabel.add(document.data.getValue("time_interval").toString())
                                    yearlyData.add(document.data.getValue("occupancy").toString().toFloat() * 100)
                                }
                                aaChartView.aa_drawChartWithChartModel(
                                    generateChart(yearlyLabel, yearlyData, getString(R.string.graphDailyTitle), getString(R.string.graphDailySubtitle))
                                )
                            }
                    } else {
                        loadingAnim.visibility = GONE
                        aaChartView.visibility = GONE
                        errorTextView.visibility = VISIBLE
                        errorTextView.text = getString(R.string.graphNoDataMessage)
                    }
                }
                .addOnFailureListener { _ ->
                    loadingAnim.visibility = GONE
                    aaChartView.visibility = GONE
                    errorTextView.visibility = VISIBLE
                    errorTextView.text = getString(R.string.graphErrorMessage)
                }
        }

        totalOption.setOnClickListener {
            resetData()
            setButton(totalOption, dailyOption, weeklyOption, monthlyOption, yearlyOption)
            errorTextView.visibility = GONE
            loadingAnim.visibility = VISIBLE

            example_results.get()
                .addOnSuccessListener { document ->
                    if (document.data?.get("total_available") == true) {
                        example_results.collection("total_data")
                            .orderBy("time_interval")
                            .get()
                            .addOnSuccessListener { documents ->
                                loadingAnim.visibility = GONE
                                aaChartView.visibility = VISIBLE
                                for (document in documents) {
                                    totalLabel.add(document.data.getValue("time_interval").toString())
                                    totalData.add(document.data.getValue("occupancy").toString().toFloat() * 100)
                                }
                                aaChartView.aa_drawChartWithChartModel(
                                    generateChart(totalLabel, totalData, getString(R.string.graphDailyTitle), getString(R.string.graphDailySubtitle))
                                )
                            }
                    } else {
                        loadingAnim.visibility = GONE
                        aaChartView.visibility = GONE
                        errorTextView.visibility = VISIBLE
                        errorTextView.text = getString(R.string.graphNoDataMessage)
                    }
                }
                .addOnFailureListener { _ ->
                    loadingAnim.visibility = GONE
                    aaChartView.visibility = GONE
                    errorTextView.visibility = VISIBLE
                    errorTextView.text = getString(R.string.graphErrorMessage)
                }
        }

        // initialization for graph of daily
        example_results.get()
            .addOnSuccessListener { document ->
                if (document.data?.get("daily_available") == true) {
                    example_results.collection("daily_data")
                        .orderBy("time_interval")
                        .get()
                        .addOnSuccessListener { documents ->
                            loadingAnim.visibility = GONE
                            aaChartView.visibility = VISIBLE
                            for (document in documents) {
//                                Log.d("data!!!!!!!", "${document.id} => " +
//                                        "${document.data.getValue("occupancy")} and " +
//                                        "${document.data.getValue("time_interval")}")
                                dailyLabel.add(document.data.getValue("time_interval").toString())
                                dailyData.add(document.data.getValue("occupancy").toString().toFloat() * 100)
                            }
//                            Log.d("data!!!!!!!", "$dailyData and $dailyLabel")
                            aaChartView.aa_drawChartWithChartModel(
                                generateChart(dailyLabel, dailyData, getString(R.string.graphDailyTitle), getString(R.string.graphDailySubtitle))
                            )
                        }
                } else {
                    loadingAnim.visibility = GONE
                    aaChartView.visibility = GONE
                    errorTextView.visibility = VISIBLE
                    errorTextView.text = getString(R.string.graphNoDataMessage)
                }
            }
            .addOnFailureListener { _ ->
                loadingAnim.visibility = GONE
                aaChartView.visibility = GONE
                errorTextView.visibility = VISIBLE
                errorTextView.text = getString(R.string.graphErrorMessage)
            }

    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)
        val languageSpinnerPosition = sharedPreferences.getString("languageSpinnerPosition", "0")?.toInt()

        when (languageSpinnerPosition) {
            1 -> setLang("it")
            2 -> setLang("zh")
            else -> setLang("en")
        }
    }

    override fun onPause() {
        super.onPause()

        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("AppSharedPreference", Context.MODE_PRIVATE)
        val languageSpinnerPosition = sharedPreferences.getString("languageSpinnerPosition", "0")?.toInt()

        when (languageSpinnerPosition) {
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

    private fun generateChart(labels: MutableList<String>,
                              data: MutableList<Float>,
                              title: String,
                              subtitle: String): AAChartModel {
        val aaChartModel = AAChartModel()
            .chartType(AAChartType.Area)
            .animationType(AAChartAnimationType.EaseOutBack)
            .title(title)
            .subtitle(subtitle)
            .yAxisTitle(getString(R.string.graphYTitle))
            .dataLabelsEnabled(false)
            .categories(labels.toTypedArray())
            .colorsTheme(arrayOf("#fe117c"))
            .series(arrayOf(
                AASeriesElement()
                    .name("you house name")
                    .data(data.toTypedArray())
            ))

        return aaChartModel
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setButton(selectedButton: TextView,
                          button1: TextView,
                          button2: TextView,
                          button3: TextView,
                          button4: TextView) {
        selectedButton.background = resources.getDrawable(R.drawable.switch_trcks,null)
        selectedButton.setTextColor(resources.getColor(R.color.textColor,null))
        button1.background = null
        button1.setTextColor(resources.getColor(R.color.light_blue_900,null))
        button2.background = null
        button2.setTextColor(resources.getColor(R.color.light_blue_900,null))
        button3.background = null
        button3.setTextColor(resources.getColor(R.color.light_blue_900,null))
        button4.background = null
        button4.setTextColor(resources.getColor(R.color.light_blue_900,null))
    }

    private fun resetData() {
        dailyData.clear()
        weeklyData.clear()
        monthlyData.clear()
        yearlyData.clear()
        totalData.clear()

        dailyLabel.clear()
        weeklyLabel.clear()
        monthlyLabel.clear()
        yearlyLabel.clear()
        totalLabel.clear()
    }

}



//        val aaChartModel : AAChartModel = AAChartModel()
//            .chartType(AAChartType.Area)
//            .title("title")
//            .subtitle("subtitle")
//            .backgroundColor("#4b2b7f")
//            .dataLabelsEnabled(true)
//            .series(arrayOf(
//                AASeriesElement()
//                    .name("Tokyo")
//                    .data(arrayOf(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6)),
//                AASeriesElement()
//                    .name("NewYork")
//                    .data(arrayOf(0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5)),
//                AASeriesElement()
//                    .name("London")
//                    .data(arrayOf(0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0)),
//                AASeriesElement()
//                    .name("Berlin")
//                    .data(arrayOf(3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8))
//            ))


//        val lineChart = view.findViewById<LineChart>(R.id.myChart)
//        val xValue = ArrayList<String>()
//        xValue.add("1/1")
//        xValue.add("1/2")
//        xValue.add("1/3")
//        xValue.add("1/4")
//        xValue.add("1/5")
//
//        val lineEntry = ArrayList<Entry>()
//        lineEntry.add(Entry(20f, 0F))
//        lineEntry.add(Entry(50f, 1F))
//        lineEntry.add(Entry(60f, 2F))
//        lineEntry.add(Entry(30f, 3F))
//        lineEntry.add(Entry(10f, 4F))
//
//        val lineDataset = LineDataSet(lineEntry, "First")
//        lineDataset.color = ContextCompat.getColor(requireContext(),R.color.purple_200)
//
//        val data = LineData(lineDataset)
//        lineChart.data = data
//        lineChart.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
//        lineChart.animateXY(3000, 3000)