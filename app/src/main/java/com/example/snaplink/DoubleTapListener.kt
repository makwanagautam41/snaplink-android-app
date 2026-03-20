package com.example.snaplink

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * Detects a double-tap gesture on any [View].
 * Attach this to the [postImageContainer] FrameLayout (NOT to ViewPager2 directly)
 * so the VP2's own horizontal scroll events still pass through unaffected.
 *
 * Returning `false` from [onTouch] means we do NOT consume the event —
 * VP2 will still receive touches for swiping between images.
 */
class DoubleTapListener(
    context: Context,
    private val onDoubleTap: () -> Unit
) : View.OnTouchListener {

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            // Must return true here so GestureDetector tracks subsequent events
            override fun onDown(e: MotionEvent): Boolean = true

            override fun onDoubleTap(e: MotionEvent): Boolean {
                onDoubleTap()
                return true
            }
        }
    )

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        // Return false so the touch event also propagates to child views (VP2)
        return false
    }
}
