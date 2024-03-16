package com.example.ecgwidgetsviewer

import android.os.Handler
import android.os.Looper
import android.util.Log


class Obtain(private val widget: GraphWidget, private val period: Long) {
    val TAG = Obtain::class.java.simpleName
    private var cycles = 0
    private var counter = 1
    private val mHandler: Handler  = Handler(Looper.getMainLooper())
    private var mIsPeriodicalActionActive = false

    fun setState(cycles: Int) {
        this.cycles = cycles
    }

    fun start() {
        Log.d(TAG, "start")
        mHandler.removeCallbacksAndMessages(null)
        mIsPeriodicalActionActive = true
        mHandler.post(periodicalRunnable)
    }

    fun redraw(counter: Int) {
        widget.update(counter)
    }

    private fun callbackFunction() {
        redraw(counter++)
        if (counter >= cycles) {
            counter = 1
        }
    }

    fun stop() {
        Log.d(TAG, "stop")
        mIsPeriodicalActionActive = false
        mHandler.removeCallbacks(periodicalRunnable)
    }

    private val periodicalRunnable: Runnable = object : Runnable {
        override fun run() {
            // Perform the periodical action here
            // For example, update UI, make API calls, etc.
            if (mIsPeriodicalActionActive) {
                callbackFunction()
                mHandler.postDelayed(this, period)
            }
        }
    }
}

