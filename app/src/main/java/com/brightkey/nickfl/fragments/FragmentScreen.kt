package com.brightkey.nickfl.fragments

/**
 * @author Nick Floussov
 * @version 1.0.1
 * @since 1.0.0
 * Date: 2/15/2017
 */
enum class FragmentScreen {
    NO_SCREEN,

    //MainActivity's fragments
    DASHBOARD_FRAGMENT,
    EXPORT_FRAGMENT, IMPORT_FRAGMENT, PERIOD_FRAGMENT,

    //Profile fragments
    WATER_FRAGMENT,
    HYDRO_FRAGMENT, HEAT_FRAGMENT, PHONE_FRAGMENT,

    //Time Line Details
    TIMEDETAILS_FRAGMENT;


    companion object {

        val mainNavigationFragmentScreens: Array<FragmentScreen>
            get() = arrayOf(NO_SCREEN, DASHBOARD_FRAGMENT, WATER_FRAGMENT, HYDRO_FRAGMENT, HEAT_FRAGMENT, PHONE_FRAGMENT, TIMEDETAILS_FRAGMENT)
    }
}