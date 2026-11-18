package com.kania.androidplayground.ui.devsettings

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast

/**
 * 상단바 빠른 설정(Quick Settings)에서 USB 디버깅을 원클릭으로 켜고 끄는 타일 서비스
 */
class UsbDebuggingTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        val hasPermission = DevSettingsManager.hasWriteSecureSettingsPermission(this)
        if (!hasPermission) {
            Toast.makeText(
                this,
                "WRITE_SECURE_SETTINGS 권한이 필요합니다. 앱을 열어 확인해주세요.",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(this, DeveloperSettingsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                startActivityAndCollapse(pendingIntent)
            } else {
                @Suppress("DEPRECATION")
                startActivityAndCollapse(intent)
            }
            updateTileState()
            return
        }

        val currentEnabled = DevSettingsManager.isAdbEnabled(this)
        val target = !currentEnabled
        val result = DevSettingsManager.setAdbEnabled(this, target)
        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
        updateTileState()
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        val hasPermission = DevSettingsManager.hasWriteSecureSettingsPermission(this)
        if (!hasPermission) {
            tile.state = Tile.STATE_INACTIVE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = "권한 필요"
            }
        } else {
            val isAdb = DevSettingsManager.isAdbEnabled(this)
            tile.state = if (isAdb) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.subtitle = if (isAdb) "ON" else "OFF"
            }
        }
        tile.updateTile()
    }
}
