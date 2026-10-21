package com.kania.androidplayground.ui.deviceinfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kania.androidplayground.ui.theme.AndroidPlaygroundTheme

class DeviceInfoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidPlaygroundTheme {
                var deviceInfo by remember {
                    mutableStateOf(DeviceInfoProvider.getFullDeviceInfo(this))
                }

                DeviceInfoScreen(
                    deviceInfo = deviceInfo,
                    onRefresh = {
                        deviceInfo = DeviceInfoProvider.getFullDeviceInfo(this)
                    }
                )
            }
        }
    }
}
