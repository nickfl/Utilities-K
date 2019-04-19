package com.brightkey.nickfl.helpers

import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import com.brightkey.nickfl.fragments.BaseFragment
import com.brightkey.nickfl.fragments.FragmentScreen
import timber.log.Timber
import java.util.*

/**
 * @author Nick Floussov
 * @version 1.0.1
 * @since 1.0.0
 * Date: 1/16/2017
 */
class NavigationManager(activity: AppCompatActivity, private val containerId: Int) {

    private var fragmentStack = ArrayList<BaseFragment>()
    private var mManager: FragmentManager? = null
    private var topFragment: BaseFragment? = null
    private var allFragments = HashMap<FragmentScreen, BaseFragment>()

    init {
        this.mManager = activity.supportFragmentManager
    }

    fun topFragment(): BaseFragment? {
        return if (fragmentStack.isEmpty()) {
            null
        } else fragmentStack[fragmentStack.size - 1]
    }

    //    public FragmentScreen topFragmentKey() {
    //        if (fragmentStack.isEmpty()) {
    //            return FragmentScreen.NO_SCREEN;
    //        }
    //        return fragmentStack.get(fragmentStack.size()-1).mTag;
    //    }

    fun storeFragment(fragment: BaseFragment, key: FragmentScreen) {
        this.allFragments.put(key, fragment)
    }

    //    public BaseFragment getScreen(FragmentScreen key) {
    //        return this.allFragments.get(key);
    //    }

    fun addScreen(key: FragmentScreen, screenAnimation: ScreenAnimation?) {
        val fr = this.allFragments[key]
        addFragment(fr, screenAnimation)
    }

    fun addFragment(fragment: BaseFragment?, screenAnimation: ScreenAnimation?) {
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[" + line + "] addFragment(" + fragment!!.javaClass.getSimpleName() + ")")

        val transaction = mManager!!.beginTransaction()
//        if (!ValidationUtils.isNull(screenAnimation)) {
//            int enter = screenAnimation.getResIdEnter();
//            int exit = screenAnimation.getResIdExit();
//            int popEnter = screenAnimation.getResIdPopEnter();
//            int popExit = screenAnimation.getResIdPopExit();
//            transaction.setCustomAnimations(enter, exit, popEnter, popExit);
//        }

        fragmentStack.add(fragment)
        transaction.add(this.containerId, fragment)
                .commitAllowingStateLoss()
        topFragment = fragment
    }

    fun replaceScreenTo(key: FragmentScreen, screenAnimation: ScreenAnimation) {
        val fr = this.allFragments[key]
        fr!!.doEdit = false
        replaceFragment(fr, screenAnimation)
    }

    fun editScreen(key: FragmentScreen, screenAnimation: ScreenAnimation, index: Int) {
        val fr = this.allFragments[key]
        fr!!.doEdit = true
        fr.editIndex = index
        replaceFragment(fr, screenAnimation)
    }

    fun replaceFragment(fragment: BaseFragment, screenAnimation: ScreenAnimation) {
        val line = Exception().stackTrace[0].lineNumber + 1
        Timber.w("[" + line + "] replaceFragment(" + fragment.javaClass.getSimpleName() + ")")

        val transaction = mManager!!.beginTransaction()
        if (!ValidationUtils.isNull(screenAnimation)) {
            val enter = screenAnimation.resIdEnter
            val exit = screenAnimation.resIdExit
            val popEnter = screenAnimation.resIdPopEnter
            val popExit = screenAnimation.resIdPopExit
            transaction.setCustomAnimations(enter, exit, popEnter, popExit)
        }

        fragmentStack.add(fragment)
        transaction.replace(this.containerId, fragment)
                .commitAllowingStateLoss()
        topFragment = fragment
    }

    fun isTopFragment(screen: FragmentScreen): Boolean {
        return topFragment!!.mTag == screen
    }

    companion object {

        private val TAG = "NavigationManager"
    }
    //    public void popFragment() {
    //        int size = fragmentStack.size();
    //        int line = new Exception().getStackTrace()[0].getLineNumber()+1;
    //        Timber.w("["+line+"] popFragment("+size+")");
    //
    //        if (size > 0) {
    //            FragmentTransaction transaction = mManager.beginTransaction();
    //            BaseFragment last = fragmentStack.get(size - 1);
    //            transaction.remove(last);
    //            transaction.commit();
    //            mManager.popBackStack();
    //            fragmentStack.remove(last);
    //            size = fragmentStack.size();
    //            topFragment = (size > 0) ? topFragment = fragmentStack.get(size - 1) : null;
    //        }
    //    }

    //region Spinner
    /*
    public void spinnerOn() {
        if (topFragment == null) {
            return;
        }
        if (topFragment.mSpinner != null) {
            topFragment.mSpinner.setVisibility(View.VISIBLE);
        }
    }
    public void spinnerOff() {
        if (topFragment == null) {
            return;
        }
        if (topFragment.mSpinner != null) {
            topFragment.mSpinner.setVisibility(View.INVISIBLE);
        }
    }
*/
    //endregion

}
