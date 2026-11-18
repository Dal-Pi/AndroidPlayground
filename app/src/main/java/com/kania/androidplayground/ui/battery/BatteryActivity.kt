package com.kania.androidplayground.ui.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kania.androidplayground.ui.theme.AndroidPlaygroundTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class BatteryActivity : ComponentActivity() {

    private var batteryState by mutableStateOf(BatteryState())
    private var isAutoRefresh by mutableStateOf(true)

    // 충전기 연결/해제 및 배터리 상태 변화 브로드캐스트 리시버
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            refreshBatteryState()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 초기 상태 로드
        refreshBatteryState()

        // 실시간 1초 주기 갱신 코루틴 (화면이 활성화되어 있을 때만 실행)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while (isActive) {
                    if (isAutoRefresh) {
                        refreshBatteryState()
                    }
                    delay(1000L)
                }
            }
        }

        setContent {
            AndroidPlaygroundTheme {
                BatteryScreen(
                    batteryState = batteryState,
                    isAutoRefresh = isAutoRefresh,
                    onToggleAutoRefresh = {
                        isAutoRefresh = !isAutoRefresh
                    },
                    onManualRefresh = {
                        refreshBatteryState()
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }
        registerReceiver(batteryReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(batteryReceiver)
        } catch (_: Exception) {}
    }

    private fun refreshBatteryState() {
        batteryState = BatteryDataProvider.getBatteryState(this)
    }
}
