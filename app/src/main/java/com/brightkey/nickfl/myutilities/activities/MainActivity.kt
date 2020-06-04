package com.brightkey.nickfl.myutilities.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigator
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.fragments.*
import com.brightkey.nickfl.myutilities.helpers.*
import com.brightkey.nickfl.myutilities.models.DashboardModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.*
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener, DashboardFragment.OnDashboardInteractionListener, DatePickerDialog.OnDateSetListener, BaseFragment.ExitFragmentListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }

//    private val appBarConfiguration by lazy {
//        AppBarConfiguration(
//            setOf(
//                R.id.chartFragment,
//                R.id.importFragment,
//                R.id.exportFragment
//            ), drawer_layout
//        )
//    }

    private var currentModelItem: DashboardModel? = null
    private var currentChartType: String = ""

    private var buttonsGap = 0.0f
    private var fabMain: FloatingActionButton? = null
    private var fabWater: FloatingActionButton? = null
    private var fabHeat: FloatingActionButton? = null
    private var fabHydro: FloatingActionButton? = null
    private var fabPhone: FloatingActionButton? = null
    private var exportFragment: ExportFragment? = null
    private var importFragment: ImportFragment? = null
    private var currentPeriod: String = ""  // default - Current Year

    // all buttons are the same
    private var originY = -1.0f
    private var buttonsVisible = false

    private var menuId = R.menu.main

    //region Activity Overrides
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
        currentChartType = Constants.HydroType
        currentModelItem = findModelItem(currentChartType)
    }

    //device back button pressed
    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
            return
        }

        val currentDestId = navController.currentDestination?.id ?: R.id.dashboardFragment
        if (currentDestId != R.id.dashboardFragment) {
            returnToDashboard()
            return
        }
        super.onBackPressed()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            if (it.hasVisibleItems()) {
                if (it.getItem(0)?.itemId == R.id.action_period) {
                    it.getItem(1).title = currentPeriod
                }
                if (it.getItem(0)?.itemId == R.id.chart_choice) {
                    it.getItem(1).title = currentModelItem?.utilityType ?: resources.getString(R.string.title_hydro)
                }
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
                navigateTo(R.id.periodFragment)
                return true
            }
            R.id.action_close -> {
                returnToDashboard()
            }
            R.id.chart_choice -> {
                val bundle = Bundle()
                bundle.putString("type", currentChartType)
                navigateTo(R.id.utilityFragment, bundle)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // Items in Drawer
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_statistics, R.id.nav_manage -> {
                setCurrentChart()
            }
            R.id.nav_export -> {
                navigateTo(R.id.exportFragment)
            }
            R.id.nav_import -> {
                navigateTo(R.id.importFragment)
            }
            R.id.nav_clean -> {
                RealmHelper.shared().cleanAllUtilityBills()
                // back to the dashboard
                returnToDashboard()
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    // endregion

    //region Helpers
    private fun topFragment(): BaseFragment {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        return navHostFragment?.childFragmentManager?.getFragments()?.get(0) as BaseFragment
    }

    private fun updateTitle(title: CharSequence) {
        toolbar.title = title
    }

    private fun navigateTo(destination: Int, bundle: Bundle? = null) {
        navController.popBackStack()
        navController.navigate(destination, bundle)
    }

    private fun navigateWithAction(action: Int, extras: FragmentNavigator.Extras) {
        navController.popBackStack(R.id.nav_host_fragment, false)
        navController.navigate(action, null, null, extras)
    }

    private fun setCurrentChart() {
        currentModelItem = findModelItem(currentChartType)
        currentModelItem?.let {
            val bundle = Bundle()
            bundle.putString("type", it.utilityIcon)
            bundle.putString("color", it.vendorColor)
            navigateTo(R.id.chartFragment, bundle)
            setChartTitle(it)
        }
    }

    private fun setChartTitle(model: DashboardModel?) {
        model?.let {
            var type = it.utilityType
            if (PeriodManager.shared.period == Periods.Current) {
                type = "Current $type"
            }
            val total = it.totalPaid
            val title: String = type + String.format(" ( $%.2f )", total)
            updateTitle(title)
        }
    }

    private fun findModelItem(forUtility: String): DashboardModel? {
        val models = DashboardModel.convertToDash(MyUtilitiesApplication.config!!)
        for (one in models) {
            if (one.utilityIcon == forUtility) {
                return one
            }
        }
        return null
    }

    private fun toggleFABs(hide: Boolean) {
        val alpha: Float = if (hide) 0.0f else 1.0f
        fabMain?.alpha = alpha
        fabWater?.alpha = alpha
        fabHeat?.alpha = alpha
        fabHydro?.alpha = alpha
        fabPhone?.alpha = alpha
    }

    private fun returnToDashboard() {
        navigateTo(R.id.dashboardFragment)
        setCustomOptions(R.menu.main)
    }

    private fun setupDrawer(toolbar: Toolbar) {
//        NavigationUI.setupActionBarWithNavController(this, navController, drawer_layout)
//        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(this)
        toggle.syncState()
    }

    private fun updateDate(header: View) {
        val date = header.findViewById<TextView>(R.id.textHeaderViewDate)
        when (PeriodManager.shared.period) {
            Periods.Current -> {
                val cal = GregorianCalendar()
                date.text = DateFormatters.dateStringFromCalendar(cal)
            }
            Periods.Year2019 -> { date.text = getString(R.string.Year2019) }
            Periods.Year2018 -> { date.text = getString(R.string.Year2018) }
        }
    }

    private fun updateTotal(header: View) {
        val total = header.findViewById<TextView>(R.id.textViewTotalNow)
        val totalPay = RealmHelper.shared().totalPayment()
        total.text = String.format(getString(R.string.nav_header_total) + "%.2f", totalPay)
    }

    private fun setupHeader() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navigationView.setupWithNavController(navController)

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
        navController.addOnDestinationChangedListener { _, destination, _ ->
            //show FAB for dashboard only!
            toggleFABs(destination.id != R.id.dashboardFragment)
        }
    }

    private fun setupFABs() {
        fabWater = findViewById<View>(R.id.fab_water) as FloatingActionButton
        fabWater?.setOnClickListener {
            fabAction()
            navigateTo(R.id.waterFragment)
        }
        fabHeat = findViewById<View>(R.id.fab_heat) as FloatingActionButton
        fabHeat?.setOnClickListener {
            fabAction()
            navigateTo(R.id.heatFragment)
        }
        fabHydro = findViewById<View>(R.id.fab_hydro) as FloatingActionButton
        fabHydro?.setOnClickListener {
            fabAction()
            navigateTo(R.id.hydroFragment)
        }
        fabPhone = findViewById<View>(R.id.fab_phone) as FloatingActionButton
        fabPhone?.setOnClickListener {
            fabAction()
            navigateTo(R.id.phoneFragment)
        }

        fabMain = findViewById<View>(R.id.fab) as FloatingActionButton
        fabMain?.setOnClickListener {
            fabAction()
        }
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
        val res = if (buttonsVisible) R.drawable.ic_remove_black_24dp
                        else R.drawable.ic_add_black_24dp
        fabMain?.setImageResource(res)
    }

    fun setCustomOptions(rMenu: Int, title: String? = null) {
        invalidateOptionsMenu()
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.i("[$line] setCustomOptions()")
        menuId = rMenu
        title?.let {
            updateTitle(it)
        }
    }
    //endregion

    //region Show Fragment
    private fun showFragmentFrom(right: Boolean, screen: FragmentScreen) {
        if (screen != FragmentScreen.DASHBOARD_FRAGMENT) {
            if (buttonsVisible) {
                fabAction()
            }
        }
        val direction = if (right) ScreenAnimation.ENTER_FROM_RIGHT else ScreenAnimation.ENTER_FROM_LEFT
//        nManager?.replaceScreenTo(screen, direction)
    }

//    private fun showFragmentFromRight(screen: FragmentScreen) {
//        showFragmentFrom(true, screen)
//    }

//    private fun showFragmentFromLeft(screen: FragmentScreen) {
//        showFragmentFrom(false, screen)
//    }
    // endregion

    //region Fragments methods
    fun backToCharts(chart: Int) {
        when (chart) {
            R.id.radioButtonHydro -> {
                currentChartType = Constants.HydroType
            }
            R.id.radioButtonGas -> {
                currentChartType = Constants.HeatType
            }
            R.id.radioButtonWater -> {
                currentChartType = Constants.WaterType
            }
            R.id.radioButtonPhone -> {
                currentChartType = Constants.PhoneType
            }
        }
        setCurrentChart()
    }

    fun editFragment(screen: FragmentScreen, index: Int) {
        val bundle = Bundle()
        bundle.putInt("index", index)
        bundle.putBoolean("edit", true)
        var destId = R.id.dashboardFragment
        when (screen) {
            FragmentScreen.NO_SCREEN -> { destId = R.id.dashboardFragment }
            FragmentScreen.WATER_FRAGMENT -> { destId = R.id.waterFragment }
            FragmentScreen.HYDRO_FRAGMENT -> { destId = R.id.hydroFragment }
            FragmentScreen.HEAT_FRAGMENT -> { destId = R.id.heatFragment }
            FragmentScreen.PHONE_FRAGMENT -> { destId = R.id.phoneFragment }
        }
        navController.navigate(destId, bundle)
    }

    fun changePeriod(period: String) {
        currentPeriod = period
//        dashFragment?.dataUpdated()
    }
    // endregion

    // OnDashboardInteractionListener
    override fun onDashboardInteraction(itemId: String) {
        val bundle = Bundle()
        bundle.putString("title", itemId)
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.i("[$line] onDashboardInteraction.itemId: $itemId")
        navigateTo(R.id.timeDetailsFragment, bundle)
    }

    // OnDateSetListener
    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val cal = GregorianCalendar(year, month, day)
        topFragment().currentDateView?.text = DateFormatters.dateStringFromCalendar(cal)
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
            importFragment?.importRecordsFromDevice()
            return
        }
        if (requestCode == Constants.REQUEST_WRITE_PERMISSIONS) {
            exportFragment?.exportRecord()
            return
        }
    }
    // endregion
}