package com.kania.androidplayground.ui.bluetooth

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kania.androidplayground.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothScreen(
    hasConnectPermission: Boolean,
    hasScanPermission: Boolean,
    adapterFeatures: AdapterFeatures,
    bondedDevices: List<BondedDeviceItem>,
    profileStatus: ProfileStatus,
    callAudioState: BluetoothCallAudioState,
    scannedDevices: List<ScannedBleDeviceItem>,
    isScanning: Boolean,
    onRequestPermissions: () -> Unit,
    onRequestEnableBluetooth: () -> Unit,
    onOpenBluetoothSettings: () -> Unit,
    onRefreshState: () -> Unit,
    onToggleBleScan: () -> Unit,
    onToggleCallAudio: () -> Unit
) {
    val activity = LocalContext.current as? Activity
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Public API 실습", "AOSP vs Public 비교", "주요 용어집")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.playground_bluetooth))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefreshState) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "새로고침",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> PublicApiDemoTab(
                    hasConnectPermission = hasConnectPermission,
                    hasScanPermission = hasScanPermission,
                    adapterFeatures = adapterFeatures,
                    bondedDevices = bondedDevices,
                    profileStatus = profileStatus,
                    callAudioState = callAudioState,
                    scannedDevices = scannedDevices,
                    isScanning = isScanning,
                    onRequestPermissions = onRequestPermissions,
                    onRequestEnableBluetooth = onRequestEnableBluetooth,
                    onOpenBluetoothSettings = onOpenBluetoothSettings,
                    onToggleBleScan = onToggleBleScan,
                    onToggleCallAudio = onToggleCallAudio
                )
                1 -> AospComparisonTab()
                2 -> GlossaryTab()
            }
        }
    }
}

@Composable
private fun PublicApiDemoTab(
    hasConnectPermission: Boolean,
    hasScanPermission: Boolean,
    adapterFeatures: AdapterFeatures,
    bondedDevices: List<BondedDeviceItem>,
    profileStatus: ProfileStatus,
    callAudioState: BluetoothCallAudioState,
    scannedDevices: List<ScannedBleDeviceItem>,
    isScanning: Boolean,
    onRequestPermissions: () -> Unit,
    onRequestEnableBluetooth: () -> Unit,
    onOpenBluetoothSettings: () -> Unit,
    onToggleBleScan: () -> Unit,
    onToggleCallAudio: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. 런타임 권한 상태 카드
        item {
            PermissionCard(
                hasConnectPermission = hasConnectPermission,
                hasScanPermission = hasScanPermission,
                onRequestPermissions = onRequestPermissions
            )
        }

        // 2. BluetoothAdapter 및 하드웨어 기능 카드
        item {
            AdapterFeaturesCard(
                adapterFeatures = adapterFeatures,
                onRequestEnableBluetooth = onRequestEnableBluetooth,
                onOpenBluetoothSettings = onOpenBluetoothSettings
            )
        }

        // 3. 본딩(페어링) 기기 목록 카드
        item {
            BondedDevicesCard(
                hasPermission = hasConnectPermission,
                bondedDevices = bondedDevices,
                onRequestPermissions = onRequestPermissions
            )
        }

        // 4. 프로파일 연결 상태 카드 (A2DP, Headset)
        item {
            ProfileProxyCard(
                profileStatus = profileStatus
            )
        }

        // 5. 통화 오디오 제어 카드 (Call Audio & SCO)
        item {
            CallAudioCard(
                callAudioState = callAudioState,
                onToggleCallAudio = onToggleCallAudio
            )
        }

        // 6. BLE 스캔 테스트 카드
        item {
            BleScanCard(
                hasScanPermission = hasScanPermission,
                isScanning = isScanning,
                scannedDevices = scannedDevices,
                onToggleBleScan = onToggleBleScan,
                onRequestPermissions = onRequestPermissions
            )
        }
    }
}

@Composable
private fun PermissionCard(
    hasConnectPermission: Boolean,
    hasScanPermission: Boolean,
    onRequestPermissions: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Android 12+ 런타임 권한 현황",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Android 12(API 31)부터 블루투스 접근 시 위치 권한 대신 BLUETOOTH_CONNECT / SCAN 권한이 필수입니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            PermissionStatusRow(
                permissionName = "BLUETOOTH_CONNECT",
                description = "페어링 기기 조회, 이름 획득, 프로파일 상태 확인",
                isGranted = hasConnectPermission
            )

            PermissionStatusRow(
                permissionName = "BLUETOOTH_SCAN",
                description = "주변 BLE 기기 검색 (neverForLocation 플래그 적용)",
                isGranted = hasScanPermission
            )

            if (!hasConnectPermission || !hasScanPermission) {
                Button(
                    onClick = onRequestPermissions,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "권한 요청하기")
                }
            }
        }
    }
}

@Composable
private fun PermissionStatusRow(
    permissionName: String,
    description: String,
    isGranted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = permissionName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        Icon(
            imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = if (isGranted) Color(0xFF2E7D32) else Color(0xFFD32F2F)
        )
    }
}

@Composable
private fun AdapterFeaturesCard(
    adapterFeatures: AdapterFeatures,
    onRequestEnableBluetooth: () -> Unit,
    onOpenBluetoothSettings: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (adapterFeatures.isEnabled) Icons.Default.BluetoothConnected else Icons.Default.BluetoothDisabled,
                    contentDescription = null,
                    tint = if (adapterFeatures.isEnabled) MaterialTheme.colorScheme.primary else Color.Gray
                )
                Text(
                    text = "Bluetooth Adapter & 하드웨어 스펙",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "BluetoothAdapter Public API를 통해 기기의 지원 상태와 LE 하드웨어 기능을 점검합니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            HorizontalDivider()

            FeatureItem(label = "Bluetooth 하드웨어 지원", value = if (adapterFeatures.isSupported) "지원됨" else "미지원")
            FeatureItem(label = "Bluetooth 활성화 상태", value = if (adapterFeatures.isEnabled) "ON" else "OFF")
            FeatureItem(label = "Adapter State", value = adapterFeatures.stateName)
            FeatureItem(label = "Scan Mode", value = adapterFeatures.scanModeName)
            if (adapterFeatures.deviceName.isNotEmpty()) {
                FeatureItem(label = "로컬 디바이스 이름", value = adapterFeatures.deviceName)
            }

            HorizontalDivider()
            Text(
                text = "BLE 하드웨어 Feature 지원 여부 (Android 8.0+ / 5.0+):",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            FeatureItem(label = "LE 2M PHY 지원", isSupported = adapterFeatures.isLe2MPhySupported)
            FeatureItem(label = "LE Coded PHY 지원 (Long Range)", isSupported = adapterFeatures.isLeCodedPhySupported)
            FeatureItem(label = "LE Extended Advertising 지원", isSupported = adapterFeatures.isLeExtendedAdvertisingSupported)
            FeatureItem(label = "Multiple Advertisement 지원", isSupported = adapterFeatures.isMultipleAdvertisementSupported)
            FeatureItem(label = "Offloaded Filtering 지원", isSupported = adapterFeatures.isOffloadedFilteringSupported)
            FeatureItem(label = "Offloaded Scan Batching 지원", isSupported = adapterFeatures.isOffloadedScanBatchingSupported)

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRequestEnableBluetooth,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "ON 요청 (Intent)", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onOpenBluetoothSettings,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "BT 설정 열기", fontSize = 12.sp)
                }
            }

            Text(
                text = "💡 AOSP 차이점: 일반 앱은 보안상 adapter.enable()을 직접 부를 수 없고 Intent로 사용자 동의를 받아야 합니다. (AOSP 시스템 앱은 직접 ON/OFF 가능)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun BondedDevicesCard(
    hasPermission: Boolean,
    bondedDevices: List<BondedDeviceItem>,
    onRequestPermissions: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bluetooth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "페어링된 기기 목록 (Bonded: ${bondedDevices.size}개)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!hasPermission) {
                Text(
                    text = "⚠️ 페어링 기기 조회를 위해 BLUETOOTH_CONNECT 권한이 필요합니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFD32F2F)
                )
                Button(onClick = onRequestPermissions) {
                    Text(text = "권한 허용")
                }
            } else if (bondedDevices.isEmpty()) {
                Text(
                    text = "페어링된 기기가 없거나 Bluetooth가 꺼져 있습니다.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                bondedDevices.forEach { device ->
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = device.name.ifEmpty { "이름 없음" },
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "주소: ${device.address}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "유형: ${device.type} | 상태: ${device.bondState}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            Text(
                text = "💡 AOSP 차이점: 일반 앱은 device.createBond()로 페어링 요청은 가능하지만, removeBond() (페어링 해제)는 @SystemApi로 일반 앱 호출이 제한됩니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ProfileProxyCard(
    profileStatus: ProfileStatus
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Profile Proxy 상태 (A2DP & Headset)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "BluetoothProfile.ServiceListener를 통해 현재 연결된 오디오 기기 프록시를 조회합니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            HorizontalDivider()

            Text(
                text = "A2DP (미디어 오디오) 연결 기기:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (profileStatus.a2dpConnectedDevices.isEmpty()) {
                Text(text = "  - 연결된 A2DP 기기 없음", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            } else {
                profileStatus.a2dpConnectedDevices.forEach { dev ->
                    Text(text = "  • $dev", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Headset / HandsFree 연결 기기:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (profileStatus.headsetConnectedDevices.isEmpty()) {
                Text(text = "  - 연결된 통화 헤드셋 기기 없음", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            } else {
                profileStatus.headsetConnectedDevices.forEach { dev ->
                    Text(text = "  • $dev", style = MaterialTheme.typography.bodySmall)
                }
            }

            Text(
                text = "💡 AOSP 차이점: 일반 앱은 getConnectedDevices() 조회만 가능합니다. 특정 기기를 강제로 A2DP로 connect() 하거나 disconnect() 하는 API는 AOSP BLUETOOTH_PRIVILEGED 시스템 앱에만 허용됩니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CallAudioCard(
    callAudioState: BluetoothCallAudioState,
    onToggleCallAudio: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.BluetoothConnected,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "통화 오디오 제어 (Call Audio & SCO)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "일반 3rd-party 앱(VoIP, WebRTC 등)은 HFP AT 커맨드를 직접 제어할 수 없으며, AudioManager의 SCO 링크 또는 setCommunicationDevice()를 통해 통화 음성을 블루투스로 라우팅합니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            HorizontalDivider()

            FeatureItem(label = "통화 외 SCO 지원 (isScoAvailableOffCall)", isSupported = callAudioState.isScoAvailable)
            FeatureItem(label = "현재 블루투스 SCO 활성 상태 (isBluetoothScoOn)", isSupported = callAudioState.isScoOn)
            FeatureItem(label = "현재 오디오 모드 (AudioManager.mode)", value = callAudioState.audioModeName)
            FeatureItem(label = "현재 활성 통신 장치", value = callAudioState.activeCommunicationDevice)

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "사용 가능한 블루투스 통화 장치:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (callAudioState.availableBluetoothHeadsets.isEmpty()) {
                Text(
                    text = "  - 연결/인식된 블루투스 통화 장치(SCO/BLE) 없음",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                callAudioState.availableBluetoothHeadsets.forEach { dev ->
                    Text(text = "  • $dev", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onToggleCallAudio,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (callAudioState.isScoOn || callAudioState.activeCommunicationDevice.contains("BT_SCO")) {
                        "블루투스 통화 오디오 해제 (스피커로 복원)"
                    } else {
                        "블루투스 통화 오디오 연결 (SCO 라우팅 요청)"
                    }
                )
            }

            Text(
                text = "💡 Public API 한계 & AOSP 동작 원리:\n• Public API: 전화 수신(ATA)/발신/종료(AT+CHUP) 같은 Call 제어는 시스템 기본 전화(InCallService)와 통신사만 가능하며, 일반 앱은 오디오 입출력 통로(SCO)만 전환할 수 있습니다.\n• AOSP: packages/modules/Bluetooth의 HeadsetService/HeadsetStateMachine이 모뎀(Telecom)과 결합하여 HFP 프로토콜을 수행하며, Android Automotive(차량용)는 BluetoothHeadsetClient(@SystemApi)를 통해 스마트폰 통화를 원격 제어합니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun BleScanCard(
    hasScanPermission: Boolean,
    isScanning: Boolean,
    scannedDevices: List<ScannedBleDeviceItem>,
    onToggleBleScan: () -> Unit,
    onRequestPermissions: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.BluetoothSearching,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "BLE Scanner 테스트 (발견: ${scannedDevices.size}개)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "BluetoothLeScanner Public API를 사용한 실시간 저전력 블루투스(BLE) 스캔입니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            if (!hasScanPermission) {
                Button(onClick = onRequestPermissions) {
                    Text(text = "BLUETOOTH_SCAN 권한 요청")
                }
            } else {
                Button(
                    onClick = onToggleBleScan,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isScanning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = if (isScanning) "BLE 스캔 중지" else "BLE 스캔 시작")
                }
            }

            if (scannedDevices.isEmpty()) {
                Text(
                    text = if (isScanning) "주변 BLE 기기를 검색하는 중입니다..." else "스캔 버튼을 눌러 주변 기기를 탐색해 보세요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    scannedDevices.take(10).forEach { item ->
                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name.ifEmpty { "(Unknown BLE Device)" },
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = item.address,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Text(
                                    text = "${item.rssi} dBm",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.rssi > -70) Color(0xFF2E7D32) else Color(0xFFE65100)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AospComparisonTab() {
    val comparisons = remember {
        listOf(
            AospComparisonItem(
                category = "어댑터 제어",
                feature = "Bluetooth ON / OFF",
                publicStatus = "제한적 (Intent 경유)",
                publicExplanation = "ACTION_REQUEST_ENABLE 인텐트로 다이얼로그를 띄워 사용자 승인을 받아야 함. adapter.enable()/disable() 직접 호출은 Android 13+ 차단.",
                aospExplanation = "IBluetooth.aidl / BluetoothManagerService 시스템 서비스에서 직접 스택을 제어하여 즉시 활성화/비활성화 가능."
            ),
            AospComparisonItem(
                category = "페어링 관리",
                feature = "기기 페어링 해제 (removeBond)",
                publicStatus = "불가 (SystemApi)",
                publicExplanation = "일반 앱은 createBond()로 페어링 요청만 가능. removeBond()는 @SystemApi 및 BLUETOOTH_PRIVILEGED 권한 필요하여 호출 불가.",
                aospExplanation = "설정 앱(Settings)이나 시스템 앱에서 자유롭게 페어링 정보를 제거하거나 강제 본딩 해제 가능."
            ),
            AospComparisonItem(
                category = "프로파일 제어",
                feature = "A2DP / Headset 강제 연결 (connect)",
                publicStatus = "불가 (SystemApi)",
                publicExplanation = "일반 앱은 getConnectedDevices() 등으로 '현재 연결 상태'만 조회 가능. connect(device) / disconnect(device) 메서드는 숨김(@hide) 처리됨.",
                aospExplanation = "BluetoothA2dpService, BluetoothHeadsetService를 직접 호출하여 특정 기기로 강제 오디오 라우팅 및 연결/해제 가능."
            ),
            AospComparisonItem(
                category = "MAC 주소 & 식별",
                feature = "로컬 어댑터 MAC 주소 조회",
                publicStatus = "불가 (마스킹)",
                publicExplanation = "개인정보 보호 정책(Android 6.0+)으로 일반 앱에서 adapter.address 호출 시 항상 '02:00:00:00:00:00'을 반환함.",
                aospExplanation = "System Server 및 네이티브 드라이버는 실제 NVRAM에 기록된 실제 하드웨어 MAC 주소를 취득/설정 가능."
            ),
            AospComparisonItem(
                category = "블루투스 스캔",
                feature = "Background Scan & HCI",
                publicStatus = "제한적 (규제 적용)",
                publicExplanation = "화면이 꺼지면 백그라운드 BLE 스캔 제한, ScanFilter 필수 권장, ScanSettings 규제 적용.",
                aospExplanation = "HCI Snoop 로그 캡처, 로우 레벨 HCI 소켓 통신, 블루투스 HAL(hardware abstraction layer) 직접 연동 가능."
            ),
            AospComparisonItem(
                category = "음성 통화 (HFP & Call)",
                feature = "Bluetooth Call 및 AT 커맨드 제어",
                publicStatus = "불가 (오디오 라우팅만 가능)",
                publicExplanation = "일반 앱은 HFP AT 커맨드(수신/발신/종료)나 통화 상태 머신에 접근 불가. VoIP 앱은 AudioManager(SCO 링크 또는 setCommunicationDevice)를 통한 마이크/스피커 통화 오디오 라우팅만 가능.",
                aospExplanation = "packages/modules/Bluetooth 내 HeadsetService, HeadsetStateMachine에서 AT 커맨드(ATA, AT+CHUP, AT+CLCC)를 처리하고 SCO/WBS 오디오 링크 및 텔레포니(Telecom) 연동을 직할 통제함. Android Automotive에서는 BluetoothHeadsetClient(@SystemApi)를 통해 스마트폰 통화를 원격 제어."
            ),
            AospComparisonItem(
                category = "음성 통화 (HFP & Call)",
                feature = "발신자 번호 표시 (CLIP) 제어",
                publicStatus = "불가 (System/Telecom 전용)",
                publicExplanation = "일반 앱은 AT+CLIP 설정이나 착신 시 전달되는 발신 번호 URC 패킷(+CLIP: \"번호\")을 직접 수신/제어 불가. 기본 다이얼러나 Telecom API만 통화 번호에 접근.",
                aospExplanation = "Telecom/TelephonyManager 착신 이벤트를 수신한 HeadsetPhoneState가 헤드셋의 AT+CLIP=1 등록 여부를 확인하여 +CLIP: \"번호\" 비동기 패킷을 RFCOMM 채널로 브로드캐스트."
            ),
            AospComparisonItem(
                category = "음성 통화 (HFP & Call)",
                feature = "통화 목록 & 발신자 이름 (CLCC <alpha>) 동적 갱신",
                publicStatus = "불가 (AOSP 내부 상태 머신 전용)",
                publicExplanation = "일반 앱은 AT+CLCC 응답 스트림이나 <alpha> 필드(주소록 매핑 이름)를 직접 주입/변경할 수 없음.",
                aospExplanation = "통화 도중 주소록 비동기 조회 완료, CallScreeningService(스팸 필터) 또는 IMS 네트워크 CNAP으로 이름이 변경되면 Telecom onDetailsChanged() -> BluetoothInCallService -> HeadsetPhoneState 캐시가 갱신됨. +CIEV 인디케이터나 +CLIP 재전송으로 헤드셋의 AT+CLCC 재폴링(Re-polling)을 유도하여 최신 <alpha>를 전달하며, Android Automotive(AAOS)는 ACTION_CALL_CHANGED로 계기판 UI를 즉시 리프레시함."
            ),
            AospComparisonItem(
                category = "블루투스 스택",
                feature = "Fluoride / Rust Bluetooth Stack",
                publicStatus = "접근 불가",
                publicExplanation = "Android Framework의 Binder IPC 인터페이스를 통해서만 제한적으로 접근 가능.",
                aospExplanation = "packages/modules/Bluetooth (Fluoride 스택 / AOSP Rust 스택), GD(Google Drops) 아키텍처 내부 코드 수정 및 커스텀 가능."
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📌 AOSP Bluetooth 개발자를 위한 핵심 요약",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "일반 안드로이드 앱은 보안과 프라이버시(사용자 동의 없는 기기 제어/추적 방지)를 위해 Bluetooth API의 대부분의 핵심 제어 기능(강제 ON/OFF, removeBond, connect/disconnect)이 차단되어 있습니다.\n\nAOSP 레벨에서 Bluetooth를 다룰 때는 packages/modules/Bluetooth 내부 서비스 및 @SystemApi, BLUETOOTH_PRIVILEGED 권한을 사용하여 하드웨어와 프로파일을 완전히 제어하게 됩니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // HFP 통화 발신자 정보 전달 아키텍처 상세 카드
        item {
            HfpCallerInfoArchitectureCard()
        }

        items(comparisons) { item ->
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "[${item.category}] ${item.feature}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    color = when {
                                        item.publicStatus.startsWith("가능") -> Color(0xFFE8F5E9)
                                        item.publicStatus.startsWith("제한적") -> Color(0xFFFFF3E0)
                                        else -> Color(0xFFFFEBEE)
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.publicStatus,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    item.publicStatus.startsWith("가능") -> Color(0xFF2E7D32)
                                    item.publicStatus.startsWith("제한적") -> Color(0xFFE65100)
                                    else -> Color(0xFFC62828)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📱 일반 앱 (Public API):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = item.publicExplanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🤖 AOSP / System API:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00796B)
                    )
                    Text(
                        text = item.aospExplanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun HfpCallerInfoArchitectureCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "📞 HFP 심화: 발신자 정보 전달 (CLIP & CLCC <alpha>) 메커니즘",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "차량이나 블루투스 이어폰으로 전화가 올 때 번호와 이름이 표시되고, 통화 도중 정보가 동적으로 갱신되는 AOSP 내부 동작 원리입니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            Text(
                text = "1. 발신자 번호 표시 (CLIP: Calling Line Identification)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "• HFP 초기 연결 시 헤드셋/차량이 'AT+CLIP=1'을 전송하여 번호 표시를 활성화합니다.\n• 전화 수신 시 스마트폰은 단순 'RING' 외에 '+CLIP: \"01012345678\",129' 비동기 패킷(URC)을 보내 헤드셋 TTS나 차량 화면에 번호를 띄웁니다.",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "2. 통화 목록 및 상대방 이름 필드 (CLCC <alpha>)",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "• 표준 규격: +CLCC: <idx>,<dir>,<status>,<mode>,<mpty>,<number>,<type>,<alpha>\n• 맨 끝의 <alpha> 필드가 주소록에 매핑된 이름(예: \"홍길동\")입니다. 차량 IVI는 전체 주소록을 검색할 필요 없이 이 필드로 화면에 이름을 즉시 표시합니다.",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "3. 통화 도중 <alpha>가 동적으로 변경되는 시나리오",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "• 주소록(ContactsProvider) 비동기 쿼리가 뒤늦게 완료된 경우\n• T전화/후후 등 스팸 필터링 앱(CallScreeningService)의 판정 결과가 도착한 경우\n• 통신사 VoLTE/IMS 기지국의 CNAP(상호명) 패킷이 지연 수신된 경우\n• 대표번호에서 개인 내선으로 착신 전환(COLP)된 경우",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "4. AOSP 내부 감지 및 헤드셋 전파 흐름",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "• 감지: Telecom 프레임워크의 Call.Callback.onDetailsChanged() 발생\n• 캐시 갱신: BluetoothInCallService가 HeadsetPhoneState의 CLCC 응답 캐시(<alpha>)를 새 이름으로 즉각 교체\n• 재폴링 유도: CLCC는 폴링 방식이므로 AOSP가 '+CIEV' 인디케이터나 '+CLIP'을 전송하여 헤드셋의 'AT+CLCC' 재요청을 유도하고 새 <alpha>를 전달\n• 차량(AAOS): BluetoothHeadsetClient가 ACTION_CALL_CHANGED 인텐트를 발행하여 계기판 UI 즉시 리프레시",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF00796B)
            )
        }
    }
}

@Composable
private fun FeatureItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun FeatureItem(
    label: String,
    isSupported: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(
            text = if (isSupported) "지원됨 (Yes)" else "미지원 (No)",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = if (isSupported) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun GlossaryTab() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("전체") }

    val categories = remember {
        listOf("전체", "무선 규격 & 하드웨어", "검색 & 연결 프로세스", "프로토콜 & 프로파일", "Bluetooth 통화 (Call & HFP)", "AOSP & 시스템 아키텍처")
    }

    val filteredItems = remember(searchQuery, selectedCategory) {
        BluetoothGlossaryData.items.filter { item ->
            val matchesCategory = (selectedCategory == "전체" || item.category == selectedCategory)
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val query = searchQuery.trim().lowercase()
                item.term.lowercase().contains(query) ||
                        item.summary.lowercase().contains(query) ||
                        item.fullExplanation.lowercase().contains(query) ||
                        item.relatedApis.any { it.lowercase().contains(query) }
            }
            matchesCategory && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 상단 가이드 배너
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📖 Bluetooth & AOSP 주요 용어 및 약어 사전",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "블루투스 화면과 AOSP 프레임워크에서 자주 등장하는 핵심 약어, 개념 및 프로토콜을 알기 쉽게 정리한 사전입니다. 검색창이나 카테고리 필터로 원하는 단어를 찾아보세요.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        // 검색창
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "용어 검색 (예: PHY, A2DP, 본딩, HCI, Snoop...)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "검색",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "지우기"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // 카테고리 필터 칩
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = (selectedCategory == category)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }

        // 검색 결과 개수
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "총 ${filteredItems.size}개 용어",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline
                )
                if (searchQuery.isNotBlank() || selectedCategory != "전체") {
                    Text(
                        text = "필터 적용됨",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 용어 카드 목록
        if (filteredItems.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "검색 결과가 없습니다.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "다른 검색어를 입력하시거나 카테고리를 '전체'로 변경해 보세요.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        } else {
            items(filteredItems) { item ->
                GlossaryCard(item = item)
            }
        }
    }
}

@Composable
private fun GlossaryCard(item: GlossaryItem) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 카테고리 태그 뱃지 + 용어 타이틀
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.term,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // 한 줄 핵심 요약
            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // 상세 설명
            Text(
                text = item.fullExplanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            // 관련 Android / AOSP API 태그
            if (item.relatedApis.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "관련 API / 구성요소:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item.relatedApis.forEach { api ->
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = api,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

