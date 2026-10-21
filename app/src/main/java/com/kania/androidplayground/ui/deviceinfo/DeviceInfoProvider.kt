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

        items.add(
            DeviceInfoItem(
                label = "Android OS 버전",
                value = Build.VERSION.RELEASE,
                description = "사용자에게 표시되는 안드로이드 버전",
                codeSnippet = "Build.VERSION.RELEASE",
                adbCommand = "adb shell getprop ro.build.version.release"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "API 레벨 (SDK_INT)",
                value = Build.VERSION.SDK_INT.toString(),
                description = "프레임워크 API 레벨",
                codeSnippet = "Build.VERSION.SDK_INT",
                adbCommand = "adb shell getprop ro.build.version.sdk"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "코드명 (Codename)",
                value = Build.VERSION.CODENAME,
                description = "개발 버전 코드명",
                codeSnippet = "Build.VERSION.CODENAME",
                adbCommand = "adb shell getprop ro.build.version.codename"
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val patch = Build.VERSION.SECURITY_PATCH
            items.add(
                DeviceInfoItem(
                    label = "보안 패치 레벨",
                    value = patch.ifEmpty { "없음" },
                    description = "구글 보안 패치 발행일자",
                    codeSnippet = "Build.VERSION.SECURITY_PATCH",
                    adbCommand = "adb shell getprop ro.build.version.security_patch"
                )
            )
        }

        items.add(
            DeviceInfoItem(
                label = "빌드 번호 (Display)",
                value = Build.DISPLAY,
                description = "빌드 표시 문자열",
                isMonospace = true,
                codeSnippet = "Build.DISPLAY",
                adbCommand = "adb shell getprop ro.build.display.id"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "빌드 ID (Build.ID)",
                value = Build.ID,
                description = "빌드 식별자",
                isMonospace = true,
                codeSnippet = "Build.ID",
                adbCommand = "adb shell getprop ro.build.id"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "빌드 타입 (Type)",
                value = Build.TYPE,
                description = "user, userdebug, eng",
                codeSnippet = "Build.TYPE",
                adbCommand = "adb shell getprop ro.build.type"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "빌드 태그 (Tags)",
                value = Build.TAGS ?: "없음",
                description = "release-keys 또는 test-keys",
                codeSnippet = "Build.TAGS",
                adbCommand = "adb shell getprop ro.build.tags"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "증분 버전 (Incremental)",
                value = Build.VERSION.INCREMENTAL,
                description = "내부 빌드 버전 코드",
                codeSnippet = "Build.VERSION.INCREMENTAL",
                adbCommand = "adb shell getprop ro.build.version.incremental"
            )
        )

        val buildDate = DateFormat.format("yyyy-MM-dd HH:mm:ss", Date(Build.TIME)).toString()
        items.add(
            DeviceInfoItem(
                label = "빌드 시각 (Build Time)",
                value = buildDate,
                description = "AOSP/ROM 컴파일 일시",
                codeSnippet = "Date(Build.TIME) // Epoch millis",
                adbCommand = "adb shell getprop ro.build.date.utc"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "빌드 지문 (Fingerprint)",
                value = Build.FINGERPRINT,
                description = "기기 및 빌드 고유 식별 지문",
                isMonospace = true,
                codeSnippet = "Build.FINGERPRINT",
                adbCommand = "adb shell getprop ro.build.fingerprint"
            )
        )

        return DeviceCategoryGroup(DeviceInfoCategory.OS_PLATFORM, items)
    }

    private fun getHardwareGroup(context: Context, isEmulator: Boolean): DeviceCategoryGroup {
        val items = mutableListOf<DeviceInfoItem>()

        items.add(
            DeviceInfoItem(
                label = "제조사 (Manufacturer)",
                value = Build.MANUFACTURER,
                description = "디바이스 제조 기업",
                codeSnippet = "Build.MANUFACTURER",
                adbCommand = "adb shell getprop ro.product.manufacturer"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "브랜드 (Brand)",
                value = Build.BRAND,
                description = "소비자용 브랜드 명칭",
                codeSnippet = "Build.BRAND",
                adbCommand = "adb shell getprop ro.product.brand"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "모델명 (Model)",
                value = Build.MODEL,
                description = "최종 마케팅 모델 번호",
                codeSnippet = "Build.MODEL",
                adbCommand = "adb shell getprop ro.product.model"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "제품명 (Product)",
                value = Build.PRODUCT,
                description = "AOSP TARGET_PRODUCT 명칭",
                codeSnippet = "Build.PRODUCT",
                adbCommand = "adb shell getprop ro.product.name"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "디바이스 코드명 (Device)",
                value = Build.DEVICE,
                description = "보드/기기 개발 코드명",
                codeSnippet = "Build.DEVICE",
                adbCommand = "adb shell getprop ro.product.device"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "메인보드 (Board)",
                value = Build.BOARD,
                description = "시스템 기판 명칭",
                codeSnippet = "Build.BOARD",
                adbCommand = "adb shell getprop ro.product.board"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "하드웨어 명칭 (Hardware)",
                value = Build.HARDWARE,
                description = "SoC/칩셋 하드웨어 식별명",
                codeSnippet = "Build.HARDWARE",
                adbCommand = "adb shell getprop ro.hardware"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "부트로더 버전 (Bootloader)",
                value = Build.BOOTLOADER,
                description = "시스템 부트로더 릴리즈",
                codeSnippet = "Build.BOOTLOADER",
                adbCommand = "adb shell getprop ro.bootloader"
            )
        )

        val formFactor = determineFormFactor(context)
        items.add(
            DeviceInfoItem(
                label = "기기 폼팩터 (Form Factor)",
                value = formFactor,
                description = "기기 카테고리 (Phone, Tablet, Automotive 등)",
                codeSnippet = "packageManager.hasSystemFeature() & smallestScreenWidthDp",
                adbCommand = "adb shell pm list features | grep -E \"automotive|watch|leanback\""
            )
        )
        items.add(
            DeviceInfoItem(
                label = "에뮬레이터 여부 (Is Emulator)",
                value = if (isEmulator) "예 (Emulator)" else "아니오 (Real Device)",
                codeSnippet = "Build.FINGERPRINT, MODEL, HARDWARE 등 종합 휴리스틱 판별",
                adbCommand = "adb shell getprop ro.kernel.qemu"
            )
        )

        return DeviceCategoryGroup(DeviceInfoCategory.HARDWARE, items)
    }

    private fun getCpuSocGroup(primaryAbi: String): DeviceCategoryGroup {
        val items = mutableListOf<DeviceInfoItem>()

        items.add(
            DeviceInfoItem(
                label = "기본 아키텍처 (Primary ABI)",
                value = primaryAbi,
                description = "앱 구동 시 기본 사용하는 ABI",
                codeSnippet = "Build.SUPPORTED_ABIS.firstOrNull()",
                adbCommand = "adb shell getprop ro.product.cpu.abi"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "지원 ABI 목록",
                value = Build.SUPPORTED_ABIS.joinToString(", ").ifEmpty { "없음" },
                description = "기기가 바이너리로 실행 가능한 전체 ABI",
                codeSnippet = "Build.SUPPORTED_ABIS.joinToString(\", \")",
                adbCommand = "adb shell getprop ro.product.cpu.abilist"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "64비트 지원 ABI",
                value = Build.SUPPORTED_64_BIT_ABIS.joinToString(", ").ifEmpty { "미지원 (32-bit 전용)" },
                codeSnippet = "Build.SUPPORTED_64_BIT_ABIS.joinToString(\", \")",
                adbCommand = "adb shell getprop ro.product.cpu.abilist64"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "32비트 지원 ABI",
                value = Build.SUPPORTED_32_BIT_ABIS.joinToString(", ").ifEmpty { "미지원 (64-bit Only)" },
                codeSnippet = "Build.SUPPORTED_32_BIT_ABIS.joinToString(\", \")",
                adbCommand = "adb shell getprop ro.product.cpu.abilist32"
            )
        )

        val cores = Runtime.getRuntime().availableProcessors()
        items.add(
            DeviceInfoItem(
                label = "사용 가능한 CPU 코어 수",
                value = "$cores Cores",
                description = "현재 런타임에서 인식 가능한 논리 프로세서 수",
                codeSnippet = "Runtime.getRuntime().availableProcessors()",
                adbCommand = "adb shell nproc  # 또는 adb shell cat /proc/cpuinfo"
            )
        )

        val kernelVersion = System.getProperty("os.version") ?: "알 수 없음"
        val osArch = System.getProperty("os.arch") ?: "알 수 없음"
        items.add(
            DeviceInfoItem(
                label = "Linux 커널 버전",
                value = kernelVersion,
                description = "하위 리눅스 커널 릴리즈",
                isMonospace = true,
                codeSnippet = "System.getProperty(\"os.version\")",
                adbCommand = "adb shell uname -r  # 또는 cat /proc/version"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "OS 아키텍처 (os.arch)",
                value = osArch,
                description = "JVM 기준 OS 구조",
                codeSnippet = "System.getProperty(\"os.arch\")",
                adbCommand = "adb shell uname -m"
            )
        )

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

        items.add(
            DeviceInfoItem(
                label = "물리 해상도 (Resolution)",
                value = "${widthPx} × ${heightPx} px",
                description = "현재 화면의 가로×세로 픽셀",
                codeSnippet = "metrics.widthPixels, metrics.heightPixels",
                adbCommand = "adb shell wm size"
            )
        )

        val widthDp = (widthPx / density).toInt()
        val heightDp = (heightPx / density).toInt()
        items.add(
            DeviceInfoItem(
                label = "화면 크기 (DP)",
                value = "${widthDp} × ${heightDp} dp",
                description = "밀도 독립적 가상 픽셀 (dp)",
                codeSnippet = "widthPx / density, heightPx / density",
                adbCommand = "adb shell wm size  # (dp = px / (dpi / 160))"
            )
        )

        val bucket = getDensityBucket(densityDpi)
        items.add(
            DeviceInfoItem(
                label = "화면 밀도 (DPI)",
                value = "$densityDpi dpi ($bucket)",
                description = "1인치당 점의 개수 및 리소스 버킷",
                codeSnippet = "resources.displayMetrics.densityDpi",
                adbCommand = "adb shell wm density"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "밀도 배율 (Scale Factor)",
                value = "${density}x (폰트 배율: ${scaledDensity}x)",
                description = "mdpi(1.0x) 대비 배율",
                codeSnippet = "resources.displayMetrics.density",
                adbCommand = "adb shell wm density  # (scale = dpi / 160.0)"
            )
        )

        val refreshRate = getRefreshRate(context)
        items.add(
            DeviceInfoItem(
                label = "화면 주사율 (Refresh Rate)",
                value = "${refreshRate.toInt()} Hz (${"%.1f".format(refreshRate)} Hz)",
                description = "디스플레이 초당 화면 갱신율",
                codeSnippet = "display.refreshRate // or defaultDisplay.refreshRate",
                adbCommand = "adb shell dumpsys display | grep -E \"mBaseDisplayInfo|fps\""
            )
        )

        val orientationStr = if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) "가로 모드 (Landscape)" else "세로 모드 (Portrait)"
        items.add(
            DeviceInfoItem(
                label = "현재 화면 방향",
                value = orientationStr,
                codeSnippet = "resources.configuration.orientation",
                adbCommand = "adb shell dumpsys input | grep SurfaceOrientation"
            )
        )

        val smallestWidthDp = config.smallestScreenWidthDp
        items.add(
            DeviceInfoItem(
                label = "최소 화면 너비 (sw)",
                value = "sw${smallestWidthDp}dp",
                description = "레이아웃 리소스 분기 기준 (sw600dp 등)",
                codeSnippet = "resources.configuration.smallestScreenWidthDp",
                adbCommand = "adb shell dumpsys window | grep -E \"mSmallestScreenWidthDp\""
            )
        )

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

            items.add(
                DeviceInfoItem(
                    label = "전체 RAM 용량 (Total RAM)",
                    value = totalRam,
                    description = "하드웨어 물리 메모리 용량",
                    codeSnippet = "activityManager.getMemoryInfo(memInfo); memInfo.totalMem",
                    adbCommand = "adb shell cat /proc/meminfo | grep MemTotal"
                )
            )
            items.add(
                DeviceInfoItem(
                    label = "사용 가능한 RAM (Avail RAM)",
                    value = availRam,
                    description = "현재 유휴 메모리",
                    codeSnippet = "memInfo.availMem",
                    adbCommand = "adb shell cat /proc/meminfo | grep MemAvailable"
                )
            )
            items.add(
                DeviceInfoItem(
                    label = "사용 중인 RAM",
                    value = "$usedRam ($usedPercent%)",
                    description = "시스템 및 백그라운드 앱 점유",
                    codeSnippet = "memInfo.totalMem - memInfo.availMem",
                    adbCommand = "adb shell dumpsys meminfo"
                )
            )
            items.add(
                DeviceInfoItem(
                    label = "메모리 부족 상태 (Low Memory)",
                    value = if (memInfo.lowMemory) "위험 (Low Memory 발생)" else "정상 (Normal)",
                    codeSnippet = "memInfo.lowMemory",
                    adbCommand = "adb shell dumpsys activity | grep -i \"mLowRam\""
                )
            )
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

            items.add(
                DeviceInfoItem(
                    label = "내부 저장소 총 용량",
                    value = totalStorage,
                    description = "/data 파티션 기준",
                    codeSnippet = "StatFs(Environment.getDataDirectory().path).totalBytes",
                    adbCommand = "adb shell df -h /data"
                )
            )
            items.add(
                DeviceInfoItem(
                    label = "내부 저장소 사용 가능",
                    value = availStorage,
                    codeSnippet = "stat.availableBytes",
                    adbCommand = "adb shell df -h /data"
                )
            )
            items.add(
                DeviceInfoItem(
                    label = "내부 저장소 사용 중",
                    value = "$usedStorage ($usedPercent%)",
                    codeSnippet = "stat.totalBytes - stat.availableBytes",
                    adbCommand = "adb shell df -h /data"
                )
            )
        } catch (_: Exception) {
            items.add(DeviceInfoItem("내부 저장소 정보", "조회 실패"))
        }

        // 3. JVM Heap 정보
        val runtime = Runtime.getRuntime()
        val maxHeap = Formatter.formatFileSize(context, runtime.maxMemory())
        val totalHeap = Formatter.formatFileSize(context, runtime.totalMemory())
        val freeHeap = Formatter.formatFileSize(context, runtime.freeMemory())
        items.add(
            DeviceInfoItem(
                label = "JVM 최대 힙 메모리 (Max Heap)",
                value = maxHeap,
                description = "앱이 할당받을 수 있는 최대 힙 크기",
                codeSnippet = "Runtime.getRuntime().maxMemory()",
                adbCommand = "adb shell getprop dalvik.vm.heapsize"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "JVM 현재 할당 힙 (Total Heap)",
                value = totalHeap,
                codeSnippet = "Runtime.getRuntime().totalMemory()",
                adbCommand = "adb shell dumpsys meminfo <package_name>"
            )
        )
        items.add(
            DeviceInfoItem(
                label = "JVM 유휴 힙 (Free Heap)",
                value = freeHeap,
                codeSnippet = "Runtime.getRuntime().freeMemory()",
                adbCommand = "adb shell dumpsys meminfo <package_name>"
            )
        )

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
            items.add(
                DeviceInfoItem(
                    label = "배터리 잔량",
                    value = if (batteryPct != -1) "$batteryPct%" else "알 수 없음",
                    codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)",
                    adbCommand = "adb shell dumpsys battery | grep level"
                )
            )

            val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val statusStr = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "충전 중 (Charging)"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "방전 중 (Discharging)"
                BatteryManager.BATTERY_STATUS_FULL -> "완충됨 (Full)"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "충전 중 아님 (Not Charging)"
                else -> "알 수 없음"
            }
            items.add(
                DeviceInfoItem(
                    label = "충전 상태 (Status)",
                    value = statusStr,
                    codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)",
                    adbCommand = "adb shell dumpsys battery | grep status"
                )
            )

            val chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val plugStr = when (chargePlug) {
                BatteryManager.BATTERY_PLUGGED_AC -> "AC 어댑터 (유선 고속/일반)"
                BatteryManager.BATTERY_PLUGGED_USB -> "USB 포트 연결"
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> "무선 충전 (Wireless)"
                else -> "배터리 전원 사용 (미충전)"
            }
            items.add(
                DeviceInfoItem(
                    label = "전원 연결 방식 (Plugged)",
                    value = plugStr,
                    codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)",
                    adbCommand = "adb shell dumpsys battery | grep -E \"AC powered|USB powered|Wireless powered\""
                )
            )

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
            items.add(
                DeviceInfoItem(
                    label = "배터리 건강 상태 (Health)",
                    value = healthStr,
                    codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)",
                    adbCommand = "adb shell dumpsys battery | grep health"
                )
            )

            val temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
            if (temp > 0) {
                val tempC = temp / 10.0
                items.add(
                    DeviceInfoItem(
                        label = "배터리 온도",
                        value = "$tempC ℃",
                        codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10.0",
                        adbCommand = "adb shell dumpsys battery | grep temperature  # (단위: 0.1도)"
                    )
                )
            }

            val voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
            if (voltage > 0) {
                items.add(
                    DeviceInfoItem(
                        label = "배터리 전압",
                        value = "$voltage mV",
                        codeSnippet = "batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)",
                        adbCommand = "adb shell dumpsys battery | grep voltage"
                    )
                )
            }

            val tech = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
            if (!tech.isNullOrEmpty()) {
                items.add(
                    DeviceInfoItem(
                        label = "배터리 소재/기술",
                        value = tech,
                        codeSnippet = "batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)",
                        adbCommand = "adb shell dumpsys battery | grep technology"
                    )
                )
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
        items.add(
            DeviceInfoItem(
                label = "런타임 가상머신 (VM)",
                value = "$vmName (v$vmVersion)",
                description = "Android Runtime (ART)",
                codeSnippet = "System.getProperty(\"java.vm.name\") + \" v\" + System.getProperty(\"java.vm.version\")",
                adbCommand = "adb shell getprop persist.sys.dalvik.vm.lib.2"
            )
        )

        val uptimeMs = SystemClock.elapsedRealtime()
        val uptimeStr = formatDuration(uptimeMs)
        items.add(
            DeviceInfoItem(
                label = "부팅 후 경과 시간 (Uptime)",
                value = uptimeStr,
                description = "SystemClock.elapsedRealtime()",
                codeSnippet = "SystemClock.elapsedRealtime()",
                adbCommand = "adb shell uptime  # 또는 cat /proc/uptime"
            )
        )

        val uptimeWithoutSleepMs = SystemClock.uptimeMillis()
        items.add(
            DeviceInfoItem(
                label = "활성 시간 (Deep Sleep 제외)",
                value = formatDuration(uptimeWithoutSleepMs),
                codeSnippet = "SystemClock.uptimeMillis()",
                adbCommand = "adb shell dumpsys power | grep -E \"mWakeLock|mIsPowered\""
            )
        )

        val locale = Locale.getDefault()
        items.add(
            DeviceInfoItem(
                label = "기본 로케일 (Locale)",
                value = "${locale.displayName} (${locale.toLanguageTag()})",
                codeSnippet = "Locale.getDefault().toLanguageTag()",
                adbCommand = "adb shell getprop persist.sys.locale"
            )
        )

        val timeZone = TimeZone.getDefault()
        items.add(
            DeviceInfoItem(
                label = "시간대 (Timezone)",
                value = "${timeZone.id} (${timeZone.getDisplayName(false, TimeZone.SHORT)})",
                codeSnippet = "TimeZone.getDefault().id",
                adbCommand = "adb shell getprop persist.sys.timezone"
            )
        )

        items.add(
            DeviceInfoItem(
                label = "환경 구분",
                value = if (isEmulator) "가상머신 에뮬레이터 (Emulator)" else "실물 디바이스 (Physical Device)",
                codeSnippet = "Build.FINGERPRINT, HARDWARE 등 종합 판별",
                adbCommand = "adb shell getprop ro.kernel.qemu"
            )
        )

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
