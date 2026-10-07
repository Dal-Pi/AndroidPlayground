package com.kania.androidplayground.ui.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

class BluetoothTestActivity : ComponentActivity() {

    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null

    private var a2dpProfile: BluetoothA2dp? = null
    private var headsetProfile: BluetoothHeadset? = null

    // UI States
    private var hasConnectPermission by mutableStateOf(false)
    private var hasScanPermission by mutableStateOf(false)

    private var adapterFeatures by mutableStateOf(AdapterFeatures())
    private val bondedDevices = mutableStateListOf<BondedDeviceItem>()
    private var profileStatus by mutableStateOf(ProfileStatus())

    private var isScanning by mutableStateOf(false)
    private val scannedDevices = mutableStateListOf<ScannedBleDeviceItem>()

    private val scanHandler = Handler(Looper.getMainLooper())

    // Activity Result Launchers
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        checkPermissionsAndUpdateState()
    }

    private val enableBtLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        refreshAllState()
    }

    // Broadcast Receiver for Bluetooth state changes
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED,
                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED,
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    refreshAllState()
                }
            }
        }
    }

    // BLE Scan Callback
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result ?: return
            handleScanResult(result)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            results?.forEach { handleScanResult(it) }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            isScanning = false
            Toast.makeText(this@BluetoothTestActivity, "BLE Scan 실패: code $errorCode", Toast.LENGTH_SHORT).show()
        }
    }

    // Profile Service Listener
    private val profileListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
            when (profile) {
                BluetoothProfile.A2DP -> {
                    a2dpProfile = proxy as? BluetoothA2dp
                    updateProfileStatus()
                }
                BluetoothProfile.HEADSET -> {
                    headsetProfile = proxy as? BluetoothHeadset
                    updateProfileStatus()
                }
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            when (profile) {
                BluetoothProfile.A2DP -> {
                    a2dpProfile = null
                    updateProfileStatus()
                }
                BluetoothProfile.HEADSET -> {
                    headsetProfile = null
                    updateProfileStatus()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.adapter

        setupProfileProxies()

        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        }
        registerReceiver(bluetoothReceiver, filter)

        setContent {
            AndroidPlaygroundTheme {
                BluetoothScreen(
                    hasConnectPermission = hasConnectPermission,
                    hasScanPermission = hasScanPermission,
                    adapterFeatures = adapterFeatures,
                    bondedDevices = bondedDevices,
                    profileStatus = profileStatus,
                    scannedDevices = scannedDevices,
                    isScanning = isScanning,
                    onRequestPermissions = { requestRequiredPermissions() },
                    onRequestEnableBluetooth = { requestEnableBluetooth() },
                    onOpenBluetoothSettings = { openBluetoothSettings() },
                    onRefreshState = { refreshAllState() },
                    onToggleBleScan = { toggleBleScan() }
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
        stopBleScan()
        unregisterReceiver(bluetoothReceiver)
        closeProfileProxies()
    }

    private fun setupProfileProxies() {
        bluetoothAdapter?.let { adapter ->
            adapter.getProfileProxy(this, profileListener, BluetoothProfile.A2DP)
            adapter.getProfileProxy(this, profileListener, BluetoothProfile.HEADSET)
        }
    }

    private fun closeProfileProxies() {
        bluetoothAdapter?.let { adapter ->
            a2dpProfile?.let { adapter.closeProfileProxy(BluetoothProfile.A2DP, it) }
            headsetProfile?.let { adapter.closeProfileProxy(BluetoothProfile.HEADSET, it) }
        }
    }

    private fun checkPermissionsAndUpdateState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            hasConnectPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED

            hasScanPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            hasConnectPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_GRANTED

            hasScanPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

        refreshAllState()
    }

    private fun requestRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            )
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun refreshAllState() {
        val adapter = bluetoothAdapter

        if (adapter == null) {
            adapterFeatures = AdapterFeatures(isSupported = false)
            bondedDevices.clear()
            return
        }

        val stateName = try {
            when (adapter.state) {
                BluetoothAdapter.STATE_OFF -> "STATE_OFF (OFF)"
                BluetoothAdapter.STATE_TURNING_ON -> "STATE_TURNING_ON"
                BluetoothAdapter.STATE_ON -> "STATE_ON (ON)"
                BluetoothAdapter.STATE_TURNING_OFF -> "STATE_TURNING_OFF"
                else -> "UNKNOWN (${adapter.state})"
            }
        } catch (e: Exception) {
            "UNKNOWN"
        }

        val scanModeName = if (hasScanPermission) {
            try {
                when (adapter.scanMode) {
                    BluetoothAdapter.SCAN_MODE_NONE -> "SCAN_MODE_NONE"
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE -> "SCAN_MODE_CONNECTABLE"
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> "CONNECTABLE_DISCOVERABLE"
                    else -> "UNKNOWN (${adapter.scanMode})"
                }
            } catch (e: SecurityException) {
                "권한 필요 (SecurityException)"
            } catch (e: Exception) {
                "UNKNOWN"
            }
        } else {
            "권한 필요 (BLUETOOTH_SCAN)"
        }

        var deviceName = ""
        if (hasConnectPermission) {
            try {
                deviceName = adapter.name ?: ""
            } catch (e: SecurityException) {
                deviceName = "권한 필요"
            } catch (e: Exception) {
                deviceName = ""
            }
        } else {
            deviceName = "권한 필요 (BLUETOOTH_CONNECT)"
        }

        val isLe2MPhySupported = try { adapter.isLe2MPhySupported } catch (e: Exception) { false }
        val isLeCodedPhySupported = try { adapter.isLeCodedPhySupported } catch (e: Exception) { false }
        val isLeExtendedAdvertisingSupported = try { adapter.isLeExtendedAdvertisingSupported } catch (e: Exception) { false }
        val isMultipleAdvertisementSupported = try { adapter.isMultipleAdvertisementSupported } catch (e: Exception) { false }
        val isOffloadedFilteringSupported = try { adapter.isOffloadedFilteringSupported } catch (e: Exception) { false }
        val isOffloadedScanBatchingSupported = try { adapter.isOffloadedScanBatchingSupported } catch (e: Exception) { false }

        adapterFeatures = AdapterFeatures(
            isSupported = true,
            isEnabled = adapter.isEnabled,
            stateName = stateName,
            scanModeName = scanModeName,
            deviceName = deviceName,
            isLe2MPhySupported = isLe2MPhySupported,
            isLeCodedPhySupported = isLeCodedPhySupported,
            isLeExtendedAdvertisingSupported = isLeExtendedAdvertisingSupported,
            isMultipleAdvertisementSupported = isMultipleAdvertisementSupported,
            isOffloadedFilteringSupported = isOffloadedFilteringSupported,
            isOffloadedScanBatchingSupported = isOffloadedScanBatchingSupported
        )

        // 페어링 기기 갱신
        bondedDevices.clear()
        if (hasConnectPermission && adapter.isEnabled) {
            try {
                adapter.bondedDevices?.forEach { dev ->
                    val typeStr = when (dev.type) {
                        BluetoothDevice.DEVICE_TYPE_CLASSIC -> "CLASSIC"
                        BluetoothDevice.DEVICE_TYPE_LE -> "LE"
                        BluetoothDevice.DEVICE_TYPE_DUAL -> "DUAL"
                        else -> "UNKNOWN"
                    }
                    val bondStr = when (dev.bondState) {
                        BluetoothDevice.BOND_BONDED -> "BONDED"
                        BluetoothDevice.BOND_BONDING -> "BONDING"
                        else -> "NONE"
                    }
                    bondedDevices.add(
                        BondedDeviceItem(
                            name = dev.name ?: "Unknown",
                            address = dev.address,
                            bondState = bondStr,
                            type = typeStr
                        )
                    )
                }
            } catch (e: SecurityException) {
                // SecurityException 처리
            }
        }

        updateProfileStatus()
    }

    @SuppressLint("MissingPermission")
    private fun updateProfileStatus() {
        if (!hasConnectPermission || bluetoothAdapter?.isEnabled != true) {
            profileStatus = ProfileStatus(
                isA2dpProxyReady = a2dpProfile != null,
                isHeadsetProxyReady = headsetProfile != null
            )
            return
        }

        val a2dpList = try {
            a2dpProfile?.connectedDevices?.map { "${it.name ?: "기기"} (${it.address})" } ?: emptyList()
        } catch (e: SecurityException) {
            emptyList()
        }

        val headsetList = try {
            headsetProfile?.connectedDevices?.map { "${it.name ?: "기기"} (${it.address})" } ?: emptyList()
        } catch (e: SecurityException) {
            emptyList()
        }

        profileStatus = ProfileStatus(
            a2dpConnectedDevices = a2dpList,
            headsetConnectedDevices = headsetList,
            isA2dpProxyReady = a2dpProfile != null,
            isHeadsetProxyReady = headsetProfile != null
        )
    }

    private fun requestEnableBluetooth() {
        val adapter = bluetoothAdapter
        if (adapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (adapter.isEnabled) {
            Toast.makeText(this, "블루투스가 이미 켜져 있습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 일반 앱에서 블루투스를 켜는 유일한 정석 Public Intent
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        enableBtLauncher.launch(enableBtIntent)
    }

    private fun openBluetoothSettings() {
        val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    private fun toggleBleScan() {
        if (isScanning) {
            stopBleScan()
        } else {
            startBleScan()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startBleScan() {
        val scanner = bluetoothAdapter?.bluetoothLeScanner
        if (scanner == null) {
            Toast.makeText(this, "BLE Scanner를 사용할 수 없습니다. (Bluetooth OFF 상태 확인)", Toast.LENGTH_SHORT).show()
            return
        }
        if (!hasScanPermission) {
            Toast.makeText(this, "BLUETOOTH_SCAN 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            requestRequiredPermissions()
            return
        }

        scannedDevices.clear()
        isScanning = true

        try {
            scanner.startScan(scanCallback)
            // 배터리 보호 및 일반 앱 권장사항: 15초 후 자동 스캔 중지
            scanHandler.postDelayed({
                if (isScanning) {
                    stopBleScan()
                    Toast.makeText(this, "스캔이 완료되었습니다. (15초 경과)", Toast.LENGTH_SHORT).show()
                }
            }, 15000)
        } catch (e: SecurityException) {
            isScanning = false
            Toast.makeText(this, "스캔 권한 오류: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopBleScan() {
        if (!isScanning) return
        isScanning = false
        scanHandler.removeCallbacksAndMessages(null)
        try {
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        } catch (e: Exception) {
            // ignore
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleScanResult(result: ScanResult) {
        val device = result.device ?: return
        val name = device.name ?: result.scanRecord?.deviceName ?: ""
        val address = device.address

        val existingIndex = scannedDevices.indexOfFirst { it.address == address }
        if (existingIndex >= 0) {
            scannedDevices[existingIndex] = scannedDevices[existingIndex].copy(
                name = name.ifEmpty { scannedDevices[existingIndex].name },
                rssi = result.rssi,
                lastSeenTimestamp = System.currentTimeMillis()
            )
        } else {
            scannedDevices.add(
                0,
                ScannedBleDeviceItem(
                    name = name,
                    address = address,
                    rssi = result.rssi
                )
            )
        }
    }
}
