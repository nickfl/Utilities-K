package com.brightkey.nickfl.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import com.brightkey.nickfl.fragments.*
import com.brightkey.nickfl.fragments.FragmentScreen.*
import com.brightkey.nickfl.helpers.*
import com.brightkey.nickfl.models.DashboardModel
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.R
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener, DashboardFragment.OnDashboardInteractionListener, DatePickerDialog.OnDateSetListener, BaseFragment.ExitFragmentListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private var nManager: NavigationManager? = null
    private var model: List<DashboardModel>? = null
    private var currentModelItem: DashboardModel? = null

    private var buttonsGap = 0.0f
    private var fabMain: FloatingActionButton? = null
    private var fabWater: FloatingActionButton? = null
    private var fabHeat: FloatingActionButton? = null
    private var fabHydro: FloatingActionButton? = null
    private var fabPhone: FloatingActionButton? = null
    private var timeListFragment: TimeDetailsFragment? = null
    private var dashFragment: DashboardFragment? = null
    private var exportFragment: ExportFragment? = null
    private var importFragment: ImportFragment? = null
    private var periodFragment: PeriodFragment? = null
    private var chartFragment: ChartFragment? = null
    private var utilityFragment: UtilityFragment? = null
    private var currentPeriod: String = ""  // default - All Years

    // all buttons are the same
    private var originY = -1.0f
    private var buttonsVisible = false

    private var menuId = R.menu.main

    //region Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        currentPeriod = resources.getString(R.string.zero_period)

        setupFABs()
        setupNavigation()
        setupDrawer(toolbar)
        setupHeader()
        model = DashboardModel.convertToDash(MyUtilitiesApplication.config!!)
        currentModelItem = findModelItem(Constants.HydroType)
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
            return
        }
        if (!nManager!!.isTopFragment(DASHBOARD_FRAGMENT)) {
            returnToDashboard()
            return
        }
        super.onBackPressed()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null && menu.hasVisibleItems()) {
            if (menu.getItem(0)?.itemId == R.id.action_period) {
                menu.getItem(1).title = currentPeriod
            }
            if (menu.getItem(0)?.itemId == R.id.chart_choice) {
                menu.getItem(1).title = currentModelItem?.utilityType ?: resources.getString(R.string.title_hydro)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val inflater: MenuInflater = menuInflater
        inflater.inflate(menuId, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_period -> {
                setTitle(R.string.title_period)
                showFragmentFromRight(PERIOD_FRAGMENT)
                return true
            }
            R.id.action_close -> {
                returnToDashboard()
            }
            R.id.chart_choice -> {
                setTitle(R.string.title_utility_chart)
                showFragmentFromRight(UTILITY_FRAGMENT)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_statistics, R.id.nav_manage -> {
                setChartTitle(currentModelItem)
                showFragmentFromRight(CHART_FRAGMENT)
                chartFragment?.configureChart(currentModelItem)
            }
            R.id.nav_export -> {
                setTitle(R.string.drawer_export)
                nManager?.replaceScreenTo(EXPORT_FRAGMENT, ScreenAnimation.ENTER_FROM_RIGHT)
            }
            R.id.nav_import -> {
                setTitle(R.string.drawer_import)
                nManager?.replaceScreenTo(IMPORT_FRAGMENT, ScreenAnimation.ENTER_FROM_RIGHT)
            }
            R.id.nav_clean -> {
                ObjectBoxHelper.shared().cleanUtilityBox()
                // back to the dashboard
                returnToDashboard()
                dashFragment?.reloadView()
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
    // endregion

    //region Helpers
    private fun toggleFABs(hide: Boolean?) {
        val alpha: Float = if (hide != null && hide) 0.0f else 1.0f
        fabMain?.alpha = alpha
        fabWater?.alpha = alpha
        fabHeat?.alpha = alpha
        fabHydro?.alpha = alpha
        fabPhone?.alpha = alpha
    }

    private fun returnToDashboard() {
        menuId = R.menu.main
        invalidateOptionsMenu()
        //replace fragment
        setTitle(R.string.app_name)
        nManager?.replaceScreenTo(DASHBOARD_FRAGMENT, ScreenAnimation.ENTER_FROM_LEFT)
        toggleFABs(false)
    }

    private fun setupDrawer(toolbar: Toolbar) {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(this/*toggle*/)
        toggle.syncState()
    }

    private fun updateDate(header: View) {
        val date = header.findViewById<TextView>(R.id.textHeaderViewDate)
        when (PeriodManager.shared.period) {
            Periods.All -> {
                val cal = GregorianCalendar()
                date.text = DateFormatters.dateStringFromCalendar(cal)
            }
            Periods.Year2019 -> { date.text = "Year 2019" }
            Periods.Year2018 -> { date.text = "Year 2018" }
        }
    }
    private fun updateTotal(header: View) {
        val total = header.findViewById<TextView>(R.id.textViewTotalNow)
        val totalPay = ObjectBoxHelper.shared().totalPayment()
        total.text = String.format(getString(R.string.nav_header_total) + "%.2f", totalPay)
    }
    private fun setupHeader() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val header = navigationView.getHeaderView(0)
        updateDate(header)
        updateTotal(header)
    }

    private fun updateHeader() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val header = navigationView.getHeaderView(0)
        updateDate(header)
        updateTotal(header)
    }

    private fun setupNavigation() {
        nManager = NavigationManager(this, R.id.fragment_container)

        //keep all fragments for this activity
        dashFragment = DashboardFragment.newInstance()
        nManager?.storeFragment(dashFragment!!, DASHBOARD_FRAGMENT)
        val hydroFragment = HydroFragment.newInstance()
        nManager?.storeFragment(hydroFragment, HYDRO_FRAGMENT)
        val heatFragment = HeatFragment.newInstance()
        nManager?.storeFragment(heatFragment, HEAT_FRAGMENT)
        val waterFragment = WaterFragment.newInstance()
        nManager?.storeFragment(waterFragment, WATER_FRAGMENT)
        val phoneFragment = PhoneFragment.newInstance()
        nManager?.storeFragment(phoneFragment, PHONE_FRAGMENT)
        timeListFragment = TimeDetailsFragment.newInstance()
        nManager?.storeFragment(timeListFragment!!, TIMEDETAILS_FRAGMENT)
        exportFragment = ExportFragment.newInstance()
        nManager?.storeFragment(exportFragment!!, EXPORT_FRAGMENT)
        importFragment = ImportFragment.newInstance()
        nManager?.storeFragment(importFragment!!, IMPORT_FRAGMENT)
        periodFragment = PeriodFragment.newInstance()
        nManager?.storeFragment(periodFragment!!, PERIOD_FRAGMENT)
        chartFragment = ChartFragment.newInstance()
        nManager?.storeFragment(chartFragment!!, CHART_FRAGMENT)
        utilityFragment = UtilityFragment.newInstance()
        nManager?.storeFragment(utilityFragment!!, UTILITY_FRAGMENT)

        // Create a first Fragment to be placed in the activity layout
        nManager?.addScreen(DASHBOARD_FRAGMENT, null)
    }

    private fun setupFABs() {
        fabWater = findViewById<View>(R.id.fab_water) as FloatingActionButton
        fabWater?.setOnClickListener {
            //replace fragment
            fabAction()
            setTitle(R.string.title_water)
            showFragmentFromRight(WATER_FRAGMENT)
        }
        fabHeat = findViewById<View>(R.id.fab_heat) as FloatingActionButton
        fabHeat?.setOnClickListener {
            fabAction()
            //replace fragment
            setTitle(R.string.title_heat)
            showFragmentFromRight(HEAT_FRAGMENT)
        }
        fabHydro = findViewById<View>(R.id.fab_hydro) as FloatingActionButton
        fabHydro?.setOnClickListener {
            fabAction()
            //replace fragment
            setTitle(R.string.title_hydro)
            showFragmentFromRight(HYDRO_FRAGMENT)
        }
        fabPhone = findViewById<View>(R.id.fab_phone) as FloatingActionButton
        fabPhone?.setOnClickListener {
            fabAction()
            //replace fragment
            setTitle(R.string.title_phone)
            showFragmentFromRight(PHONE_FRAGMENT)
        }

        fabMain = findViewById<View>(R.id.fab) as FloatingActionButton
        fabMain?.setOnClickListener { fabAction() }
    }

    private fun fabAction() {
        if (this.originY < 0.0f) {
            this.originY = this.fabWater!!.y
            val originHeight = this.fabWater!!.height.toFloat()
            this.buttonsGap = 1.5f * originHeight
        }
        var phoneY = originY
        var waterY = originY
        var hydroY = originY
        var heatY = originY
        if (!buttonsVisible) {
            phoneY = originY - buttonsGap
            waterY = originY - 2 * buttonsGap
            heatY = originY - 3 * buttonsGap
            hydroY = originY - 4 * buttonsGap
        }
        buttonsVisible = !buttonsVisible
        Geometry.moveButtonToY(fabWater!!, waterY, null)
        Geometry.moveButtonToY(fabHeat!!, heatY, null)
        Geometry.moveButtonToY(fabHydro!!, hydroY, null)
        Geometry.moveButtonToY(fabPhone!!, phoneY, null)
    }

    fun setCustomOptions(rMenu: Int) {
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.i("[$line] setCustomOptions()")
        menuId = rMenu
    }
    //endregion

    //region Show Fragment
    private fun showFragmentFrom(right: Boolean, screen: FragmentScreen) {
        if (screen != DASHBOARD_FRAGMENT) {
            toggleFABs(true)
            if (buttonsVisible) {
                fabAction()
            }
        }
        val direction = if (right) ScreenAnimation.ENTER_FROM_RIGHT else ScreenAnimation.ENTER_FROM_LEFT
        nManager?.replaceScreenTo(screen, direction)
    }

    private fun showFragmentFromRight(screen: FragmentScreen) {
        showFragmentFrom(true, screen)
    }

    private fun showFragmentFromLeft(screen: FragmentScreen) {
        showFragmentFrom(false, screen)
    }
    // endregion

    //region Fragments methods
    fun backToCharts(chart: Int) {
        var charType = ""
        when (chart) {
            R.id.radioButtonHydro -> {
                charType = Constants.HydroType
            }
            R.id.radioButtonGas -> {
                charType = Constants.HeatType
            }
            R.id.radioButtonWater -> {
                charType = Constants.WaterType
            }
            R.id.radioButtonPhone -> {
                charType = Constants.PhoneType
            }
        }
        currentModelItem = findModelItem(charType)
        setChartTitle(currentModelItem)
        showFragmentFromLeft(CHART_FRAGMENT)
        chartFragment?.configureChart(currentModelItem)
    }

    fun editFragment(screen: FragmentScreen, index: Int) {
        nManager?.editScreen(screen, ScreenAnimation.ENTER_FROM_RIGHT, index)
    }

    fun changePeriod(period: String) {
        currentPeriod = period
        dashFragment?.adapter?.notifyDataSetChanged()
    }
    // endregion

    private fun setChartTitle(model: DashboardModel?) {
        val type = currentModelItem?.utilityType ?: Constants.HydroType
        val total = currentModelItem?.totalPaid ?: 0.0
        val title: String = type + String.format(" ( $%.2f )", total)
        setTitle(title)
    }
    private fun findModelItem(forUtility: String): DashboardModel? {
        if (model != null) {
            for (one in model!!) {
                if (one.utilityIcon == forUtility) {
                    return one
                }
            }
        }
        return null
    }

    // OnDashboardInteractionListener
    override fun onDashboardInteraction(itemId: String) {

        val title = MyUtilitiesApplication.getConfigEntityForType(itemId)?.utilityVendorName
        setTitle(title)
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.i("[$line] onDashboardInteraction.title: $title")
        timeListFragment?.setDetailsType(itemId)
        showFragmentFromRight(TIMEDETAILS_FRAGMENT)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val cal = GregorianCalendar(year, month, day)
        nManager?.topFragment()?.currentDateView?.text = DateFormatters.dateStringFromCalendar(cal)
    }

    // ExitFragmentListener
    override fun onFragmentExit() {
        returnToDashboard()
    }

    //DrawerListener
    override fun onDrawerOpened(drawerView: View) {
        updateHeader()
    }

    //region Override 2
    override fun onDrawerClosed(drawerView: View) {}
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
    override fun onDrawerStateChanged(newState: Int) {}

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (grantResults[0] != PERMISSION_GRANTED) {
            return
        }
        if (requestCode == Constants.REQUEST_READ_PERMISSIONS) {
            importFragment?.importRecord()
            return
        }
        if (requestCode == Constants.REQUEST_WRITE_PERMISSIONS) {
            exportFragment?.exportRecord()
            return
        }
    }
    // endregion
}
