package com.brightkey.nickfl.myutilities.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.databinding.FragmentChartBinding
import com.brightkey.nickfl.myutilities.helpers.Constants
import com.brightkey.nickfl.myutilities.helpers.RealmHelper
import com.brightkey.nickfl.myutilities.models.ChartModel
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import timber.log.Timber
import java.util.*

class ChartFragment : Fragment() {

//    protected var tfLight: Typeface? = null
    private val allMonths = arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")

    private val maxBars = 12
    private var _chart: HorizontalBarChart? = null
    private val chart get() = _chart!!

    private var chartValues: FloatArray = FloatArray(12)
    private var chartColor: Int = Color.GREEN// use this.utilityList[index].vendorColor
    private var chartType: String = Constants.HydroType

    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf")
        val model = arguments?.getParcelable<ChartModel>("chartModel")
        chartType = Constants.HydroType
        var color = "#F58233"
        model?.let{
            chartType = it.type
            color = it.color
        }
        chartColor = Color.parseColor(color)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentChartBinding.inflate(inflater, container, false)
        cleanup()
        loadData()
        setup()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Fixing memory leak
        _chart = null
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onResume()")
        (activity as MainActivity).setCustomOptions(R.menu.charts)
        cleanup()

        setData(chartValues)
        _chart?.setFitBars(true)
        _chart?.invalidate()
    }

    //region start Helpers
    private fun cleanup() {
    }

    private fun setup() {
        _chart = binding.chart

        // disable interaction
        _chart?.setTouchEnabled(false)

        _chart?.setDrawBarShadow(false)
        _chart?.setDrawValueAboveBar(true)
        _chart?.description?.isEnabled = false

        // months
        _chart?.setMaxVisibleValueCount(maxBars + 1)
        _chart?.xAxis?.valueFormatter = YAxisFormatter()

        // scaling can now only be done on x- and y-axis separately
        _chart?.setPinchZoom(false)

        _chart?.setDrawGridBackground(false)

        val xl = chart.xAxis
        xl?.position = XAxis.XAxisPosition.BOTTOM
//        xl?.typeface = setTypeface(tfLight)
        xl?.setDrawAxisLine(true)
        xl?.setDrawGridLines(false)

        val yl = chart.axisLeft
//        yl?.typeface = tfLight
        yl?.setDrawAxisLine(true)
        yl?.setDrawGridLines(true)
        yl?.axisMinimum = 0f // this replaces setStartAtZero(true)

        val yr = chart.axisRight
//        yr?.typeface = tfLight
        yr?.setDrawAxisLine(true)
        yr?.setDrawGridLines(false)
        yr?.axisMinimum = 0f // this replaces setStartAtZero(true)

        _chart?.setFitBars(true)
        _chart?.animateY(1500)
    }

    private fun loadData() {
        val utils = RealmHelper.utilitiesForType(chartType)
        chartValues = FloatArray(12)
        for (one in utils) {
            val mnth = getMonthFor(one.billDate)
            val mnthIndex = allMonths.indexOf(mnth)
            val value = one.amountDue.toFloat() + chartValues[mnthIndex]
            chartValues[mnthIndex] = value
        }
    }

    private fun getMonthFor(date: Date?): String {
        val calendar = Calendar.getInstance()
        calendar.time = date ?: Date()
        val index = calendar.get(Calendar.MONTH)
        return allMonths[index]
    }

    private fun setData(values: FloatArray) {

        // bar thickness
        val barWidth = 0.85f
        val entries = ArrayList<BarEntry>()

        val count = minOf(maxBars, values.size)
        for (index in 0 until count) {
            entries.add(BarEntry(index*1f, values[index]))
        }

        val set1: BarDataSet

        val chartData = chart.data
        if (chartData != null && chartData.dataSetCount > 0) {
            set1 = chartData.getDataSetByIndex(0) as BarDataSet
            set1.values = entries
            chartData.notifyDataChanged()
            _chart?.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(entries, chartType)
            set1.setDrawIcons(false)
            set1.color = chartColor

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
//            data.setValueTypeface(tfLight)
            data.barWidth = barWidth
            _chart?.data = data
            _chart?.legend?.isEnabled = false
        }
    }
    //endregion

    class YAxisFormatter: ValueFormatter() {
        private val months: MutableList<String> = arrayListOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return months.getOrNull(value.toInt()) ?: value.toString()
        }
    }
}
