package com.brightkey.nickfl.myutilities.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.activities.MainActivity
import com.brightkey.nickfl.myutilities.helpers.Constants
import com.brightkey.nickfl.myutilities.helpers.RealmHelper
import com.brightkey.nickfl.myutilities.models.DashboardModel
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

class ChartFragment : BaseFragment() {

//    protected var tfLight: Typeface? = null
    private val allMonths = arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")

    private val maxBars = 12
    private var chart: HorizontalBarChart? = null

    private var chartValues: FloatArray = FloatArray(12)
    private var chartColor: Int = Color.GREEN// use this.utilityList[index].vendorColor
    private var chartType: String = Constants.HydroType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf")
        chartType = arguments?.getString("type") ?: Constants.HydroType
        val color = arguments?.getString("color") ?: "#F58233"
        chartColor = Color.parseColor(color)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        cleanup()
        loadData()
        setup(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onResume()")
        (activity as MainActivity).setCustomOptions(R.menu.charts)
        cleanup()

        setData(chartValues)
        chart?.setFitBars(true)
        chart?.invalidate()
    }

    fun configureChart(model: DashboardModel?) {
        chartType = model?.utilityIcon ?: Constants.HydroType
        chartColor = Color.parseColor(model?.vendorColor ?: "#F58233")
    }

    //region start Helpers
    private fun cleanup() {
    }

    private fun setup(view: View) {
        chart = view.findViewById(R.id.chart)

        // disable interaction
        chart?.setTouchEnabled(false)

        chart?.setDrawBarShadow(false)
        chart?.setDrawValueAboveBar(true)
        chart?.description?.isEnabled = false

        // months
        chart?.setMaxVisibleValueCount(maxBars + 1)
        chart?.xAxis?.valueFormatter = YAxisFormatter()

        // scaling can now only be done on x- and y-axis separately
        chart?.setPinchZoom(false)

        chart?.setDrawGridBackground(false)

        val xl = chart?.xAxis
        xl?.position = XAxis.XAxisPosition.BOTTOM
//        xl?.typeface = setTypeface(tfLight)
        xl?.setDrawAxisLine(true)
        xl?.setDrawGridLines(false)

        val yl = chart?.axisLeft
//        yl?.typeface = tfLight
        yl?.setDrawAxisLine(true)
        yl?.setDrawGridLines(true)
        yl?.axisMinimum = 0f // this replaces setStartAtZero(true)

        val yr = chart?.axisRight
//        yr?.typeface = tfLight
        yr?.setDrawAxisLine(true)
        yr?.setDrawGridLines(false)
        yr?.axisMinimum = 0f // this replaces setStartAtZero(true)

        chart?.setFitBars(true)
        chart?.animateY(1500)
    }

    private fun loadData() {
        val utils = RealmHelper.utilitiesForType(chartType)
        chartValues = FloatArray(12)
        for (one in utils) {
            val mnth = getMonthFor(one.datePaid)
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

        val chartData = chart?.data
        if (chartData != null && chartData.dataSetCount > 0) {
            set1 = chartData.getDataSetByIndex(0) as BarDataSet
            set1.values = entries
            chartData.notifyDataChanged()
            chart?.notifyDataSetChanged()
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
            chart?.data = data
            chart?.legend?.isEnabled = false
        }
    }

    companion object {

        fun newInstance(): ChartFragment {
            val fragment = ChartFragment()
            fragment.mTag = FragmentScreen.CHART_FRAGMENT
            return fragment
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
