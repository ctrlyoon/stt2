package com.tyeng.stt2

import android.bluetooth.BluetoothAdapter.STATE_DISCONNECTED
import android.telecom.Call
import android.telecom.Connection.*
import android.telecom.InCallService
import android.util.Log

class CallService : InCallService() {

    companion object {
        private val TAG = "mike_" + Thread.currentThread().stackTrace[2].className + " "
    }

    override fun onCallAdded(call: Call?) {
        super.onCallAdded(call)
        val callState = call?.getState()

        when (callState) {
            STATE_RINGING -> {
                Log.d(TAG + " " + Thread.currentThread().stackTrace[2].lineNumber,"STATE_RINGING")
            }
            STATE_DIALING -> {
                Log.d(TAG + " " + Thread.currentThread().stackTrace[2].lineNumber,"STATE_DIALING")
            }
            STATE_ACTIVE -> {
                Log.d(TAG + " " + Thread.currentThread().stackTrace[2].lineNumber,"STATE_ACTIVE")
            }
            STATE_DISCONNECTED -> {
                Log.d(TAG + " " + Thread.currentThread().stackTrace[2].lineNumber,"STATE_DISCONNECTED")
            }
        }
    }

}