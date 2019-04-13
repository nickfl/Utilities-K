package com.brightkey.nickfl.helpers

import com.brightkey.nickfl.myutilities.R

/**
 * @author Nick Floussov
 * @version 1.0.1
 * @since 1.0.0
 * Date: 1/24/2017
 */
enum class ScreenAnimation {

    ENTER_FROM_RIGHT(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right),
    ENTER_FROM_LEFT(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left),
    ENTER_FROM_BOTTOM(R.anim.enter_from_bottom, R.anim.exit_to_top, R.anim.enter_from_top, R.anim.exit_to_bottom);

    var resIdEnter: Int = 0
        private set
    var resIdExit: Int = 0
        private set
    var resIdPopEnter: Int = 0
    var resIdPopExit: Int = 0

    private constructor(enter: Int, exit: Int, popEnter: Int, popExit: Int) {
        this.resIdEnter = enter
        this.resIdExit = exit
        this.resIdPopEnter = popEnter
        this.resIdPopExit = popExit
    }

    private constructor(enter: Int, exit: Int) {
        this.resIdEnter = enter
        this.resIdExit = exit
    }

}
