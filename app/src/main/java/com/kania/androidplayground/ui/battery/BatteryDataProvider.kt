package com.kania.androidplayground.ui.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import java.util.Locale
import kotlin.math.abs

object BatteryDataProvider {

    /**
     * 현재 배터리 상태 및 실시간 충전 전력(W) 수집
     */
    fun getBatteryState(context: Context): BatteryState {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager

        // Sticky broadcast Intent 수신
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryIntent: Intent? = context.registerReceiver(null, intentFilter)

        // 1. 충전 여부 판별
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isChargingFromIntent = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        val isCharging = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            batteryManager?.isCharging ?: isChargingFromIntent
        } else {
            isChargingFromIntent
        }

        val statusString = when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "충전 중 (Charging)"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "방전 중 (Discharging)"
            BatteryManager.BATTERY_STATUS_FULL -> "완충됨 (Full 100%)"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "충전 중 아님 (Not Charging)"
            else -> if (isCharging) "충전 중" else "방전 중"
        }

        // 2. 전원 연결 방식
        val plugged = batteryIntent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val plugTypeString = when (plugged) {
            BatteryManager.BATTERY_PLUGGED_AC -> "유선 AC 충전기 (Wall Charger)"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB 포트 (PC / 저속 전원)"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "무선 충전 (Wireless Qi)"
            4 -> "도크 충전 (Dock)" // BatteryManager.BATTERY_PLUGGED_DOCK
            else -> if (isCharging) "충전기 연결됨" else "미충전 (배터리 전원 사용)"
        }

        // 3. 배터리 잔량 (%)
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val levelPercent = if (level != -1 && scale > 0) (level * 100 / scale.toFloat()).toInt() else -1

        // 4. 전압 (Voltage: mV -> V)
        val voltageMv = batteryIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
        val voltageV = voltageMv / 1000.0

        // 5. 전류 (Current: 마이크로암페어 -> 밀리암페어)
        var rawCurrentNow = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) ?: 0
        val rawCurrentAvg = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)

        // 기기별 단위 보정: 대부분 microampere(uA)지만, 절댓값이 10,000 미만인 기기는 이미 mA 단위일 수 있음
        val currentNowMa = convertToMilliamperes(rawCurrentNow, isCharging)
        val currentAvgMa = rawCurrentAvg?.let { convertToMilliamperes(it, isCharging) }

        // 6. 전력(W) 계산: P = V(Volt) * I(Ampere)
        val powerWatts = if (isCharging && voltageV > 0 && currentNowMa > 0) {
            (voltageV * (currentNowMa / 1000.0))
        } else if (!isCharging && voltageV > 0 && currentNowMa > 0) {
            // 방전 소비 전력
            (voltageV * (currentNowMa / 1000.0))
        } else {
            0.0
        }

        // 7. 충전 속도 등급 분류
        val speedCategory = if (!isCharging) {
            "배터리 방전 중"
        } else {
            when {
                powerWatts >= 25.0 -> "초고속 충전 (Super Fast, 25W+)"
                powerWatts >= 15.0 -> "고속 충전 (Fast Charging, 15~25W)"
                powerWatts >= 5.0 -> "일반 유선 충전 (Standard, 5~15W)"
                powerWatts > 0.0 -> "저속 충전 (Slow Charging, ~5W)"
                else -> "충전 준비 중 (측정 대기)"
            }
        }

        // 8. 기타 배터리 건강, 온도, 기술
        val tempRaw = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val tempC = tempRaw / 10.0

        val health = batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
        val healthString = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "좋음 (Good)"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "과열 (Overheat)"
            BatteryManager.BATTERY_HEALTH_DEAD -> "수명 다함 (Dead)"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "과전압 (Over Voltage)"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "오류 (Failure)"
            BatteryManager.BATTERY_HEALTH_COLD -> "저온 (Cold)"
            else -> "정상 (Normal)"
        }

        val technology = batteryIntent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"

        // 잔여 전하량 (charge_counter)
        val chargeCounterRaw = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        val chargeCounterMah = if (chargeCounterRaw != null && chargeCounterRaw > 0) {
            if (chargeCounterRaw > 50000) chargeCounterRaw / 1000 else chargeCounterRaw
        } else null

        return BatteryState(
            isCharging = isCharging,
            statusCode = status,
            statusString = statusString,
            pluggedCode = plugged,
            plugTypeString = plugTypeString,
            levelPercent = levelPercent,
            voltageMv = voltageMv,
            voltageV = voltageV,
            currentNowMa = currentNowMa,
            currentAverageMa = currentAvgMa,
            powerWatts = powerWatts,
            chargingSpeedCategory = speedCategory,
            temperatureC = tempC,
            healthCode = health,
            healthString = healthString,
            technology = technology,
            chargeCounterMah = chargeCounterMah
        )
    }

    /**
     * CURRENT_NOW 마이크로암페어(uA) 값을 밀리암페어(mA)로 변환 및 부호/단위 보정
     */
    private fun convertToMilliamperes(rawCurrent: Int, isCharging: Boolean): Double {
        if (rawCurrent == 0 || rawCurrent == Int.MIN_VALUE) return 0.0
        val magnitude = abs(rawCurrent.toDouble())
        return if (magnitude > 10000) {
            magnitude / 1000.0 // uA -> mA
        } else {
            magnitude // 이미 mA
        }
    }

    /**
     * 화면에 표시할 상세 지표 목록 (코드 스니펫, ADB 명령어 및 가능한 상태값 목록 포함)
     */
    fun getDetailItems(state: BatteryState): List<BatteryDetailItem> {
        val items = mutableListOf<BatteryDetailItem>()

        // 1. 충전 여부
        items.add(
            BatteryDetailItem(
                label = "충전 여부 (isCharging)",
                value = if (state.isCharging) "충전 중 (YES)" else "충전 중 아님 (NO)",
                description = "기기가 외부 전원에 연결되어 배터리가 충전 중인지 여부",
                codeSnippet = "batteryManager.isCharging // or status == CHARGING || FULL",
                adbCommand = "adb shell dumpsys battery | grep -E \"powered|status\"",
                isHighlight = true
            )
        )

        // 2. 충전 상태 (EXTRA_STATUS) - 상태값 목록 제공
        val statusPossibleStates = listOf(
            PossibleState(
                constantName = "BATTERY_STATUS_CHARGING (2)",
                label = "충전 중 (Charging)",
                description = "전원에 연결되어 배터리에 전류가 유입되는 정상 상태",
                isCurrent = state.statusCode == BatteryManager.BATTERY_STATUS_CHARGING
            ),
            PossibleState(
                constantName = "BATTERY_STATUS_DISCHARGING (3)",
                label = "방전 중 (Discharging)",
                description = "외부 전원이 분리되어 배터리 전력으로 기기가 동작 중인 상태",
                isCurrent = state.statusCode == BatteryManager.BATTERY_STATUS_DISCHARGING
            ),
            PossibleState(
                constantName = "BATTERY_STATUS_FULL (5)",
                label = "완충됨 (Full 100%)",
                description = "배터리가 100% 충전 완료된 상태",
                isCurrent = state.statusCode == BatteryManager.BATTERY_STATUS_FULL
            ),
            PossibleState(
                constantName = "BATTERY_STATUS_NOT_CHARGING (4)",
                label = "충전 중 아님 (Not Charging)",
                description = "충전기가 연결되었으나 배터리 보호 기능(85% 보호 충전), 과열 등으로 충전이 차단된 상태",
                isCurrent = state.statusCode == BatteryManager.BATTERY_STATUS_NOT_CHARGING
            ),
            PossibleState(
                constantName = "BATTERY_STATUS_UNKNOWN (1)",
                label = "알 수 없음 (Unknown)",
                description = "충전 상태를 파악할 수 없는 상태",
                isCurrent = state.statusCode == BatteryManager.BATTERY_STATUS_UNKNOWN
            )
        )
        items.add(
            BatteryDetailItem(
                label = "충전 상태 (Status)",
                value = state.statusString,
                description = "안드로이드 OS 배터리 프레임워크가 발행하는 현재 충전 상태",
                codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)",
                adbCommand = "adb shell dumpsys battery | grep status",
                isHighlight = true,
                possibleStates = statusPossibleStates
            )
        )

        // 3. 실시간 충전/소비 전력 (W) - 등급 목록 제공
        val wattsFormatted = String.format(Locale.US, "%.2f W", state.powerWatts)
        val speedPossibleStates = listOf(
            PossibleState(
                constantName = "Super Fast (25W+)",
                label = "초고속 충전 (Super Fast)",
                description = "USB-PD PPS 고속 프로토콜로 25W 이상의 초급속 전력 유입",
                isCurrent = state.isCharging && state.powerWatts >= 25.0
            ),
            PossibleState(
                constantName = "Fast (15~25W)",
                label = "고속 충전 (Fast Charging)",
                description = "QC, AFC, USB-PD 18W 등 일반 고속 어댑터 전력 유입",
                isCurrent = state.isCharging && state.powerWatts in 15.0..<25.0
            ),
            PossibleState(
                constantName = "Standard (5~15W)",
                label = "일반 유선 충전 (Standard)",
                description = "5V 2A 일반 유선 어댑터 규격 전력 유입",
                isCurrent = state.isCharging && state.powerWatts in 5.0..<15.0
            ),
            PossibleState(
                constantName = "Slow (~5W)",
                label = "저속 충전 (Slow Charging)",
                description = "PC USB 2.0/3.0 포트나 저출력 전원 규격 유입",
                isCurrent = state.isCharging && state.powerWatts in 0.01..<5.0
            ),
            PossibleState(
                constantName = "Discharging (방전)",
                label = "배터리 방전 중",
                description = "충전기가 연결되지 않아 배터리가 소모되는 상태",
                isCurrent = !state.isCharging
            )
        )
        items.add(
            BatteryDetailItem(
                label = "실시간 전력 (Watt)",
                value = if (state.isCharging) "$wattsFormatted (${state.chargingSpeedCategory})" else "0.00 W (방전 중)",
                description = "P = V(전압) × I(전류) 공식으로 계산된 배터리 셀 실시간 충전 전력",
                codeSnippet = "val watts = (voltageMv / 1000.0) * (abs(currentNowUa) / 1000000.0)",
                adbCommand = "adb shell \"dumpsys battery | grep -E 'voltage|current'\"",
                isHighlight = true,
                possibleStates = speedPossibleStates
            )
        )

        // 4. 충전 전원 연결 방식 (EXTRA_PLUGGED) - 상태값 목록 제공
        val pluggedPossibleStates = listOf(
            PossibleState(
                constantName = "0 (Unplugged)",
                label = "미충전 (배터리 전원)",
                description = "외부 전원 미연결 상태",
                isCurrent = state.pluggedCode <= 0
            ),
            PossibleState(
                constantName = "BATTERY_PLUGGED_AC (1)",
                label = "유선 AC 충전기",
                description = "벽면 AC 어댑터 유선 연결 (일반/고속/초고속 전원)",
                isCurrent = state.pluggedCode == BatteryManager.BATTERY_PLUGGED_AC
            ),
            PossibleState(
                constantName = "BATTERY_PLUGGED_USB (2)",
                label = "USB 포트 연결",
                description = "PC USB 포트 등 저속 데이터 호스트 전원",
                isCurrent = state.pluggedCode == BatteryManager.BATTERY_PLUGGED_USB
            ),
            PossibleState(
                constantName = "BATTERY_PLUGGED_WIRELESS (4)",
                label = "무선 충전 (Wireless Qi)",
                description = "무선 충전 패드 / 거치대 전력 전송",
                isCurrent = state.pluggedCode == BatteryManager.BATTERY_PLUGGED_WIRELESS
            ),
            PossibleState(
                constantName = "BATTERY_PLUGGED_DOCK (8)",
                label = "도크 충전 (Dock)",
                description = "스마트 디스플레이 및 태블릿 도킹 스테이션 전원 (Android 13+)",
                isCurrent = state.pluggedCode == 4 || state.pluggedCode == 8
            )
        )
        items.add(
            BatteryDetailItem(
                label = "충전 방식 (Plugged)",
                value = state.plugTypeString,
                description = "AC 어댑터, USB, 무선 충전 등 전원 공급 경로",
                codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)",
                adbCommand = "adb shell dumpsys battery | grep -E \"AC powered|USB powered|Wireless powered\"",
                possibleStates = pluggedPossibleStates
            )
        )

        // 5. 배터리 건강 상태 (EXTRA_HEALTH) - 상태값 목록 제공
        val healthPossibleStates = listOf(
            PossibleState(
                constantName = "BATTERY_HEALTH_GOOD (2)",
                label = "좋음 (Good)",
                description = "배터리 셀이 정상적으로 전압과 온도를 유지하는 상태",
                isCurrent = state.healthCode == BatteryManager.BATTERY_HEALTH_GOOD
            ),
            PossibleState(
                constantName = "BATTERY_HEALTH_OVERHEAT (3)",
                label = "과열 (Overheat)",
                description = "배터리 온도가 안전 임계치를 초과하여 충전이 제한된 상태",
                isCurrent = state.healthCode == BatteryManager.BATTERY_HEALTH_OVERHEAT
            ),
            PossibleState(
                constantName = "BATTERY_HEALTH_DEAD (4)",
                label = "수명 다함 (Dead)",
                description = "배터리가 영구적으로 손상되었거나 전압 유지가 불가능한 상태",
                isCurrent = state.healthCode == BatteryManager.BATTERY_HEALTH_DEAD
            ),
            PossibleState(
                constantName = "BATTERY_HEALTH_OVER_VOLTAGE (5)",
                label = "과전압 (Over Voltage)",
                description = "비규격 충전기 등으로 허용 전압 한계를 초과한 상태",
                isCurrent = state.healthCode == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE
            ),
            PossibleState(
                constantName = "BATTERY_HEALTH_COLD (7)",
                label = "저온 (Cold)",
                description = "빙점 이하 혹한 환경으로 인해 화학 반응 저하 및 충전 전류가 제한된 상태",
                isCurrent = state.healthCode == BatteryManager.BATTERY_HEALTH_COLD
            ),
            PossibleState(
                constantName = "BATTERY_HEALTH_UNSPECIFIED_FAILURE (6)",
                label = "미지정 오류 (Failure)",
                description = "원인 불명의 하드웨어 레벨 배터리 오류",
                isCurrent = state.healthCode == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE
            ),
            PossibleState(
                constantName = "BATTERY_HEALTH_UNKNOWN (1)",
                label = "알 수 없음 (Unknown)",
                description = "건강 상태를 판별할 수 없는 상태",
                isCurrent = state.healthCode == BatteryManager.BATTERY_HEALTH_UNKNOWN
            )
        )
        items.add(
            BatteryDetailItem(
                label = "배터리 상태 (Health)",
                value = state.healthString,
                description = "배터리 수명 및 이상(과열, 과전압 등) 상태",
                codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)",
                adbCommand = "adb shell dumpsys battery | grep health",
                possibleStates = healthPossibleStates
            )
        )

        // 6. 배터리 단자 전압 (Voltage)
        val voltageFormatted = String.format(Locale.US, "%d mV (%.3f V)", state.voltageMv, state.voltageV)
        items.add(
            BatteryDetailItem(
                label = "배터리 전압 (Voltage)",
                value = voltageFormatted,
                description = "현재 배터리 셀 단자 전압 (완충 시 약 4.2~4.45V, 방전 시 3.5~3.7V)",
                codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) // mV",
                adbCommand = "adb shell dumpsys battery | grep voltage"
            )
        )

        // 7. 실시간 유입/유출 전류 (Current Now)
        val currentFormatted = String.format(Locale.US, "%.1f mA", state.currentNowMa)
        items.add(
            BatteryDetailItem(
                label = "실시간 전류 (Current Now)",
                value = currentFormatted,
                description = if (state.isCharging) "배터리로 유입되는 실시간 충전 전류" else "배터리에서 소모되는 방전 전류",
                codeSnippet = "batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)",
                adbCommand = "adb shell cat /sys/class/power_supply/battery/current_now  # (단위: uA)"
            )
        )

        // 8. 평균 전류 (Current Average)
        if (state.currentAverageMa != null && state.currentAverageMa > 0) {
            val avgFormatted = String.format(Locale.US, "%.1f mA", state.currentAverageMa)
            items.add(
                BatteryDetailItem(
                    label = "평균 전류 (Current Avg)",
                    value = avgFormatted,
                    description = "일정 기간 동안의 이동 평균 전류",
                    codeSnippet = "batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE)",
                    adbCommand = "adb shell cat /sys/class/power_supply/battery/current_avg"
                )
            )
        }

        // 9. 배터리 잔량 (%)
        items.add(
            BatteryDetailItem(
                label = "배터리 잔량 (Level)",
                value = "${state.levelPercent}%",
                description = "현재 충전 잔량 퍼센트 (0~100%)",
                codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)",
                adbCommand = "adb shell dumpsys battery | grep level"
            )
        )

        // 10. 배터리 온도 (Temperature)
        val tempFormatted = String.format(Locale.US, "%.1f ℃", state.temperatureC)
        items.add(
            BatteryDetailItem(
                label = "배터리 온도 (Temperature)",
                value = tempFormatted,
                description = "배터리 내부 서미스터 측정 온도 (0.1℃ 단위)",
                codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0",
                adbCommand = "adb shell dumpsys battery | grep temperature"
            )
        )

        // 11. 잔여 전하량 (Charge Counter)
        if (state.chargeCounterMah != null) {
            items.add(
                BatteryDetailItem(
                    label = "현재 충전량 (Charge Counter)",
                    value = "${state.chargeCounterMah} mAh",
                    description = "배터리에 남아있는 실시간 전하량",
                    codeSnippet = "batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)",
                    adbCommand = "adb shell cat /sys/class/power_supply/battery/charge_counter"
                )
            )
        }

        // 12. 배터리 소재/기술 (Technology)
        items.add(
            BatteryDetailItem(
                label = "배터리 기술 (Technology)",
                value = state.technology,
                description = "리튬 이온(Li-ion), 리튬 폴리머(Li-poly) 등 화학적 소재",
                codeSnippet = "batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)",
                adbCommand = "adb shell dumpsys battery | grep technology"
            )
        )

        return items
    }
}
