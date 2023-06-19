package com.devjeem.clasificadordeimagenes.util

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserPermits @Inject constructor(
    @ActivityContext private val activity: Activity,
    @ApplicationContext private val context: Context
) {
    fun checkPermissionsCamera(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionsCamera()
            false
        } else {
            true
        }
    }

    private fun requestPermissionsCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.CAMERA
            )
        ) {
            Log.e("PERMISOS","Permisos no entregados")
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                100
            )
        }
    }
}