package com.kania.androidplayground.ui.devsettings

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kania.androidplayground.ui.theme.AndroidPlaygroundTheme

class DeveloperSettingsActivity : ComponentActivity() {

    private var devSettingsState by mutableStateOf(DevSettingsState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateState()

        setContent {
            AndroidPlaygroundTheme {
                DeveloperSettingsScreen(
                    state = devSettingsState,
                    onToggleDevSettings = { enabled ->
                        val result = DevSettingsManager.setDevSettingsEnabled(this, enabled)
                        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                        updateState()
                    },
                    onToggleAdb = { enabled ->
                        val result = DevSettingsManager.setAdbEnabled(this, enabled)
                        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                        updateState()
                    },
                    onRefresh = {
                        updateState()
                        Toast.makeText(this, "상태가 새로고침되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateState()
    }

    private fun updateState() {
        devSettingsState = DevSettingsManager.getCurrentState(this)
    }
}
