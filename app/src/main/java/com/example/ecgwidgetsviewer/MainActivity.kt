package com.example.ecgwidgetsviewer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.UUID


class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.java.getSimpleName()
    private var mFab: FloatingActionButton? = null
    private var mScrollView: ScrollView? = null
    private var mContainerLayout: LinearLayout? = null
    private var onBackPressedCallback: OnBackPressedCallback? = null
    private val mHandler = Handler(Looper.getMainLooper())
    private val utils_ = Utils()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mScrollView = findViewById(R.id.verticalScrollView)
        mContainerLayout = findViewById(R.id.containerLayout)
        mFab = findViewById(R.id.fab)
        mFab?.setOnClickListener(View.OnClickListener { v: View? ->
            mHandler.post { addCard() }
        })
        createAndAddBackDispatcher()
    }

    private fun createAndAddBackDispatcher() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle back button press here
                // For example, you can navigate back or perform any desired action
                // You can also call isEnabled() to check if callback is enabled or not
                Log.d(TAG, "MainActivity.handleOnBackPressed")
                confirmExitApp()
            }
        }
        // Adding the callback to the OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback!!)
    }

    private fun confirmExitApp() {
        CustomAlertDialogBox(this@MainActivity,
            getString(R.string.exit_app),
            getString(R.string.are_you_sure),
            getString(R.string.cancel),
            getString(R.string.ok),
            object : IActionResult {
                override fun onSuccess() {
                    stopAll()
                    onBackPressedCallback!!.remove()
                    finish()
                }

                override fun onFailed() {}
            })
    }

    private fun stopAll() {
        val childCount = cardsNumber
        if (childCount == 0) {
            return
        }
        for (i in 0 until childCount) {
            val view = mContainerLayout!!.getChildAt(i)
            if (view is CardView) {
                val widget: GraphWidget = view.findViewById(R.id.painting_view)
                widget.stop()
            }
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        // Remove the callback when fragment is destroyed
        if (onBackPressedCallback != null) {
            onBackPressedCallback!!.remove()
        }
        Log.d(TAG, "onDestroy!")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop!")
    }

    override fun onPause() {
        super.onPause()
        //  --- some code --
        Log.d(TAG, "onPause!")
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume!")
    }

    private fun addCard() {
        Log.d(TAG, "addCard")
        val inflater = LayoutInflater.from(this@MainActivity)
        val cardView = createWidget(inflater)
        //  Add CardView to the container
        mContainerLayout!!.addView(cardView)
        scrollToViewTop(cardView)
    }

    private fun scrollToViewTop(view: View?) {
        if (view == null) {
            return
        }
        val heightInDp = 8
        val heightInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            heightInDp.toFloat(), getResources().displayMetrics
        ).toInt()
        mScrollView!!.post {
            mScrollView!!.smoothScrollTo(
                0,
                view.top - heightInPixels
            )
        }
    }

    private fun createWidget(inflater: LayoutInflater): CardView {
        val cardView =
            inflater.inflate(R.layout.card_view_layout, mContainerLayout, false) as CardView
        val mUuid = UUID.randomUUID().toString()
        cardView.tag = mUuid
        val title = cardView.findViewById<TextView>(R.id.title_widget)
        title.text = "ECG #" + (cardsNumber + 1)
        val paintingView: GraphWidget = cardView.findViewById(R.id.painting_view)
        val mode: GraphMode =
            if (cardsNumber % 2 == 0) GraphMode.overlay else GraphMode.flowing
        paintingView.setMode(utils_.randomInRange(128, 320), mode)
        val switchButton = cardView.findViewById<SwitchCompat>(R.id.switch_button)

        // Set an OnCheckedChangeListener
        switchButton.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            // Perform actions when the switch state changes
            if (isChecked) {
                // Switch is ON
                switchButton.text = getString(R.string.flowing)
                paintingView.setMode(GraphMode.flowing)
                Log.d(TAG, "ON")
            } else {
                // Switch is OFF
                switchButton.text = getString(R.string.overlay)
                paintingView.setMode(GraphMode.overlay)
                Log.d(TAG, "OFF")
            }
        }
        switchButton.text =
            if (mode === GraphMode.flowing) getString(R.string.flowing) else getString(
                R.string.overlay
            )
        switchButton.setChecked(if (mode === GraphMode.flowing) true else false)
        val delete = cardView.findViewById<ImageView>(R.id.cancel_button)
        delete.setOnClickListener { v: View? ->
            confirmDeleteWidget(cardView);
        }
        paintingView.start()
        return cardView
    }

    private fun confirmDeleteWidget(cardView : CardView) {
        CustomAlertDialogBox(this@MainActivity,
            getString(R.string.delete_widget),
            getString(R.string.are_you_sure),
            getString(R.string.cancel),
            getString(R.string.ok),
            object : IActionResult {
                override fun onSuccess() {
                    deleteCard(
                        cardView.tag as String
                    )
                }
                override fun onFailed() {}
            })
    }

    private fun deleteCard(tag: String) {
        Log.d(TAG, "Delete [$tag]")
        val index = getIndex(tag)
        if (index < 0 || index >= cardsNumber) {
            return
        }
        removeElementAt(index)
        scrollToFirst()
    }

    private fun scrollToFirst() {
        val childCount = cardsNumber
        if (childCount == 0) {
            return
        }
        val heightInDp = 8
        val heightInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            heightInDp.toFloat(), getResources().displayMetrics
        ).toInt()
        mScrollView!!.post {
            val firstChild = mContainerLayout!!.getChildAt(0)
            if (firstChild != null) {
                //  No top, getTop() - (android:layout_margin="16dp" in card_view_layout, I hope)
                mScrollView!!.smoothScrollTo(0, firstChild.top - heightInPixels)
            }
        }
    }

    private fun removeElementAt(index: Int) {
        val childCount = cardsNumber
        if (index < 0 || index >= childCount) {
            return
        }
        mContainerLayout!!.removeViewAt(index)
    }

    private fun getIndex(tag: String): Int {
        val childCount = cardsNumber
        if (childCount == 0) {
            return -1
        }
        for (i in 0 until childCount) {
            val view = mContainerLayout!!.getChildAt(i)
            if (view is CardView) {
                val cardView = view
                if (cardView.tag != null && tag.equals(cardView.tag as String, ignoreCase = true)) {
                    return i
                }
            }
        }
        return -1
    }

    private fun scrollToLast() {
        val childCount = cardsNumber
        mScrollView!!.post {
            val lastChild = mContainerLayout!!.getChildAt(childCount - 1)
            if (lastChild != null) {
                mScrollView!!.smoothScrollTo(0, lastChild.bottom)
            }
        }
    }

    private val cardsNumber: Int
        private get() = mContainerLayout!!.childCount
}

//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//    }
//}