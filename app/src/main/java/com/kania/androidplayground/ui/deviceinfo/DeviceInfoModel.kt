package com.kania.androidplayground.ui.deviceinfo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SettingsSystemDaydream
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 디바이스 정보 카테고리 정의
 */
enum class DeviceInfoCategory(
    val title: String,
    val description: String,
    val icon: ImageVector
) {
    OS_PLATFORM(
        title = "OS & 플랫폼",
        description = "안드로이드 OS 버전, API 레벨, 보안 패치 등",
        icon = Icons.Default.SettingsSystemDaydream
    ),
    HARDWARE(
        title = "기기 & 하드웨어",
        description = "제조사, 모델명, 보드, 폼팩터 등",
        icon = Icons.Default.PhoneAndroid
    ),
    CPU_SOC(
        title = "CPU & 아키텍처",
        description = "지원 ABI (arm64, x86 등), 코어 수, 커널 정보",
        icon = Icons.Default.Speed
    ),
    DISPLAY(
        title = "디스플레이",
        description = "해상도, 화면 밀도(DPI), 주사율(Hz) 등",
        icon = Icons.Default.Tv
    ),
    MEMORY_STORAGE(
        title = "메모리 & 저장소",
        description = "RAM 용량, 내부 저장공간, JVM 힙 메모리",
        icon = Icons.Default.Storage
    ),
    BATTERY(
        title = "배터리 & 전원",
        description = "충전 상태, 잔량, 배터리 온도 및 건강 상태",
        icon = Icons.Default.BatteryChargingFull
    ),
    RUNTIME_SYSTEM(
        title = "런타임 & 환경",
        description = "ART 버전, 부팅 후 경과 시간, 에뮬레이터 여부",
        icon = Icons.Default.Memory
    )
}

/**
 * 개별 기기 정보 아이템
 */
data class DeviceInfoItem(
    val label: String,
    val value: String,
    val description: String? = null,
    val isMonospace: Boolean = false,
    val codeSnippet: String? = null,
    val adbCommand: String? = null
)

/**
 * 카테고리별 정보 그룹
 */
data class DeviceCategoryGroup(
    val category: DeviceInfoCategory,
    val items: List<DeviceInfoItem>
)

/**
 * 상단 요약 카드용 핵심 기기 요약 정보
 */
data class DeviceSummary(
    val model: String,
    val manufacturer: String,
    val androidVersion: String,
    val apiLevel: Int,
    val primaryAbi: String,
    val isEmulator: Boolean
)

/**
 * 전체 디바이스 정보
 */
data class FullDeviceInfo(
    val summary: DeviceSummary,
    val groups: List<DeviceCategoryGroup>
)
