package com.example.ecgwidgetsviewer

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class CustomAlertDialogBox(
    private var mActivity: Activity?,
    private val mTitle: String,
    private val mMessage: String,
    private val mCancel: String,
    private val mOk: String,
    private val mResponse: IActionResult?) {

    val TAG = CustomAlertDialogBox::class.java.getSimpleName()
    private var mDialog: AlertDialog? = null
    private var progressBar: ProgressBar? = null
    private val mHandler = Handler(Looper.getMainLooper())

    init {
        createAndRender()
    }

    private fun createAndRender() {
        val dialogView = LayoutInflater.from(mActivity).inflate(R.layout.custom_alert_dialog, null)

// Initialize components
        val titleTextView = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val messageTextView = dialogView.findViewById<TextView>(R.id.dialogMessage)
        progressBar = dialogView.findViewById(R.id.progressBar)
        progressBar?.setVisibility(View.GONE)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        cancelButton.text = mCancel
        val doneButton = dialogView.findViewById<Button>(R.id.doneButton)
        doneButton.text = mOk
        titleTextView.text = mTitle
        messageTextView.text = mMessage

// Set up the dialog
        val context: Context = ContextThemeWrapper(mActivity, R.style.Theme_ECGWidgetsViewer)
        val builder = MaterialAlertDialogBuilder(context)
        builder.setView(dialogView)
        builder.setCancelable(false) // Set this to true if you want to dismiss the dialog by tapping outside

// Create the dialog
        mDialog = builder.create()
        mDialog!!.setCancelable(true)
        mDialog!!.setOnCancelListener { close(false) }


// Set up click listener for the cancel button
        cancelButton.setOnClickListener { v: View? ->
            close(
                false
            )
        }
        doneButton.setOnClickListener { v: View? ->
            doneButton.setEnabled(false)
            start()
        }

//  Make rounded
        mDialog!!.window!!.decorView.setBackgroundResource(R.drawable.rounded_dialog_background) // setting the background

//  Show the dialog
        mDialog!!.show()
    }

    fun close(result: Boolean) {
        if (mResponse != null) {
            if (result) {
                mResponse.onSuccess()
            } else {
                mHandler.removeCallbacksAndMessages(null)
                mResponse.onFailed()
            }
        }
        mDialog!!.dismiss()
        mActivity = null
    }

    fun start() {
        progressBar!!.visibility = View.VISIBLE
        mHandler.postDelayed({
            val cuccess = true
            if (cuccess) {
                close(true)
            } else {
                close(false)
            }
        }, 500)
    }
}

