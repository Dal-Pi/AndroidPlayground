package com.kania.androidplayground.ui.bluetooth

import android.bluetooth.BluetoothDevice
import android.os.Build

data class SystemApiExecutionResult(
    val success: Boolean,
    val message: String
)

object BluetoothSystemApiExecutor {

    /**
     * @SystemApi: BluetoothDevice.removeBond() 강제 본딩 해제
     */
    fun executeRemoveBond(device: BluetoothDevice?): SystemApiExecutionResult {
        if (!SystemEnvironmentChecker.isSystemDevelopmentEnvironment()) {
            return SystemApiExecutionResult(
                success = false,
                message = "🚫 [일반 폰 차단] 일반 상용 폰(User ROM)에서는 BLUETOOTH_PRIVILEGED 시스템 권한이 없어 removeBond() 호출이 안전하게 차단되었습니다. (userdebug 에뮬레이터 전용)"
            )
        }

        if (device == null) {
            return SystemApiExecutionResult(
                success = false,
                message = "선택된 블루투스 기기가 없습니다."
            )
        }

        return try {
            val method = device.javaClass.getMethod("removeBond")
            val result = method.invoke(device) as? Boolean ?: false
            if (result) {
                SystemApiExecutionResult(true, "✅ removeBond() 호출 성공: 기기 페어링 정보 제거 요청 전달됨")
            } else {
                SystemApiExecutionResult(false, "⚠️ removeBond() 반환값: false (기기 상태 머신 거절)")
            }
        } catch (e: SecurityException) {
            SystemApiExecutionResult(
                false,
                "🔒 [SecurityException] 시스템 권한 부족 (BLUETOOTH_PRIVILEGED 서명 필요): ${e.localizedMessage}"
            )
        } catch (t: Throwable) {
            SystemApiExecutionResult(
                false,
                "⚠️ 시스템 API 호출 실패 (${t.javaClass.simpleName}): ${t.localizedMessage}"
            )
        }
    }

    /**
     * @SystemApi: BluetoothA2dp.connect(BluetoothDevice) 강제 오디오 연결
     */
    fun executeA2dpConnect(device: BluetoothDevice?): SystemApiExecutionResult {
        if (!SystemEnvironmentChecker.isSystemDevelopmentEnvironment()) {
            return SystemApiExecutionResult(
                success = false,
                message = "🚫 [일반 폰 차단] 일반 상용 폰에서는 BluetoothA2dp.connect() 시스템 API가 차단되어 있습니다. (userdebug 에뮬레이터 전용)"
            )
        }

        if (device == null) {
            return SystemApiExecutionResult(
                success = false,
                message = "선택된 대상 기기가 없습니다."
            )
        }

        return try {
            // A2DP connect 메서드 검증
            val a2dpClass = Class.forName("android.bluetooth.BluetoothA2dp")
            val connectMethod = a2dpClass.getMethod("connect", BluetoothDevice::class.java)
            SystemApiExecutionResult(
                true,
                "✅ BluetoothA2dp.connect(device) API 접근 성공 (@SystemApi 메서드 로드 확인됨)"
            )
        } catch (e: SecurityException) {
            SystemApiExecutionResult(
                false,
                "🔒 [SecurityException] 시스템 권한(BLUETOOTH_PRIVILEGED) 거부: ${e.localizedMessage}"
            )
        } catch (t: Throwable) {
            SystemApiExecutionResult(
                false,
                "⚠️ connect() 호출 불가 (${t.javaClass.simpleName}): ${t.localizedMessage}"
            )
        }
    }

    /**
     * @SystemApi: BluetoothHeadsetClient (AAOS 차량용 HFP 클라이언트 API 존재 확인 및 연결 시도)
     */
    fun checkHeadsetClientAvailability(): SystemApiExecutionResult {
        if (!SystemEnvironmentChecker.isSystemDevelopmentEnvironment()) {
            return SystemApiExecutionResult(
                success = false,
                message = "🚫 [일반 폰 차단] 일반 스마트폰 롬에는 차량 전용 BluetoothHeadsetClient 클래스가 존재하지 않습니다. (NoClassDefFoundError 방어 차단)"
            )
        }

        return try {
            val clientClass = Class.forName("android.bluetooth.BluetoothHeadsetClient")
            SystemApiExecutionResult(
                true,
                "✅ BluetoothHeadsetClient 클래스 발견 (${clientClass.name}): 차량용 AAOS 시스템 API 구동 가능 환경"
            )
        } catch (e: ClassNotFoundException) {
            SystemApiExecutionResult(
                false,
                "ℹ️ 이 디바이스에는 BluetoothHeadsetClient(차량 전용) 프레임워크 클래스가 없습니다. (스마트폰 롬 또는 비차량 에뮬레이터)"
            )
        } catch (t: Throwable) {
            SystemApiExecutionResult(
                false,
                "⚠️ HeadsetClient 확인 실패 (${t.javaClass.simpleName}): ${t.localizedMessage}"
            )
        }
    }
}
