package com.brightkey.nickfl.myutilities.fragments

import androidx.fragment.app.Fragment

/**
 * @author Nick Floussov
 * @version 1.0.1
 * @since 1.0.0
 * Date: 1/21/2017
 */
abstract class BaseFragment() : Fragment() {

    lateinit var mTag: FragmentScreen
    var exitListener: ExitFragmentListener? = null

    //-------------------------------
    interface ExitFragmentListener {
        fun onFragmentExit()
    }
}
