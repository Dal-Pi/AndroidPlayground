package com.kania.androidplayground.ui.battery

/**
 * 프레임워크에서 지원하는 가능한 상태값 항목 정의
 */
data class PossibleState(
    val constantName: String,
    val label: String,
    val description: String,
    val isCurrent: Boolean
)

/**
 * 실시간 배터리 상태 및 전력(W) 계산 데이터
 */
data class BatteryState(
    val isCharging: Boolean = false,
    val statusCode: Int = -1,
    val statusString: String = "알 수 없음",
    val pluggedCode: Int = -1,
    val plugTypeString: String = "미충전 (배터리 전원)",
    val levelPercent: Int = -1,
    val voltageMv: Int = 0,
    val voltageV: Double = 0.0,
    val currentNowMa: Double = 0.0,
    val currentAverageMa: Double? = null,
    val powerWatts: Double = 0.0,
    val chargingSpeedCategory: String = "미충전",
    val temperatureC: Double = 0.0,
    val healthCode: Int = -1,
    val healthString: String = "알 수 없음",
    val technology: String = "Li-ion",
    val chargeCounterMah: Int? = null,
    val timestampMillis: Long = System.currentTimeMillis()
)

/**
 * 개별 배터리 상세 지표 아이템 (코드, ADB 명령어 및 가능한 상태 목록 포함)
 */
data class BatteryDetailItem(
    val label: String,
    val value: String,
    val description: String? = null,
    val codeSnippet: String? = null,
    val adbCommand: String? = null,
    val isHighlight: Boolean = false,
    val possibleStates: List<PossibleState> = emptyList()
)
