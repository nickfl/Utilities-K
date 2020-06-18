package com.brightkey.nickfl.myutilities.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.brightkey.nickfl.myutilities.MyUtilitiesApplication
import com.brightkey.nickfl.myutilities.R
import com.brightkey.nickfl.myutilities.adapters.ExitFragmentListener
import com.brightkey.nickfl.myutilities.fragments.*
import com.brightkey.nickfl.myutilities.helpers.*
import com.brightkey.nickfl.myutilities.models.ChartModel
import com.brightkey.nickfl.myutilities.models.DashboardModel
import com.brightkey.nickfl.myutilities.models.UtilityEditModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.*
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        DrawerLayout.DrawerListener,
        DashboardFragment.OnDashboardInteractionListener,
        DatePickerDialog.OnDateSetListener,
        ExitFragmentListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

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
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT)
            return
        }

//        val currentDestId = navController.currentDestination?.id ?: R.id.dashboardFragment
//        if (currentDestId != R.id.dashboardFragment) {
//            returnToDashboard()
//            return
//        }
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
                val action = DashboardFragmentDirections.actionDashboardFragmentToPeriodFragment(currentPeriod)
                navController.navigate(action)
                return true
            }
            R.id.chart_choice -> {
                val action = ChartFragmentDirections.actionChartFragmentToUtilityFragment(currentChartType)
                navController.navigate(action)
                return true
            }
            R.id.action_close -> {
                returnToDashboard()
                return true
            }
            R.id.action_delete_bill -> {
                //alert to confirm Yes-No
                showConfirmationDelete()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // Items in Drawer - OnNavigationItemSelectedListener
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
        drawer.closeDrawer(Gravity.LEFT)
        return true
    }

    // endregion

    //region Helpers
    private fun topFragment(): BaseEditFragment {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        return navHostFragment?.childFragmentManager?.fragments?.get(0) as BaseEditFragment
    }

    private fun updateTitle(title: CharSequence) {
        toolbar.title = title
    }

    private fun navigateTo(destination: Int, bundle: Bundle? = null) {
        navController.navigate(destination, bundle)
    }

    private fun setCurrentChart(fromDrawer: Boolean = true) {
        currentModelItem = findModelItem(currentChartType)
        currentModelItem?.let {
            val model = ChartModel(it.utilityIcon, it.vendorColor)
            val action = if (fromDrawer) {
                DashboardFragmentDirections.actionDashboardFragmentToChartFragment(model)
            } else {
                UtilityFragmentDirections.actionUtilityFragmentToChartFragment(model)
            }
            navController.navigate(action)
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
        //use Global action
        val action = DashboardFragmentDirections.actionGlobalToDashboardFragment()
        navController.navigate(action)
        setCustomOptions(R.menu.main, getString(R.string.title_myutility))
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
            val action = DashboardFragmentDirections.actionDashboardFragmentToWaterFragment(UtilityEditModel())
            navController.navigate(action)
        }
        fabHeat = findViewById<View>(R.id.fab_heat) as FloatingActionButton
        fabHeat?.setOnClickListener {
            fabAction()
            val action = DashboardFragmentDirections.actionDashboardFragmentToHeatFragment(UtilityEditModel())
            navController.navigate(action)
        }
        fabHydro = findViewById<View>(R.id.fab_hydro) as FloatingActionButton
        fabHydro?.setOnClickListener {
            fabAction()
            val action = DashboardFragmentDirections.actionDashboardFragmentToHydroFragment(UtilityEditModel())
            navController.navigate(action)
        }
        fabPhone = findViewById<View>(R.id.fab_phone) as FloatingActionButton
        fabPhone?.setOnClickListener {
            fabAction()
            val action = DashboardFragmentDirections.actionDashboardFragmentToPhoneFragment(UtilityEditModel())
            navController.navigate(action)
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
        val res = if (buttonsVisible) R.drawable.ic_minus
                        else R.drawable.ic_plus
        fabMain?.setImageResource(res)
    }

    private fun showConfirmationDelete() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.myErrorDialog))
        builder.setMessage(R.string.sure_delete_bill)
                .setTitle(R.string.attention)
                .setIcon(R.drawable.warning)
                .setNeutralButton(R.string.Cancel, null)
                .setPositiveButton(R.string.action_delete_bill) {_, _ ->
                    val count = topFragment().removeBill()
                    if (count > 0) {
                        onBackPressed()
                    } else {
                        returnToDashboard()
                    }
                }

        val alert = builder.create()
        alert.show()
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

    //region Fragments methods
    fun backToCharts(chart: Int) {
        currentChartType = when (chart) {
            R.id.radioButtonHydro -> {
                Constants.HydroType
            }
            R.id.radioButtonGas -> {
                Constants.HeatType
            }
            R.id.radioButtonWater -> {
                Constants.WaterType
            }
            R.id.radioButtonPhone -> {
                Constants.PhoneType
            }
            else -> return
        }
        setCurrentChart(false)
    }

    fun editFragment(screen: FragmentScreen, index: Int) {
        val model = UtilityEditModel(index, true)
        val action = when (screen) {
            FragmentScreen.WATER_FRAGMENT -> {
                TimeDetailsFragmentDirections.actionTimeDetailsFragmentToWaterFragment(model)
            }
            FragmentScreen.HYDRO_FRAGMENT -> {
                TimeDetailsFragmentDirections.actionTimeDetailsFragmentToHydroFragment(model)
            }
            FragmentScreen.HEAT_FRAGMENT -> {
                TimeDetailsFragmentDirections.actionTimeDetailsFragmentToHeatFragment(model)
            }
            FragmentScreen.PHONE_FRAGMENT -> {
                TimeDetailsFragmentDirections.actionTimeDetailsFragmentToPhoneFragment(model)
            }
            else -> return
        }
        action.let {
            navController.navigate(it)
        }
    }

    fun changePeriod(period: String) {
        currentPeriod = period
    }
    // endregion

    // OnDashboardInteractionListener
    // `itemId` - utilityType, default - HydroType
    override fun onDashboardInteraction(itemId: String) {
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.i("[$line] onDashboardInteraction.itemId: $itemId")
        val action = DashboardFragmentDirections.actionDashboardFragmentToTimeDetailsFragment(itemId)
        navController.navigate(action)
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
            RealmStorageRecords.importRecords()
            onFragmentExit()
            return
        }
        if (requestCode == Constants.REQUEST_WRITE_PERMISSIONS) {
            RealmStorageRecords.exportRecords()
            onFragmentExit()
            return
        }
    }
    // endregion
}