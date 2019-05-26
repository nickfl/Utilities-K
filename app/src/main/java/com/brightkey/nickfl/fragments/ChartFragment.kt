package com.brightkey.nickfl.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brightkey.nickfl.activities.MainActivity
import com.brightkey.nickfl.myutilities.R
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

    private var chart: HorizontalBarChart? = null
    private val values: FloatArray = floatArrayOf(10f, 20f, 50f, 10f, 60f, 20f, 50f,
            50f, 70f, 0f, 40f, 90f, 30f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chart, container, false)
        setup(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[$line] onResume()")
        activity?.invalidateOptionsMenu()
        (activity as MainActivity).setCustomOptions(R.menu.fragment)
        cleanup()

        setData(8, values)
        chart?.setFitBars(true)
        chart?.invalidate()
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
        chart?.getDescription()?.isEnabled = false

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart?.setMaxVisibleValueCount(12)
        chart?.xAxis?.valueFormatter = YAxisFormatter()

        // scaling can now only be done on x- and y-axis separately
        chart?.setPinchZoom(false)

        chart?.setDrawGridBackground(false)

        val xl = chart?.getXAxis()
        xl?.position = XAxis.XAxisPosition.BOTTOM
//        xl?.typeface = setTypeface(tfLight)
        xl?.setDrawAxisLine(true)
        xl?.setDrawGridLines(false)
//        xl?.granularity = 1f

        val yl = chart?.getAxisLeft()
//        yl?.typeface = tfLight
        yl?.setDrawAxisLine(true)
        yl?.setDrawGridLines(true)
        yl?.axisMinimum = 0f // this replaces setStartAtZero(true)

        val yr = chart?.getAxisRight()
//        yr?.typeface = tfLight
        yr?.setDrawAxisLine(true)
        yr?.setDrawGridLines(false)
        yr?.axisMinimum = 0f // this replaces setStartAtZero(true)

        chart?.setFitBars(true)
        chart?.animateY(2500)
    }

    private fun setData(months: Int, values: FloatArray) {

        val barWidth = 0.85f
        val entries = ArrayList<BarEntry>()

        for (index in 0 until months) {
            entries.add(BarEntry(index*1f, values[index]))
        }

        val set1: BarDataSet

        val data = chart?.getData()
        if (data != null && data.dataSetCount > 0) {
            set1 = chart?.getData()?.getDataSetByIndex(0) as BarDataSet
            set1.values = entries
            data.notifyDataChanged()
            chart?.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(entries, "DataSet")
            set1.setDrawIcons(false)

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
//            data.setValueTypeface(tfLight)
            data.barWidth = barWidth
            chart?.setData(data)
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
        private val months = arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return months.getOrNull(value.toInt()) ?: value.toString()
        }
    }
}
