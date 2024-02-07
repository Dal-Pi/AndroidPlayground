package com.kania.androidplayground.ui.notification

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationManagerCompat
import com.kania.androidplayground.ui.theme.AndroidPlaygroundTheme

class NotificationActivity : ComponentActivity() {
    //TODO make stateFlow
    private var areNotificationsEnabled: MutableState<Boolean?> = mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidPlaygroundTheme {
                NotificationScreen(
                    areNotificationsEnabled = areNotificationsEnabled.value,
                    onClickNotificationSettings = {
                        launchNotificationSettingsByVersion()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        areNotificationsEnabled.value = NotificationManagerCompat.from(this).areNotificationsEnabled()
    }

    private fun launchNotificationSettingsByVersion() {
        val notificationSettingsIntent = when {
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) -> {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).also { intent ->
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
            }
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) -> {
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).also { intent ->
                    intent.putExtra("app_package", packageName)
                    intent.putExtra("app_uid", applicationInfo.uid)
                }
            }
            else -> {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    data = Uri.parse("package:" + packageName)
                }
            }
        }

        notificationSettingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(notificationSettingsIntent)
    }
}