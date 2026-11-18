package com.kania.androidplayground.ui.devsettings

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings

object DevSettingsManager {

    /**
     * WRITE_SECURE_SETTINGS 권한 획득 여부 확인
     */
    fun hasWriteSecureSettingsPermission(context: Context): Boolean {
        return context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 현재 개발자 옵션 마스터 스위치 활성화 여부
     */
    fun isDevSettingsEnabled(context: Context): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) == 1
        } catch (_: Exception) {
            false
        }
    }

    /**
     * 개발자 옵션 마스터 스위치 켜기 / 끄기
     */
    fun setDevSettingsEnabled(context: Context, enabled: Boolean): ToggleResult {
        if (!hasWriteSecureSettingsPermission(context)) {
            return ToggleResult(
                success = false,
                message = "권한 부족: PC에서 'adb shell pm grant ...' 명령어로 WRITE_SECURE_SETTINGS 권한을 먼저 부여해주세요."
            )
        }

        return try {
            val targetValue = if (enabled) 1 else 0
            val success = Settings.Global.putInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                targetValue
            )
            if (success) {
                val stateText = if (enabled) "켜짐(ON)" else "꺼짐(OFF)"
                ToggleResult(true, "개발자 옵션이 $stateText 상태로 변경되었습니다.")
            } else {
                ToggleResult(false, "설정 변경 실패 (반환값: false)")
            }
        } catch (e: SecurityException) {
            ToggleResult(false, "보안 오류: WRITE_SECURE_SETTINGS 권한이 필요합니다. (${e.localizedMessage})")
        } catch (t: Throwable) {
            ToggleResult(false, "오류 발생: ${t.localizedMessage}")
        }
    }

    /**
     * 현재 USB 디버깅(ADB) 활성화 여부
     */
    fun isAdbEnabled(context: Context): Boolean {
        return try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED,
                0
            ) == 1
        } catch (_: Exception) {
            false
        }
    }

    /**
     * USB 디버깅(ADB) 켜기 / 끄기
     */
    fun setAdbEnabled(context: Context, enabled: Boolean): ToggleResult {
        if (!hasWriteSecureSettingsPermission(context)) {
            return ToggleResult(
                success = false,
                message = "권한 부족: PC에서 WRITE_SECURE_SETTINGS 권한을 먼저 부여해주세요."
            )
        }

        return try {
            val targetValue = if (enabled) 1 else 0
            val success = Settings.Global.putInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED,
                targetValue
            )
            if (success) {
                val stateText = if (enabled) "켜짐(ON)" else "꺼짐(OFF)"
                ToggleResult(true, "USB 디버깅이 $stateText 상태로 변경되었습니다.")
            } else {
                ToggleResult(false, "USB 디버깅 변경 실패")
            }
        } catch (e: SecurityException) {
            ToggleResult(false, "보안 오류: WRITE_SECURE_SETTINGS 권한이 필요합니다. (${e.localizedMessage})")
        } catch (t: Throwable) {
            ToggleResult(false, "오류 발생: ${t.localizedMessage}")
        }
    }

    /**
     * 시스템 설정 앱의 개발자 옵션 화면으로 직접 이동
     */
    fun openDevelopmentSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallback)
        }
    }

    fun getCurrentState(context: Context): DevSettingsState {
        return DevSettingsState(
            isDevSettingsEnabled = isDevSettingsEnabled(context),
            isAdbEnabled = isAdbEnabled(context),
            hasPermission = hasWriteSecureSettingsPermission(context),
            grantAdbCommand = "adb shell pm grant ${context.packageName} android.permission.WRITE_SECURE_SETTINGS"
        )
    }
}
