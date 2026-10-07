package com.kania.androidplayground.ui.wifi

data class WifiHardwareFeatures(
    val isWifiEnabled: Boolean = false,
    val wifiStateName: String = "UNKNOWN",
    val is5GHzSupported: Boolean = false,
    val is6GHzSupported: Boolean = false,
    val isP2pSupported: Boolean = false,
    val isEasyConnectSupported: Boolean = false,
    val isWpa3SaeSupported: Boolean = false,
    val isWpa3SuiteBSupported: Boolean = false,
    val isEnhancedOpenSupported: Boolean = false
)

data class CurrentWifiConnection(
    val isConnected: Boolean = false,
    val ssid: String = "<미연결>",
    val bssid: String = "N/A",
    val rssi: Int = 0,
    val linkSpeedMbps: Int = 0,
    val frequencyMhz: Int = 0,
    val frequencyBand: String = "N/A",
    val wifiStandard: String = "알 수 없음",
    val ipAddress: String = "0.0.0.0",
    val macAddress: String = "02:00:00:00:00:00 (마스킹됨)"
)

data class ScannedWifiItem(
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val frequencyMhz: Int,
    val capabilities: String,
    val standardName: String
)

data class AospWifiComparisonItem(
    val category: String,
    val feature: String,
    val publicStatus: String, // "가능", "제한적", "불가 (AOSP 전용)"
    val publicExplanation: String,
    val aospExplanation: String
)

data class WifiGlossaryItem(
    val term: String,
    val category: String,
    val summary: String,
    val fullExplanation: String,
    val relatedApis: List<String> = emptyList()
)
