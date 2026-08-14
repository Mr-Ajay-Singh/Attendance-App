package com.invictus.attendanceapp.core.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

fun openAppPermissionSettings(context: Context) {
    try {
        // Try opening the specific App Permissions management screen (Android 11+)
        val managePermissionsIntent = Intent("android.intent.action.MANAGE_APP_PERMISSIONS").apply {
            putExtra("android.intent.extra.PACKAGE_NAME", context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(managePermissionsIntent)
    } catch (e: Exception) {
        try {
            // Fallback to the App Info / App Details Settings page
            val appDetailsIntent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(appDetailsIntent)
        } catch (e2: Exception) {
            // Fallback to general settings
            val generalSettingsIntent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(generalSettingsIntent)
        }
    }
}
