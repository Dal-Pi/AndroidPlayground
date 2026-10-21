package com.kania.androidplayground.ui.deviceinfo

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.text.format.DateFormat
import android.text.format.Formatter
import android.view.WindowManager
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DeviceInfoProvider {

    fun getFullDeviceInfo(context: Context): FullDeviceInfo {
        val isEmulator = checkIsEmulator()
        val primaryAbi = Build.SUPPORTED_ABIS.firstOrNull() ?: "알 수 없음"

        val summary = DeviceSummary(
            model = Build.MODEL,
            manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() },
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            primaryAbi = primaryAbi,
            isEmulator = isEmulator
        )

        val groups = listOf(
            getOsPlatformGroup(),
            getHardwareGroup(context, isEmulator),
            getCpuSocGroup(primaryAbi),
            getDisplayGroup(context),
            getMemoryStorageGroup(context),
            getBatteryGroup(context),
            getRuntimeSystemGroup(context, isEmulator)
        )

        return FullDeviceInfo(summary = summary, groups = groups)
    }

    private fun getOsPlatformGroup(): DeviceCategoryGroup {
        val items = mutableListOf<DeviceInfoItem>()

        items.add(DeviceInfoItem("Android OS 버전", Build.VERSION.RELEASE, "사용자에게 표시되는 안드로이드 버전"))
        items.add(DeviceInfoItem("API 레벨 (SDK_INT)", Build.VERSION.SDK_INT.toString(), "프레임워크 API 레벨"))
        items.add(DeviceInfoItem("코드명 (Codename)", Build.VERSION.CODENAME, "개발 버전 코드명"))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val patch = Build.VERSION.SECURITY_PATCH
            items.add(DeviceInfoItem("보안 패치 레벨", patch.ifEmpty { "없음" }, "구글 보안 패치 발행일자"))
        }

        items.add(DeviceInfoItem("빌드 번호 (Display)", Build.DISPLAY, "빌드 표시 문자열", isMonospace = true))
        items.add(DeviceInfoItem("빌드 ID (Build.ID)", Build.ID, "빌드 식별자", isMonospace = true))
        items.add(DeviceInfoItem("빌드 타입 (Type)", Build.TYPE, "user, userdebug, eng"))
        items.add(DeviceInfoItem("빌드 태그 (Tags)", Build.TAGS ?: "없음", "release-keys 또는 test-keys"))
        items.add(DeviceInfoItem("증분 버전 (Incremental)", Build.VERSION.INCREMENTAL, "내부 빌드 버전 코드"))

        val buildDate = DateFormat.format("yyyy-MM-dd HH:mm:ss", Date(Build.TIME)).toString()
        items.add(DeviceInfoItem("빌드 시각 (Build Time)", buildDate, "AOSP/ROM 컴파일 일시"))
        items.add(DeviceInfoItem("빌드 지문 (Fingerprint)", Build.FINGERPRINT, "기기 및 빌드 고유 식별 지문", isMonospace = true))

        return DeviceCategoryGroup(DeviceInfoCategory.OS_PLATFORM, items)
    }

    private fun getHardwareGroup(context: Context, isEmulator: Boolean): DeviceCategoryGroup {
        val items = mutableListOf<DeviceInfoItem>()

        items.add(DeviceInfoItem("제조사 (Manufacturer)", Build.MANUFACTURER, "디바이스 제조 기업"))
        items.add(DeviceInfoItem("브랜드 (Brand)", Build.BRAND, "소비자용 브랜드 명칭"))
        items.add(DeviceInfoItem("모델명 (Model)", Build.MODEL, "최종 마케팅 모델 번호"))
        items.add(DeviceInfoItem("제품명 (Product)", Build.PRODUCT, "AOSP TARGET_PRODUCT 명칭"))
        items.add(DeviceInfoItem("디바이스 코드명 (Device)", Build.DEVICE, "보드/기기 개발 코드명"))
        items.add(DeviceInfoItem("메인보드 (Board)", Build.BOARD, "시스템 기판 명칭"))
        items.add(DeviceInfoItem("하드웨어 명칭 (Hardware)", Build.HARDWARE, "SoC/칩셋 하드웨어 식별명"))
        items.add(DeviceInfoItem("부트로더 버전 (Bootloader)", Build.BOOTLOADER, "시스템 부트로더 릴리즈"))

        val formFactor = determineFormFactor(context)
        items.add(DeviceInfoItem("기기 폼팩터 (Form Factor)", formFactor, "기기 카테고리 (Phone, Tablet, Automotive 등)"))
        items.add(DeviceInfoItem("에뮬레이터 여부 (Is Emulator)", if (isEmulator) "예 (Emulator)" else "아니오 (Real Device)"))

        return DeviceCategoryGroup(DeviceInfoCategory.HARDWARE, items)
    }

    private fun getCpuSocGroup(primaryAbi: String): DeviceCategoryGroup {
        val items = mutableListOf<DeviceInfoItem>()

        items.add(DeviceInfoItem("기본 아키텍처 (Primary ABI)", primaryAbi, "앱 구동 시 기본 사용하는 ABI"))
        items.add(DeviceInfoItem("지원 ABI 목록", Build.SUPPORTED_ABIS.joinToString(", ").ifEmpty { "없음" }, "기기가 바이너리로 실행 가능한 전체 ABI"))
        items.add(DeviceInfoItem("64비트 지원 ABI", Build.SUPPORTED_64_BIT_ABIS.joinToString(", ").ifEmpty { "미지원 (32-bit 전용)" }))
        items.add(DeviceInfoItem("32비트 지원 ABI", Build.SUPPORTED_32_BIT_ABIS.joinToString(", ").ifEmpty { "미지원 (64-bit Only)" }))

        val cores = Runtime.getRuntime().availableProcessors()
        items.add(DeviceInfoItem("사용 가능한 CPU 코어 수", "$cores Cores", "현재 런타임에서 인식 가능한 논리 프로세서 수"))

        val kernelVersion = System.getProperty("os.version") ?: "알 수 없음"
        val osArch = System.getProperty("os.arch") ?: "알 수 없음"
        items.add(DeviceInfoItem("Linux 커널 버전", kernelVersion, "하위 리눅스 커널 릴리즈", isMonospace = true))
        items.add(DeviceInfoItem("OS 아키텍처 (os.arch)", osArch, "JVM 기준 OS 구조"))

        return DeviceCategoryGroup(DeviceInfoCategory.CPU_SOC, items)
    }

    @Suppress("DEPRECATION")
    private fun getDisplayGroup(context: Context): DeviceCategoryGroup {
        val items = mutableListOf<DeviceInfoItem>()
        val metrics = context.resources.displayMetrics
        val config = context.resources.configuration

        val widthPx = metrics.widthPixels
        val heightPx = metrics.heightPixels
        val densityDpi = metrics.densityDpi
        val density = metrics.density
        val scaledDensity = metrics.scaledDensity

        items.add(DeviceInfoItem("물리 해상도 (Resolution)", "${widthPx} × ${heightPx} px", "현재 화면의 가로×세로 픽셀"))

        val widthDp = (widthPx / density).toInt()
        val heightDp = (heightPx / density).toInt()
        items.add(DeviceInfoItem("화면 크기 (DP)", "${widthDp} × ${heightDp} dp", "밀도 독립적 가상 픽셀 (dp)"))

        val bucket = getDensityBucket(densityDpi)
        items.add(DeviceInfoItem("화면 밀도 (DPI)", "$densityDpi dpi ($bucket)", "1인치당 점의 개수 및 리소스 버킷"))
        items.add(DeviceInfoItem("밀도 배율 (Scale Factor)", "${density}x (폰트 배율: ${scaledDensity}x)", "mdpi(1.0x) 대비 배율"))

        val refreshRate = getRefreshRate(context)
        items.add(DeviceInfoItem("화면 주사율 (Refresh Rate)", "${refreshRate.toInt()} Hz (${"%.1f".format(refreshRate)} Hz)", "디스플레이 초당 화면 갱신율"))

        val orientationStr = if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) "가로 모드 (Landscape)" else "세로 모드 (Portrait)"
        items.add(DeviceInfoItem("현재 화면 방향", orientationStr))

        val smallestWidthDp = config.smallestScreenWidthDp
        items.add(DeviceInfoItem("최소 화면 너비 (sw)", "sw${smallestWidthDp}dp", "레이아웃 리소스 분기 기준 (sw600dp 등)"))

        return DeviceCategoryGroup(DeviceInfoCategory.DISPLAY, items)
    }

    private fun getMemoryStorageGroup(context: Context): DeviceCategoryGroup {
        val items = mutableListOf<DeviceInfoItem>()

        // 1. RAM 정보
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        if (activityManager != null) {
            val memInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memInfo)

            val totalRam = Formatter.formatFileSize(context, memInfo.totalMem)
            val availRam = Formatter.formatFileSize(context, memInfo.availMem)
            val usedRam = Formatter.formatFileSize(context, memInfo.totalMem - memInfo.availMem)
            val usedPercent = ((memInfo.totalMem - memInfo.availMem).toDouble() / memInfo.totalMem * 100).toInt()

            items.add(DeviceInfoItem("전체 RAM 용량 (Total RAM)", totalRam, "하드웨어 물리 메모리 용량"))
            items.add(DeviceInfoItem("사용 가능한 RAM (Avail RAM)", availRam, "현재 유휴 메모리"))
            items.add(DeviceInfoItem("사용 중인 RAM", "$usedRam ($usedPercent%)", "시스템 및 백그라운드 앱 점유"))
            items.add(DeviceInfoItem("메모리 부족 상태 (Low Memory)", if (memInfo.lowMemory) "위험 (Low Memory 발생)" else "정상 (Normal)"))
        }

        // 2. 내부 저장소 정보
        try {
            val dataDir = Environment.getDataDirectory()
            val stat = StatFs(dataDir.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availBlocks = stat.availableBlocksLong

            val totalStorage = Formatter.formatFileSize(context, totalBlocks * blockSize)
            val availStorage = Formatter.formatFileSize(context, availBlocks * blockSize)
            val usedStorage = Formatter.formatFileSize(context, (totalBlocks - availBlocks) * blockSize)
            val usedPercent = (((totalBlocks - availBlocks).toDouble() / totalBlocks) * 100).toInt()

            items.add(DeviceInfoItem("내부 저장소 총 용량", totalStorage, "/data 파티션 기준"))
            items.add(DeviceInfoItem("내부 저장소 사용 가능", availStorage))
            items.add(DeviceInfoItem("내부 저장소 사용 중", "$usedStorage ($usedPercent%)"))
        } catch (_: Exception) {
            items.add(DeviceInfoItem("내부 저장소 정보", "조회 실패"))
        }

        // 3. JVM Heap 정보
        val runtime = Runtime.getRuntime()
        val maxHeap = Formatter.formatFileSize(context, runtime.maxMemory())
        val totalHeap = Formatter.formatFileSize(context, runtime.totalMemory())
        val freeHeap = Formatter.formatFileSize(context, runtime.freeMemory())
        items.add(DeviceInfoItem("JVM 최대 힙 메모리 (Max Heap)", maxHeap, "앱이 할당받을 수 있는 최대 힙 크기"))
        items.add(DeviceInfoItem("JVM 현재 할당 힙 (Total Heap)", totalHeap))
        items.add(DeviceInfoItem("JVM 유휴 힙 (Free Heap)", freeHeap))

        return DeviceCategoryGroup(DeviceInfoCategory.MEMORY_STORAGE, items)
    }

    private fun getBatteryGroup(context: Context): DeviceCategoryGroup {
        val items = mutableListOf<DeviceInfoItem>()

        // sticky broadcast intent 조회
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, intentFilter)

        if (batteryStatus != null) {
            val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = if (level != -1 && scale > 0) (level * 100 / scale.toFloat()).toInt() else -1
            items.add(DeviceInfoItem("배터리 잔량", if (batteryPct != -1) "$batteryPct%" else "알 수 없음"))

            val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val statusStr = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "충전 중 (Charging)"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "방전 중 (Discharging)"
                BatteryManager.BATTERY_STATUS_FULL -> "완충됨 (Full)"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "충전 중 아님 (Not Charging)"
                else -> "알 수 없음"
            }
            items.add(DeviceInfoItem("충전 상태 (Status)", statusStr))

            val chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val plugStr = when (chargePlug) {
                BatteryManager.BATTERY_PLUGGED_AC -> "AC 어댑터 (유선 고속/일반)"
                BatteryManager.BATTERY_PLUGGED_USB -> "USB 포트 연결"
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> "무선 충전 (Wireless)"
                else -> "배터리 전원 사용 (미충전)"
            }
            items.add(DeviceInfoItem("전원 연결 방식 (Plugged)", plugStr))

            val health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)
            val healthStr = when (health) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "좋음 (Good)"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "과열 (Overheat)"
                BatteryManager.BATTERY_HEALTH_DEAD -> "수명 다함 (Dead)"
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "과전압 (Over Voltage)"
                BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "실패 (Failure)"
                BatteryManager.BATTERY_HEALTH_COLD -> "저온 (Cold)"
                else -> "알 수 없음"
            }
            items.add(DeviceInfoItem("배터리 건강 상태 (Health)", healthStr))

            val temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            if (temp > 0) {
                val tempC = temp / 10.0
                items.add(DeviceInfoItem("배터리 온도", "$tempC ℃"))
            }

            val voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            if (voltage > 0) {
                items.add(DeviceInfoItem("배터리 전압", "$voltage mV"))
            }

            val tech = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
            if (!tech.isNullOrEmpty()) {
                items.add(DeviceInfoItem("배터리 소재/기술", tech))
            }
        } else {
            items.add(DeviceInfoItem("배터리 정보", "배터리 상태를 읽을 수 없습니다."))
        }

        return DeviceCategoryGroup(DeviceInfoCategory.BATTERY, items)
    }

    private fun getRuntimeSystemGroup(context: Context, isEmulator: Boolean): DeviceCategoryGroup {
        val items = mutableListOf<DeviceInfoItem>()

        val vmName = System.getProperty("java.vm.name") ?: "Dalvik / ART"
        val vmVersion = System.getProperty("java.vm.version") ?: "알 수 없음"
        items.add(DeviceInfoItem("런타임 가상머신 (VM)", "$vmName (v$vmVersion)", "Android Runtime (ART)"))

        val uptimeMs = SystemClock.elapsedRealtime()
        val uptimeStr = formatDuration(uptimeMs)
        items.add(DeviceInfoItem("부팅 후 경과 시간 (Uptime)", uptimeStr, "SystemClock.elapsedRealtime()"))

        val uptimeWithoutSleepMs = SystemClock.uptimeMillis()
        items.add(DeviceInfoItem("활성 시간 (Deep Sleep 제외)", formatDuration(uptimeWithoutSleepMs)))

        val locale = Locale.getDefault()
        items.add(DeviceInfoItem("기본 로케일 (Locale)", "${locale.displayName} (${locale.toLanguageTag()})"))

        val timeZone = TimeZone.getDefault()
        items.add(DeviceInfoItem("시간대 (Timezone)", "${timeZone.id} (${timeZone.getDisplayName(false, TimeZone.SHORT)})"))

        items.add(DeviceInfoItem("환경 구분", if (isEmulator) "가상머신 에뮬레이터 (Emulator)" else "실물 디바이스 (Physical Device)"))

        return DeviceCategoryGroup(DeviceInfoCategory.RUNTIME_SYSTEM, items)
    }

    private fun determineFormFactor(context: Context): String {
        val pm = context.packageManager
        return when {
            pm.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE) -> "차량용 인포테인먼트 (Automotive / AAOS)"
            pm.hasSystemFeature(PackageManager.FEATURE_WATCH) -> "스마트 워치 (Wear OS)"
            pm.hasSystemFeature(PackageManager.FEATURE_LEANBACK) -> "스마트 TV (Android TV)"
            context.resources.configuration.smallestScreenWidthDp >= 600 -> "태블릿 (Tablet)"
            else -> "스마트폰 (Smartphone)"
        }
    }

    private fun getDensityBucket(dpi: Int): String {
        return when {
            dpi <= 120 -> "ldpi"
            dpi <= 160 -> "mdpi"
            dpi <= 240 -> "hdpi"
            dpi <= 320 -> "xhdpi"
            dpi <= 480 -> "xxhdpi"
            dpi <= 640 -> "xxxhdpi"
            else -> "ultra-high dpi"
        }
    }

    @Suppress("DEPRECATION")
    private fun getRefreshRate(context: Context): Float {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display?.refreshRate ?: 60.0f
            } else {
                val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
                wm?.defaultDisplay?.refreshRate ?: 60.0f
            }
        } catch (_: Exception) {
            60.0f
        }
    }

    private fun formatDuration(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60)) % 24
        val days = millis / (1000 * 60 * 60 * 24)

        return if (days > 0) {
            "${days}일 ${hours}시간 ${minutes}분 ${seconds}초"
        } else if (hours > 0) {
            "${hours}시간 ${minutes}분 ${seconds}초"
        } else {
            "${minutes}분 ${seconds}초"
        }
    }

    private fun checkIsEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu"))
    }
}
