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
        // Android 표준: 충전 시 양수(+), 방전 시 음수(-)
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
            // uAh -> mAh
            if (chargeCounterRaw > 50000) chargeCounterRaw / 1000 else chargeCounterRaw
        } else null

        return BatteryState(
            isCharging = isCharging,
            statusString = statusString,
            plugTypeString = plugTypeString,
            levelPercent = levelPercent,
            voltageMv = voltageMv,
            voltageV = voltageV,
            currentNowMa = currentNowMa,
            currentAverageMa = currentAvgMa,
            powerWatts = powerWatts,
            chargingSpeedCategory = speedCategory,
            temperatureC = tempC,
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

        // 제조사별 부호 편차 보정: 충전 중일 때는 순수 유입 전류 크기(양수)로 취급
        val magnitude = abs(rawCurrent.toDouble())

        // 10,000 미만이면 이미 mA 단위로 제공하는 기기일 가능성 높음
        return if (magnitude > 10000) {
            magnitude / 1000.0 // uA -> mA
        } else {
            magnitude // 이미 mA
        }
    }

    /**
     * 화면에 표시할 상세 지표 목록 (코드 스니펫 & ADB 명령어 포함)
     */
    fun getDetailItems(state: BatteryState): List<BatteryDetailItem> {
        val items = mutableListOf<BatteryDetailItem>()

        // 1. 충전 여부
        items.add(
            BatteryDetailItem(
                label = "충전 여부 (isCharging)",
                value = if (state.isCharging) "충전 중 (YES)" else "충전 중 아님 (NO)",
                description = "기기가 외부 전원에 연결되어 충전 중인지 여부",
                codeSnippet = "batteryManager.isCharging // or EXTRA_STATUS == CHARGING",
                adbCommand = "adb shell dumpsys battery | grep -E \"powered|status\"",
                isHighlight = true
            )
        )

        // 2. 실시간 충전/소비 전력 (W)
        val wattsFormatted = String.format(Locale.US, "%.2f W", state.powerWatts)
        items.add(
            BatteryDetailItem(
                label = "실시간 전력 (Watt)",
                value = if (state.isCharging) "$wattsFormatted (${state.chargingSpeedCategory})" else "0.00 W (방전 중)",
                description = "P = V(전압) × I(전류) 공식으로 계산된 배터리 셀 실시간 충전 전력",
                codeSnippet = "val watts = (voltageMv / 1000.0) * (abs(currentNowUa) / 1000000.0)",
                adbCommand = "adb shell \"dumpsys battery | grep -E 'voltage|current'\"",
                isHighlight = true
            )
        )

        // 3. 충전 전원 연결 방식
        items.add(
            BatteryDetailItem(
                label = "충전 방식 (Plugged)",
                value = state.plugTypeString,
                description = "AC 어댑터, USB, 무선 충전 등 전원 공급 경로",
                codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)",
                adbCommand = "adb shell dumpsys battery | grep -E \"AC powered|USB powered|Wireless powered\""
            )
        )

        // 4. 배터리 단자 전압 (Voltage)
        val voltageFormatted = String.format(Locale.US, "%d mV (%.3f V)", state.voltageMv, state.voltageV)
        items.add(
            BatteryDetailItem(
                label = "배터리 전압 (Voltage)",
                value = voltageFormatted,
                description = "현재 배터리 셀 단자 전압",
                codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) // mV",
                adbCommand = "adb shell dumpsys battery | grep voltage"
            )
        )

        // 5. 실시간 유입/유출 전류 (Current Now)
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

        // 6. 평균 전류 (Current Average)
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

        // 7. 배터리 잔량 (%)
        items.add(
            BatteryDetailItem(
                label = "배터리 잔량 (Level)",
                value = "${state.levelPercent}%",
                description = "현재 충전 잔량 퍼센트",
                codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)",
                adbCommand = "adb shell dumpsys battery | grep level"
            )
        )

        // 8. 배터리 온도 (Temperature)
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

        // 9. 배터리 건강 상태 (Health)
        items.add(
            BatteryDetailItem(
                label = "배터리 상태 (Health)",
                value = state.healthString,
                description = "배터리 수명 및 이상(과열, 과전압 등) 상태",
                codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)",
                adbCommand = "adb shell dumpsys battery | grep health"
            )
        )

        // 10. 잔여 전하량 (Charge Counter)
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

        // 11. 배터리 소재/기술 (Technology)
        items.add(
            BatteryDetailItem(
                label = "배터리 기술 (Technology)",
                value = state.technology,
                description = "리튬 이온(Li-ion), 리튬 폴리머(Li-poly) 등",
                codeSnippet = "batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)",
                adbCommand = "adb shell dumpsys battery | grep technology"
            )
        )

        return items
    }
}
