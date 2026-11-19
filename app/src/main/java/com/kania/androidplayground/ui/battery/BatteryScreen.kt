package com.kania.androidplayground.ui.battery

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryScreen(
    batteryState: BatteryState,
    isAutoRefresh: Boolean,
    onToggleAutoRefresh: () -> Unit,
    onManualRefresh: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val detailItems = BatteryDataProvider.getDetailItems(batteryState)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Battery (충전 & 전력 W)")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                actions = {
                    // 실시간 자동 갱신(1초 루프) 토글 버튼
                    IconButton(onClick = onToggleAutoRefresh) {
                        Icon(
                            imageVector = if (isAutoRefresh) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isAutoRefresh) "실시간 자동 갱신 일시정지" else "실시간 자동 갱신 시작",
                            tint = if (isAutoRefresh) Color(0xFFFFD54F) else Color.White
                        )
                    }
                    // 수동 새로고침
                    IconButton(onClick = onManualRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "수동 새로고침",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. 최상단 실시간 충전 전력(W) 대시보드 카드
            item {
                ChargingPowerBannerCard(
                    state = batteryState,
                    isAutoRefresh = isAutoRefresh
                )
            }

            // 2. 충전 W 계산 공식 및 기술적 한계 가이드 카드
            item {
                PowerCalculationGuideCard()
            }

            // 3. 배터리 세부 지표 목록 (헤더 + 리스트)
            item {
                Text(
                    text = "📊 실시간 배터리 측정 지표",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(detailItems) { item ->
                BatteryDetailRowCard(item = item)
            }
        }
    }
}

/**
 * 최상단 실시간 충전 전력(W) 대형 카드
 */
@Composable
private fun ChargingPowerBannerCard(
    state: BatteryState,
    isAutoRefresh: Boolean
) {
    val isCharging = state.isCharging
    val bannerBgColor = if (isCharging) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (isCharging) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bannerBgColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 상단 상태 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isCharging) Icons.Default.Bolt else Icons.Default.PowerOff,
                        contentDescription = null,
                        tint = if (isCharging) Color(0xFFE65100) else contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = if (isCharging) "충전 중 (Charging)" else "배터리 전원 사용 중",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isAutoRefresh) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = if (isAutoRefresh) "LIVE (1초 갱신)" else "PAUSED",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(color = contentColor.copy(alpha = 0.2f))

            // 중앙 대형 와트(W) 표시 (충전 및 방전 시 상시 표시)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val signPrefix = if (isCharging && state.powerWatts > 0.0) "+" else if (!isCharging && state.powerWatts > 0.0) "-" else ""
                    Text(
                        text = String.format(Locale.US, "%s%.2f W", signPrefix, state.powerWatts),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isCharging) MaterialTheme.colorScheme.primary else contentColor
                    )
                    if (isCharging && state.isPowerEstimated) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = "추정치",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (isCharging) {
                    Text(
                        text = state.chargingSpeedCategory,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE65100)
                    )
                } else {
                    Text(
                        text = if (state.powerWatts > 0.0) "실시간 배터리 방전 소모 전력" else "배터리 전원 사용 중 (충전기 연결 시 충전 전력 측정)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor.copy(alpha = 0.85f)
                    )
                }

                // 센서 0이거나 에뮬레이터/완충 진단 메시지 박스
                if (!state.powerDiagnosticMessage.isNullOrEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = state.powerDiagnosticMessage,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 하단 3단 스펙 요약 (전압, 전류, 충전방식)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PowerSubStat(
                        label = "배터리 전압",
                        value = String.format(Locale.US, "%.3f V", state.voltageV)
                    )
                    PowerSubStat(
                        label = "충전/방전 전류",
                        value = if (state.currentNowMa > 0.0) {
                            String.format(Locale.US, "%.1f mA", state.currentNowMa)
                        } else {
                            if (state.isPowerEstimated) "센서 0mA" else "0.0 mA"
                        }
                    )
                    PowerSubStat(
                        label = "충전 잔량",
                        value = "${state.levelPercent}%"
                    )
                }
            }
        }
    }
}

@Composable
private fun PowerSubStat(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * 충전 전력(W) 계산 원리 및 기술적 한계 설명 카드
 */
@Composable
private fun PowerCalculationGuideCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
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
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "💡 안드로이드 배터리 W(와트) 계산 원리 & 한계",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider()

            Text(
                text = "1. 전력 계산 공식: P = V × I",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "• 전압 (V): Intent.EXTRA_VOLTAGE (배터리 단자 전압, mV)\n• 전류 (I): BatteryManager.BATTERY_PROPERTY_CURRENT_NOW (배터리 셀 유입 전류, uA)\n• 계산: (Voltage_mV / 1000.0) × (Current_mA / 1000.0) = 실시간 전력(W)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "2. ⚠️ 왜 25W/45W 충전기를 꽂아도 25W로 나오지 않을까요?",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC62828)
            )
            Text(
                text = "• 배터리 셀 유입 전력 vs 충전기 입력 전력:\n  안드로이드 표준 API는 '배터리 셀(Cell)로 유입되는 최종 전력'만 알려줍니다. 충전기의 고전압(9V/15V/20V)을 PMIC가 4.2~4.5V로 변환하는 손실과 화면/CPU 소모량이 빠지므로 실제 배터리 전력은 15~20W 수준으로 측정됩니다.\n• 충전 곡선(CC/CV):\n  배터리 잔량이 80%를 넘으면 수명 보호를 위해 충전 전력이 5~10W 이하로 급격히 떨어집니다.\n• 충전기 협상 전력(Vbus) API 부재:\n  어댑터의 입력 스펙(USB-PD 25W/45W)을 읽는 UsbPortStatus는 시스템 전용(@SystemApi)입니다.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 세부 배터리 지표 아이템 카드 (API 코드, ADB 명령어 및 가능한 상태값 목록 포함)
 */
@Composable
private fun BatteryDetailRowCard(
    item: BatteryDetailItem
) {
    val context = LocalContext.current
    var isStatesExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. 메인 라벨 & 값
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        copyToClipboard(context, item.label, item.value, "${item.label} 값 복사됨: ${item.value}")
                    }
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!item.description.isNullOrEmpty()) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (item.isHighlight) FontWeight.Bold else FontWeight.Normal,
                        color = if (item.isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "복사",
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // 2. 가능한 상태값 목록 (상태값을 가진 항목인 경우 표시)
            if (item.possibleStates.isNotEmpty()) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                    thickness = 0.5.dp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isStatesExpanded = !isStatesExpanded }
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "가능한 상태 목록 (${item.possibleStates.size})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = if (isStatesExpanded) "탭하여 접기" else "탭하여 전체 상태 보기",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Icon(
                        imageVector = if (isStatesExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isStatesExpanded) "접기" else "펼치기",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }

                AnimatedVisibility(visible = isStatesExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item.possibleStates.forEach { possibleState ->
                            PossibleStateItemView(state = possibleState)
                        }
                    }
                }
            }

            // 3. 약한 구분선 및 API 소스 코드 설명 (Monospace)
            if (!item.codeSnippet.isNullOrEmpty()) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 0.5.dp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            copyToClipboard(context, "API 코드", item.codeSnippet, "API 코드 복사됨: ${item.codeSnippet}")
                        }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.codeSnippet,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "API 복사",
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // 4. 약한 구분선 및 ADB 명령어 설명 (Monospace)
            if (!item.adbCommand.isNullOrEmpty()) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    thickness = 0.5.dp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            copyToClipboard(context, "ADB 커맨드", item.adbCommand, "ADB 명령어 복사됨: ${item.adbCommand}")
                        }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.adbCommand,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "ADB 복사",
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

/**
 * 상태값 항목 뷰 (현재 활성화된 상태는 초록색 배지와 강조 테두리로 표시)
 */
@Composable
private fun PossibleStateItemView(
    state: PossibleState
) {
    val isCurrent = state.isCurrent

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        color = if (isCurrent) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        },
        border = if (isCurrent) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = state.label,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = state.constantName,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = if (isCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline
                    )
                }
                Text(
                    text = state.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            if (isCurrent) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF2E7D32)
                ) {
                    Text(
                        text = "현재 활성",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, textToCopy: String, toastMessage: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText(label, textToCopy)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
}
