package com.kania.androidplayground.ui.bluetooth

data class AdapterFeatures(
    val isSupported: Boolean = false,
    val isEnabled: Boolean = false,
    val stateName: String = "UNKNOWN",
    val scanModeName: String = "UNKNOWN",
    val deviceName: String = "",
    val isLe2MPhySupported: Boolean = false,
    val isLeCodedPhySupported: Boolean = false,
    val isLeExtendedAdvertisingSupported: Boolean = false,
    val isMultipleAdvertisementSupported: Boolean = false,
    val isOffloadedFilteringSupported: Boolean = false,
    val isOffloadedScanBatchingSupported: Boolean = false
)

data class BondedDeviceItem(
    val name: String,
    val address: String,
    val bondState: String,
    val type: String
)

data class ScannedBleDeviceItem(
    val name: String,
    val address: String,
    val rssi: Int,
    val lastSeenTimestamp: Long = System.currentTimeMillis()
)

data class ProfileStatus(
    val a2dpConnectedDevices: List<String> = emptyList(),
    val headsetConnectedDevices: List<String> = emptyList(),
    val isA2dpProxyReady: Boolean = false,
    val isHeadsetProxyReady: Boolean = false
)

data class AospComparisonItem(
    val category: String,
    val feature: String,
    val publicStatus: String, // "가능", "제한적", "불가 (AOSP 전용)"
    val publicExplanation: String,
    val aospExplanation: String
)
