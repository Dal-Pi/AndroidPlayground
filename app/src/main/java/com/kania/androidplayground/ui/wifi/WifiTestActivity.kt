package com.kania.androidplayground.ui.wifi

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.kania.androidplayground.ui.theme.AndroidPlaygroundTheme
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

class WifiTestActivity : ComponentActivity() {

    private var wifiManager: WifiManager? = null
    private var connectivityManager: ConnectivityManager? = null

    // UI States
    private var hasLocationPermission by mutableStateOf(false)
    private var hasNearbyWifiPermission by mutableStateOf(false)

    private var hardwareFeatures by mutableStateOf(WifiHardwareFeatures())
    private var currentConnection by mutableStateOf(CurrentWifiConnection())
    private val scannedList = mutableStateListOf<ScannedWifiItem>()
    private var isScanning by mutableStateOf(false)

    // Activity Result Launchers
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        checkPermissionsAndUpdateState()
    }

    // Broadcast Receiver for Wi-Fi State Changes & Scan Results
    private val wifiReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                WifiManager.WIFI_STATE_CHANGED_ACTION,
                WifiManager.NETWORK_STATE_CHANGED_ACTION,
                WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION -> {
                    refreshAllState()
                }
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                    isScanning = false
                    val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                    handleScanResults(success)
                }
            }
        }
    }

    // Network Callback for real-time Wi-Fi connection info
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            refreshConnectionInfo(networkCapabilities)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            currentConnection = CurrentWifiConnection(isConnected = false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        connectivityManager = getSystemService(ConnectivityManager::class.java)

        val filter = IntentFilter().apply {
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        }
        registerReceiver(wifiReceiver, filter)

        registerNetworkCallback()

        setContent {
            AndroidPlaygroundTheme {
                WifiScreen(
                    hasLocationPermission = hasLocationPermission,
                    hasNearbyWifiPermission = hasNearbyWifiPermission,
                    hardwareFeatures = hardwareFeatures,
                    currentConnection = currentConnection,
                    scannedList = scannedList,
                    isScanning = isScanning,
                    onRequestPermissions = { requestRequiredPermissions() },
                    onOpenWifiSettings = { openWifiSettings() },
                    onRefreshState = { refreshAllState() },
                    onTriggerScan = { triggerWifiScan() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissionsAndUpdateState()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiReceiver)
        try {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun registerNetworkCallback() {
        try {
            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()
            connectivityManager?.registerNetworkCallback(request, networkCallback)
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun checkPermissionsAndUpdateState() {
        hasLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        hasNearbyWifiPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }

        refreshAllState()
    }

    private fun requestRequiredPermissions() {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    private fun refreshAllState() {
        val wifi = wifiManager ?: return

        // 1. 하드웨어 및 기본 상태 갱신
        val stateName = when (wifi.wifiState) {
            WifiManager.WIFI_STATE_DISABLED -> "DISABLED (꺼짐)"
            WifiManager.WIFI_STATE_DISABLING -> "DISABLING (꺼지는 중)"
            WifiManager.WIFI_STATE_ENABLED -> "ENABLED (켜짐)"
            WifiManager.WIFI_STATE_ENABLING -> "ENABLING (켜지는 중)"
            else -> "UNKNOWN (${wifi.wifiState})"
        }

        val is5GHz = try { wifi.is5GHzBandSupported } catch (e: Exception) { false }
        val is6GHz = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) wifi.is6GHzBandSupported else false
        } catch (e: Exception) { false }

        val isP2p = packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)

        val isEasyConnect = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) wifi.isEasyConnectSupported else false
        } catch (e: Exception) { false }

        val isWpa3Sae = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) wifi.isWpa3SaeSupported else false
        } catch (e: Exception) { false }

        val isWpa3SuiteB = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) wifi.isWpa3SuiteBSupported else false
        } catch (e: Exception) { false }

        val isEnhancedOpen = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) wifi.isEnhancedOpenSupported else false
        } catch (e: Exception) { false }

        hardwareFeatures = WifiHardwareFeatures(
            isWifiEnabled = wifi.isWifiEnabled,
            wifiStateName = stateName,
            is5GHzSupported = is5GHz,
            is6GHzSupported = is6GHz,
            isP2pSupported = isP2p,
            isEasyConnectSupported = isEasyConnect,
            isWpa3SaeSupported = isWpa3Sae,
            isWpa3SuiteBSupported = isWpa3SuiteB,
            isEnhancedOpenSupported = isEnhancedOpen
        )

        // 2. 현재 연결 상태 갱신
        val activeNetwork = connectivityManager?.activeNetwork
        val caps = activeNetwork?.let { connectivityManager?.getNetworkCapabilities(it) }
        if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            refreshConnectionInfo(caps)
        } else {
            // 보조: wifiManager.connectionInfo 확인
            val info = try { wifi.connectionInfo } catch (e: Exception) { null }
            if (info != null && info.networkId != -1) {
                updateConnectionInfoFromWifiInfo(info)
            } else {
                currentConnection = CurrentWifiConnection(isConnected = false)
            }
        }

        // 3. 기존 캐시된 스캔 결과 가져오기
        handleScanResults(true)
    }

    private fun refreshConnectionInfo(caps: NetworkCapabilities) {
        val wifiInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            caps.transportInfo as? WifiInfo ?: wifiManager?.connectionInfo
        } else {
            wifiManager?.connectionInfo
        }

        if (wifiInfo != null && wifiInfo.networkId != -1) {
            updateConnectionInfoFromWifiInfo(wifiInfo)
        } else {
            currentConnection = CurrentWifiConnection(isConnected = false)
        }
    }

    private fun updateConnectionInfoFromWifiInfo(info: WifiInfo) {
        var rawSsid = info.ssid ?: "<미연결>"
        if (rawSsid.startsWith("\"") && rawSsid.endsWith("\"") && rawSsid.length >= 2) {
            rawSsid = rawSsid.substring(1, rawSsid.length - 1)
        }
        val ssid = if (rawSsid == "<unknown ssid>") {
            "<unknown ssid> (위치 권한 필요)"
        } else {
            rawSsid
        }

        val bssid = info.bssid ?: "N/A"
        val rssi = info.rssi
        val linkSpeed = info.linkSpeed
        val freq = info.frequency
        val band = when {
            freq in 2400..2484 -> "2.4 GHz"
            freq in 4900..5895 -> "5 GHz"
            freq in 5925..7125 -> "6 GHz"
            else -> "기타 대역"
        }

        val standard = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when (info.wifiStandard) {
                ScanResult.WIFI_STANDARD_11AX -> "Wi-Fi 6 / 6E (802.11ax)"
                ScanResult.WIFI_STANDARD_11AC -> "Wi-Fi 5 (802.11ac)"
                ScanResult.WIFI_STANDARD_11N -> "Wi-Fi 4 (802.11n)"
                ScanResult.WIFI_STANDARD_LEGACY -> "Legacy (802.11a/b/g)"
                8 -> "Wi-Fi 7 (802.11be)"
                else -> "알 수 없음 (${info.wifiStandard})"
            }
        } else {
            "Android 10 이하 미지원"
        }

        val ipStr = formatIpAddress(info.ipAddress)

        currentConnection = CurrentWifiConnection(
            isConnected = true,
            ssid = ssid,
            bssid = bssid,
            rssi = rssi,
            linkSpeedMbps = linkSpeed,
            frequencyMhz = freq,
            frequencyBand = band,
            wifiStandard = standard,
            ipAddress = ipStr,
            macAddress = info.macAddress ?: "02:00:00:00:00:00 (마스킹)"
        )
    }

    @SuppressLint("MissingPermission")
    private fun triggerWifiScan() {
        val wifi = wifiManager
        if (wifi == null) {
            Toast.makeText(this, "Wi-Fi 모듈이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (!wifi.isWifiEnabled) {
            Toast.makeText(this, "Wi-Fi가 꺼져 있습니다. 먼저 켜주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if (!hasLocationPermission && !hasNearbyWifiPermission) {
            Toast.makeText(this, "스캔을 위해 위치 또는 Nearby 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            requestRequiredPermissions()
            return
        }

        isScanning = true
        val started = try {
            wifi.startScan()
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
            false
        }

        if (started) {
            Toast.makeText(this, "주변 Wi-Fi 스캔을 시작했습니다.", Toast.LENGTH_SHORT).show()
        } else {
            isScanning = false
            Toast.makeText(this, "스캔 실패: Android Scan Throttling (2분에 최대 4회 초과)", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleScanResults(isSuccess: Boolean) {
        val wifi = wifiManager ?: return
        if (!hasLocationPermission && !hasNearbyWifiPermission) {
            return
        }

        val results = try {
            wifi.scanResults
        } catch (e: SecurityException) {
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        scannedList.clear()
        results.sortedByDescending { it.level }.forEach { res ->
            val standard = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                when (res.wifiStandard) {
                    ScanResult.WIFI_STANDARD_11AX -> "Wi-Fi 6"
                    ScanResult.WIFI_STANDARD_11AC -> "Wi-Fi 5"
                    ScanResult.WIFI_STANDARD_11N -> "Wi-Fi 4"
                    8 -> "Wi-Fi 7"
                    else -> "802.11"
                }
            } else {
                "Wi-Fi"
            }

            scannedList.add(
                ScannedWifiItem(
                    ssid = res.SSID ?: "",
                    bssid = res.BSSID ?: "",
                    rssi = res.level,
                    frequencyMhz = res.frequency,
                    capabilities = res.capabilities ?: "",
                    standardName = standard
                )
            )
        }
    }

    private fun openWifiSettings() {
        try {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "설정 화면을 열 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatIpAddress(ip: Int): String {
        return if (ip == 0) {
            "0.0.0.0"
        } else {
            try {
                val byteBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ip)
                InetAddress.getByAddress(byteBuffer.array()).hostAddress ?: "0.0.0.0"
            } catch (e: Exception) {
                "0.0.0.0"
            }
        }
    }
}
