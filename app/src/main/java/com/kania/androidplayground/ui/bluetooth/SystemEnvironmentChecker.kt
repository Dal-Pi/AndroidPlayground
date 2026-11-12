package com.kania.androidplayground.ui.bluetooth

import android.os.Build

object SystemEnvironmentChecker {

    /**
     * 현재 기기가 에뮬레이터이거나 userdebug/eng 빌드인지 여부 (시스템 개발 환경)
     */
    fun isSystemDevelopmentEnvironment(): Boolean {
        return isEmulator() || isUserDebugOrEng()
    }

    /**
     * userdebug 또는 eng 롬 빌드 여부
     */
    fun isUserDebugOrEng(): Boolean {
        val type = Build.TYPE
        return "userdebug".equals(type, ignoreCase = true) || "eng".equals(type, ignoreCase = true)
    }

    /**
     * 에뮬레이터 환경 판별
     */
    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu"))
    }

    fun getEnvironmentSummary(): String {
        return if (isSystemDevelopmentEnvironment()) {
            val envType = if (isEmulator()) "가상머신 에뮬레이터" else "userdebug/eng 기기"
            "시스템 개발 환경 ($envType)"
        } else {
            "일반 상용 스마트폰 (User ROM)"
        }
    }
}
