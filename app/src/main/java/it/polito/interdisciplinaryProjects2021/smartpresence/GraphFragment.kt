package it.polito.interdisciplinaryProjects2021.smartpresence

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class GraphFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aaChartView = view.findViewById<AAChartView>(R.id.aa_chart_view)

        val aaChartModel : AAChartModel = AAChartModel()
            .chartType(AAChartType.Area)
            .title("title")
            .subtitle("subtitle")
            .backgroundColor("#4b2b7f")
            .dataLabelsEnabled(true)
            .series(arrayOf(
                AASeriesElement()
                    .name("Tokyo")
                    .data(arrayOf(7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6)),
                AASeriesElement()
                    .name("NewYork")
                    .data(arrayOf(0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5)),
                AASeriesElement()
                    .name("London")
                    .data(arrayOf(0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0)),
                AASeriesElement()
                    .name("Berlin")
                    .data(arrayOf(3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8))
            ))

        aaChartView.aa_drawChartWithChartModel(aaChartModel)

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

    }

}