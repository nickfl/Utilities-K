package com.brightkey.nickfl.myutilities.helpers

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.view.View
import android.widget.FrameLayout

/**
 * @author Nick Floussov
 * @version 1.0.1
 * @since 1.0.0
 * Date: 12/05/2016
 */
class Geometry {

    interface AnimationFinished {
        fun finished()
    }

    fun convertPdsToPixel(pts: Float, activity: Activity): Int {
        val scale = activity.resources.displayMetrics.density
        return (pts * scale + 0.5f).toInt()
    }

    fun changeHeight(layout: FrameLayout, newH: Float) {
        val params = layout.layoutParams
        params.height = newH.toInt()
        layout.layoutParams = params
    }

    companion object {

        private var duration = 300

        fun moveButtonToY(yellow: View, newY: Float, aFinished: AnimationFinished?) {
            val animation1 = ObjectAnimator.ofFloat(yellow, "y", newY)
            animation1.duration = duration.toLong()
            animation1.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}

                override fun onAnimationEnd(animator: Animator) {
                    aFinished?.finished()
                }

                override fun onAnimationCancel(animator: Animator) {}

                override fun onAnimationRepeat(animator: Animator) {}
            })
            animation1.start()
        }
    }

}
