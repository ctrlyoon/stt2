package com.tyeng.stt2

import android.Manifest
import android.Manifest.permission.CALL_PHONE
import android.Manifest.permission.READ_PHONE_STATE
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var telecomManager: TelecomManager
    private lateinit var changeDefaultDialerIntent: Intent
    private lateinit var defaultDialerLauncher: ActivityResultLauncher<Intent>
    private var defaultDialer: Boolean = false

    class MainActivity : AppCompatActivity() {

        private lateinit var telecomManager: TelecomManager
        private lateinit var changeDefaultDialerIntent: Intent
        private lateinit var defaultDialerLauncher: ActivityResultLauncher<Intent>
        private var defaultDialer: Boolean = false

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            // Request runtime permissions
            val permissions =
                arrayOf(
                    READ_PHONE_STATE,
                    CALL_PHONE
                )
            val permissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                    if (!isGranted) {
                        Toast.makeText(this, "권한이 없다면 앱이 정상적으로 동작하지 않습니다.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        handleDefaultDialer()
                    }
                }
            for (permission in permissions) {
                permissionLauncher.launch(permission)
            }

            // Check if app is set as the default dialer
            telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                    if (telecomManager.isInCall) {
                        defaultDialer = packageName == telecomManager.defaultDialerPackage
                    } else {
                        val roleManager = getSystemService(ROLE_SERVICE) as RoleManager?
                        if (roleManager != null) {
                            if (roleManager.isRoleAvailable(RoleManager.ROLE_DIALER)) {
                                val isRoleGranted = roleManager.isRoleHeld(RoleManager.ROLE_DIALER)
                                if (!isRoleGranted) {
                                    defaultDialerLauncher =
                                        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                                            val thisDefaultDialer =
                                                packageName == telecomManager.defaultDialerPackage
                                            defaultDialer = thisDefaultDialer
                                            if (!thisDefaultDialer) {
                                                Toast.makeText(
                                                    this,
                                                    "기본 전화 앱 설정은 필수입니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                finish()
                                            }
                                        }
                                    defaultDialerLauncher.launch(changeDefaultDialerIntent)
                                } else {
                                    defaultDialer =
                                        packageName == telecomManager.defaultDialerPackage
                                }
                            }
                        }
                    }
                }
            } else {
                defaultDialer = true
            }
            if (!defaultDialer) {
                Toast.makeText(this, "기본 전화 앱 설정은 필수입니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        private fun handleDefaultDialer() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                if (!telecomManager.isInCall) {
                    if (!defaultDialer) {
                        defaultDialerLauncher.launch(changeDefaultDialerIntent)
                    }
                }
            } else {
                if (!defaultDialer) {
                    defaultDialerLauncher.launch(changeDefaultDialerIntent)
                }
            }
        }

        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleDefaultDialer()
            }
        }
    }
}