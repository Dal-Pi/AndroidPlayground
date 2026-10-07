package com.kania.androidplayground.ui.wifi

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.material.icons.filled.WifiLock
import androidx.compose.material.icons.filled.WifiOff
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kania.androidplayground.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiScreen(
    hasLocationPermission: Boolean,
    hasNearbyWifiPermission: Boolean,
    hardwareFeatures: WifiHardwareFeatures,
    currentConnection: CurrentWifiConnection,
    scannedList: List<ScannedWifiItem>,
    isScanning: Boolean,
    onRequestPermissions: () -> Unit,
    onOpenWifiSettings: () -> Unit,
    onRefreshState: () -> Unit,
    onTriggerScan: () -> Unit
) {
    val activity = LocalContext.current as? Activity
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Public API 실습", "AOSP vs Public 비교", "주요 용어집")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.playground_wifi))
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
                    hasLocationPermission = hasLocationPermission,
                    hasNearbyWifiPermission = hasNearbyWifiPermission,
                    hardwareFeatures = hardwareFeatures,
                    currentConnection = currentConnection,
                    scannedList = scannedList,
                    isScanning = isScanning,
                    onRequestPermissions = onRequestPermissions,
                    onOpenWifiSettings = onOpenWifiSettings,
                    onTriggerScan = onTriggerScan
                )
                1 -> AospComparisonTab()
                2 -> GlossaryTab()
            }
        }
    }
}

@Composable
private fun PublicApiDemoTab(
    hasLocationPermission: Boolean,
    hasNearbyWifiPermission: Boolean,
    hardwareFeatures: WifiHardwareFeatures,
    currentConnection: CurrentWifiConnection,
    scannedList: List<ScannedWifiItem>,
    isScanning: Boolean,
    onRequestPermissions: () -> Unit,
    onOpenWifiSettings: () -> Unit,
    onTriggerScan: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. 권한 현황 카드
        item {
            PermissionCard(
                hasLocationPermission = hasLocationPermission,
                hasNearbyWifiPermission = hasNearbyWifiPermission,
                onRequestPermissions = onRequestPermissions
            )
        }

        // 2. 현재 연결된 Wi-Fi 정보 카드
        item {
            CurrentConnectionCard(
                currentConnection = currentConnection,
                hasPermission = hasLocationPermission || hasNearbyWifiPermission,
                onRequestPermissions = onRequestPermissions
            )
        }

        // 3. Wi-Fi 하드웨어 & 기능 지원 카드
        item {
            HardwareFeaturesCard(
                hardwareFeatures = hardwareFeatures,
                onOpenWifiSettings = onOpenWifiSettings
            )
        }

        // 4. Wi-Fi 스캔 테스트 카드
        item {
            WifiScanCard(
                hasPermission = hasLocationPermission || hasNearbyWifiPermission,
                isScanning = isScanning,
                scannedList = scannedList,
                onTriggerScan = onTriggerScan,
                onRequestPermissions = onRequestPermissions
            )
        }
    }
}

@Composable
private fun PermissionCard(
    hasLocationPermission: Boolean,
    hasNearbyWifiPermission: Boolean,
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
                    text = "Wi-Fi 런타임 권한 현황",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Wi-Fi SSID/BSSID 및 주변 AP 스캔 결과를 취득하려면 위치 권한 또는 Android 13+ NEARBY_WIFI_DEVICES 권한이 필수입니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ACCESS_FINE_LOCATION",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "SSID, BSSID 및 스캔 결과 조회 (Android 12 이하 필수)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Icon(
                    imageVector = if (hasLocationPermission) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = if (hasLocationPermission) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "NEARBY_WIFI_DEVICES",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "위치 없이 주변 Wi-Fi 장치 탐색 (Android 13+ API 33+)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Icon(
                    imageVector = if (hasNearbyWifiPermission) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = if (hasNearbyWifiPermission) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                )
            }

            if (!hasLocationPermission && !hasNearbyWifiPermission) {
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
private fun CurrentConnectionCard(
    currentConnection: CurrentWifiConnection,
    hasPermission: Boolean,
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
                    imageVector = if (currentConnection.isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = if (currentConnection.isConnected) MaterialTheme.colorScheme.primary else Color.Gray
                )
                Text(
                    text = "현재 연결된 Wi-Fi 정보 (WifiInfo)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "WifiManager 및 NetworkCapabilities Public API를 통해 연결 상태를 실시간 확인합니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            HorizontalDivider()

            if (!currentConnection.isConnected) {
                Text(
                    text = "현재 연결된 Wi-Fi가 없습니다. (모바일 데이터 사용 중이거나 Wi-Fi 꺼짐)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                InfoRow(label = "연결 상태", value = "연결됨 (Connected)")
                InfoRow(label = "SSID (네트워크 이름)", value = currentConnection.ssid)
                InfoRow(label = "BSSID (AP MAC 주소)", value = currentConnection.bssid)
                InfoRow(label = "신호 세기 (RSSI)", value = "${currentConnection.rssi} dBm")
                InfoRow(label = "링크 전송 속도", value = "${currentConnection.linkSpeedMbps} Mbps")
                InfoRow(label = "주파수 / 대역", value = "${currentConnection.frequencyMhz} MHz (${currentConnection.frequencyBand})")
                InfoRow(label = "Wi-Fi 규격", value = currentConnection.wifiStandard)
                InfoRow(label = "할당된 사설 IP", value = currentConnection.ipAddress)
                InfoRow(label = "단말 MAC 주소", value = currentConnection.macAddress)

                if (!hasPermission) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚠️ 위치/Nearby 권한이 없으면 SSID가 '<unknown ssid>'로 표시되고 BSSID가 숨겨집니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD32F2F)
                    )
                    Button(onClick = onRequestPermissions) {
                        Text(text = "권한 허용하여 상세 정보 보기")
                    }
                }
            }

            Text(
                text = "💡 AOSP 차이점: 일반 앱은 프라이버시 보호를 위해 항상 무작위 MAC을 보게 되며, 실제 하드웨어 MAC 취득은 AOSP 시스템 앱에만 허용됩니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun HardwareFeaturesCard(
    hardwareFeatures: WifiHardwareFeatures,
    onOpenWifiSettings: () -> Unit
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
                    text = "Wi-Fi 어댑터 & 하드웨어 기능",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            InfoRow(label = "Wi-Fi 활성화 상태", value = if (hardwareFeatures.isWifiEnabled) "ON (활성화됨)" else "OFF (꺼짐)")
            InfoRow(label = "Wi-Fi State", value = hardwareFeatures.wifiStateName)

            HorizontalDivider()

            Text(
                text = "고급 하드웨어 및 보안 프로토콜 지원 여부:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            SupportRow(label = "5GHz 대역 지원", isSupported = hardwareFeatures.is5GHzSupported)
            SupportRow(label = "6GHz 대역 지원 (Wi-Fi 6E/7)", isSupported = hardwareFeatures.is6GHzSupported)
            SupportRow(label = "Wi-Fi Direct (P2P) 지원", isSupported = hardwareFeatures.isP2pSupported)
            SupportRow(label = "WPA3-Personal (SAE) 지원", isSupported = hardwareFeatures.isWpa3SaeSupported)
            SupportRow(label = "WPA3-Enterprise (Suite B) 지원", isSupported = hardwareFeatures.isWpa3SuiteBSupported)
            SupportRow(label = "Enhanced Open (OWE) 지원", isSupported = hardwareFeatures.isEnhancedOpenSupported)
            SupportRow(label = "Easy Connect (DPP) 지원", isSupported = hardwareFeatures.isEasyConnectSupported)

            HorizontalDivider()

            OutlinedButton(
                onClick = onOpenWifiSettings,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Wi-Fi 시스템 설정 열기")
            }

            Text(
                text = "💡 AOSP 차이점: 일반 앱은 wifiManager.setWifiEnabled(boolean) 호출이 차단되어 Intent로 설정 화면을 유도해야 합니다. AOSP 시스템 앱은 직접 ON/OFF 가능.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun WifiScanCard(
    hasPermission: Boolean,
    isScanning: Boolean,
    scannedList: List<ScannedWifiItem>,
    onTriggerScan: () -> Unit,
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
                    imageVector = Icons.Default.WifiFind,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "주변 Wi-Fi 스캔 (발견: ${scannedList.size}개)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "WifiManager.startScan() 및 ScanResult Public API를 사용합니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )

            Text(
                text = "⚠️ Android Scan Throttling 안내:\n포그라운드 일반 앱은 2분당 최대 4회까지만 스캔이 허용됩니다. 빈번한 스캔 호출 시 OS가 요청을 무시합니다.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFE65100),
                lineHeight = 16.sp
            )

            if (!hasPermission) {
                Button(onClick = onRequestPermissions) {
                    Text(text = "스캔을 위해 위치 권한 허용")
                }
            } else {
                Button(
                    onClick = onTriggerScan,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isScanning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = if (isScanning) "스캔 요청 중..." else "주변 Wi-Fi 스캔 요청 (startScan)")
                }
            }

            if (scannedList.isEmpty()) {
                Text(
                    text = if (isScanning) "주변 무선 네트워크를 스캔하고 있습니다..." else "스캔 버튼을 눌러 주변 Wi-Fi AP를 탐색해 보세요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    scannedList.take(15).forEach { item ->
                        OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (item.capabilities.contains("WPA") || item.capabilities.contains("WEP")) Icons.Default.WifiLock else Icons.Default.Wifi,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(end = 2.dp)
                                        )
                                        Text(
                                            text = item.ssid.ifEmpty { "(Hidden SSID)" },
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${item.bssid} | ${item.frequencyMhz}MHz | ${item.standardName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = item.capabilities,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline,
                                        maxLines = 1
                                    )
                                }
                                Text(
                                    text = "${item.rssi} dBm",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.rssi > -65) Color(0xFF2E7D32) else Color(0xFFE65100)
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
            AospWifiComparisonItem(
                category = "어댑터 제어",
                feature = "Wi-Fi 강제 ON / OFF",
                publicStatus = "불가 (차단됨)",
                publicExplanation = "wifiManager.setWifiEnabled(boolean) 호출은 Android 10(Q)부터 일반 앱에서 호출 시 무시되거나 deprecated됨. Intent로 설정 열기만 가능.",
                aospExplanation = "AOSP WifiServiceImpl.setWifiEnabled()를 시스템 앱(Settings/SystemUI)에서 직접 호출하여 백그라운드에서 즉시 Wi-Fi를 켜거나 끔."
            ),
            AospWifiComparisonItem(
                category = "네트워크 연결",
                feature = "특정 Wi-Fi 강제 연결 (connect)",
                publicStatus = "제한적 (Suggestion / Specifier)",
                publicExplanation = "connect(), enableNetwork(), disconnect()는 Android 10+ 차단됨. 일반 앱은 Suggestion(추천) 목록에 등록하거나, 로컬 기기용 Specifier로 사용자 다이얼로그 승인을 받아야 함.",
                aospExplanation = "WifiConfiguration을 직접 생성하여 WifiManager.connect(netId)를 호출, 사용자 확인 다이얼로그 없이 원하는 AP로 즉시 강제 연결 가능."
            ),
            AospWifiComparisonItem(
                category = "네트워크 저장/삭제",
                feature = "Wi-Fi 프로파일 저장 (save / forget)",
                publicStatus = "불가 (SystemApi)",
                publicExplanation = "일반 앱은 자신이 등록한 Suggestion만 삭제 가능. 기기에 저장된 다른 Wi-Fi 프로파일을 읽거나 삭제하는 forget()은 호출 불가.",
                aospExplanation = "AOSP WifiConfigStore / Settings 앱에서 기기 저장소(/data/misc/apexdata/com.android.wifi/WifiConfigStore.xml)의 모든 저장된 Wi-Fi를 직접 수정/삭제."
            ),
            AospWifiComparisonItem(
                category = "식별자 & MAC",
                feature = "단말기 실제 물리 MAC 주소",
                publicStatus = "불가 (무작위 MAC 강제)",
                publicExplanation = "Android 10부터 MAC 무작위화가 기본 강제되며, wifiInfo.macAddress는 항상 '02:00:00:00:00:00'을 반환함.",
                aospExplanation = "네이티브 드라이버 및 시스템 서비스는 칩셋 NVRAM에 기록된 실제 하드웨어 팩토리 MAC 주소를 취득 및 설정 가능."
            ),
            AospWifiComparisonItem(
                category = "스캔 정책",
                feature = "스캔 빈도 및 백그라운드 스캔",
                publicStatus = "강력한 Throttling 규제",
                publicExplanation = "포그라운드 앱은 2분당 최대 4회, 백그라운드 앱은 30분당 1회로 OS가 스캔 빈도를 강제 차단함.",
                aospExplanation = "wificond 및 WifiScanner 시스템 서비스를 통해 PNO(Preferred Network Offload) 스캔 및 상시 스케줄링 스캔 직접 제어 가능."
            ),
            AospWifiComparisonItem(
                category = "테더링 & 핫스팟",
                feature = "SoftAP (모바일 핫스팟) 제어",
                publicStatus = "제한적 (LocalOnlyHotspot만 가능)",
                publicExplanation = "일반 앱은 인터넷 공유가 안 되는 로컬 기기 간 연결용 LocalOnlyHotspot만 켤 수 있음. 실제 셀룰러 인터넷 공유 테더링은 불가.",
                aospExplanation = "TetheringManager 및 hostapd 데몬을 호출하여 셀룰러 데이터를 라우팅하는 정식 모바일 핫스팟을 직접 켜고 끄며 SSID/비밀번호 제어."
            ),
            AospWifiComparisonItem(
                category = "네이티브 스택",
                feature = "wpa_supplicant & wificond",
                publicStatus = "접근 불가",
                publicExplanation = "Android Framework Binder IPC를 통해서만 접근 가능하며, 네이티브 소켓이나 데몬 제어 불가.",
                aospExplanation = "external/wpa_supplicant_8 C 코드 수정, wificond 데몬 및 리눅스 커널 nl80211 무선 드라이버 직접 수정 및 포팅 가능."
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
                        text = "📌 AOSP Wi-Fi 개발자를 위한 핵심 요약",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "안드로이드는 배터리 소모와 무단 네트워크 하이재킹, 위치 추적을 방지하기 위해 Android 10(Q) 이후 일반 앱의 Wi-Fi 제어 기능(강제 ON/OFF, 강제 연결/삭제, 하드웨어 MAC 조회)을 전면 차단했습니다.\n\nAOSP 레벨에서는 system_server 내부의 WifiServiceImpl과 네이티브 wpa_supplicant/hostapd/wificond 데몬, 그리고 NETWORK_SETTINGS 시스템 권한을 통해 Wi-Fi 스택 전체를 완전히 통제할 수 있습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
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
private fun GlossaryTab() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("전체") }

    val categories = remember {
        listOf("전체", "무선 규격 & 대역", "식별자 & 네트워크", "보안 & 인증", "Android 연결 API", "AOSP & 시스템 아키텍처")
    }

    val filteredItems = remember(searchQuery, selectedCategory) {
        WifiGlossaryData.items.filter { item ->
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
                        text = "📖 Wi-Fi & AOSP 주요 용어 및 약어 사전",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Wi-Fi 통신 및 AOSP 무선 스택(wpa_supplicant, hostapd 등)에서 사용되는 핵심 약어, 보안 규격, 프로토콜을 정리한 사전입니다. 검색창과 필터 칩을 활용해 보세요.",
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
                placeholder = { Text(text = "용어 검색 (예: SSID, WPA3, Throttling, supplicant...)") },
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
private fun GlossaryCard(item: WifiGlossaryItem) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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

            Text(
                text = item.summary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Text(
                text = item.fullExplanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

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

@Composable
private fun InfoRow(
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
private fun SupportRow(
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
