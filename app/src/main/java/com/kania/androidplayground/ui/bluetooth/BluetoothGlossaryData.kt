package com.kania.androidplayground.ui.bluetooth

object BluetoothGlossaryData {
    val items = listOf(
        // 1. 무선 규격 & 물리 계층
        GlossaryItem(
            term = "BLE (Bluetooth Low Energy)",
            category = "무선 규격 & 하드웨어",
            summary = "소량의 데이터를 저전력으로 간헐적 전송하기 위해 설계된 블루투스 표준 (Bluetooth 4.0+).",
            fullExplanation = "스마트워치, 헬스케어 밴드, 비콘 센서 등 배터리 용량이 작은 기기들을 위해 고안되었습니다. 항상 연결을 유지하기보다는 짧은 순간 패킷을 주고받고 대기(Sleep) 모드로 전환되어 배터리를 수개월~수년까지 유지할 수 있습니다.",
            relatedApis = listOf("BluetoothLeScanner", "BluetoothGatt", "BluetoothGattCallback")
        ),
        GlossaryItem(
            term = "Classic Bluetooth (BR/EDR)",
            category = "무선 규격 & 하드웨어",
            summary = "지속적이고 대용량의 데이터 및 음성/오디오 전송에 사용되는 전통적 블루투스 표준.",
            fullExplanation = "BR(Basic Rate, 1Mbps)과 EDR(Enhanced Data Rate, 2~3Mbps)을 포함합니다. 무선 헤드폰(A2DP), 핸즈프리 차량 통화(HFP), 대용량 파일 전송(OBEX)처럼 지속적인 스트리밍 대역폭이 필요할 때 사용됩니다. BLE보다 전력 소모가 큽니다.",
            relatedApis = listOf("BluetoothSocket", "BluetoothServerSocket", "BluetoothA2dp", "BluetoothHeadset")
        ),
        GlossaryItem(
            term = "Dual Mode (듀얼 모드)",
            category = "무선 규격 & 하드웨어",
            summary = "Classic Bluetooth(BR/EDR)와 BLE를 모두 지원하는 칩셋 또는 디바이스.",
            fullExplanation = "스마트폰이나 노트북, 태블릿은 대부분 듀얼 모드 장치(Bluetooth Smart Ready)입니다. Classic 오디오 기기와 연결하면서 동시에 BLE 피트니스 밴드와도 통신할 수 있습니다.",
            relatedApis = listOf("BluetoothDevice.DEVICE_TYPE_DUAL", "BluetoothDevice.type")
        ),
        GlossaryItem(
            term = "PHY (Physical Layer / 물리 계층)",
            category = "무선 규격 & 하드웨어",
            summary = "무선 전파(RF) 신호를 0과 1의 비트 데이터로 실제로 변조하고 송수신하는 가장 밑단의 물리 계층.",
            fullExplanation = "블루투스 5.0부터 기존 1M PHY 외에 2M PHY, Coded PHY가 추가되어 상황에 따라 속도(속도 우선) 또는 도달 거리(거리 우선)를 유연하게 선택할 수 있게 되었습니다.",
            relatedApis = listOf("BluetoothDevice.setPreferredPhy()", "BluetoothGattCallback.onPhyUpdate()")
        ),
        GlossaryItem(
            term = "LE 2M PHY",
            category = "무선 규격 & 하드웨어",
            summary = "전송 속도를 2배(2Mbps)로 높여 데이터 전송 시간을 단축하고 무선 점유율을 줄이는 블루투스 5.0 기술.",
            fullExplanation = "심볼 레이트를 2배로 올려 더 빠른 속도로 데이터를 전송합니다. 전송 시간이 절반으로 줄어들기 때문에 무선 송수신 칩셋이 깨어 있는 시간이 줄어들어 결과적으로 배터리 소모를 절감할 수 있습니다.",
            relatedApis = listOf("BluetoothAdapter.isLe2MPhySupported()", "BluetoothDevice.PHY_LE_2M_MASK")
        ),
        GlossaryItem(
            term = "LE Coded PHY (Long Range)",
            category = "무선 규격 & 하드웨어",
            summary = "전진 오류 정정(FEC) 코딩을 적용해 전송 거리를 최대 4배까지 확장하는 블루투스 5.0 기술.",
            fullExplanation = "송신 전력을 무작정 올리는 대신, 데이터 비트에 오류 정정 코드(S=2 또는 S=8)를 붙여 신호가 약하거나 벽에 가로막혀도 패킷 복원이 가능하도록 합니다. 속도는 125kbps~500kbps로 낮아지지만 야외나 넓은 공장, 스마트홈 환경에서 수백 미터까지 통신이 가능합니다.",
            relatedApis = listOf("BluetoothAdapter.isLeCodedPhySupported()", "BluetoothDevice.PHY_LE_CODED_MASK")
        ),
        GlossaryItem(
            term = "RSSI (Received Signal Strength Indicator)",
            category = "무선 규격 & 하드웨어",
            summary = "수신된 무선 신호의 세기를 나타내는 지표 (단위: dBm).",
            fullExplanation = "값이 0에 가까울수록 신호가 강하고(근거리), 음수 절대값이 클수록 신호가 약합니다(원거리). 통상 -30 ~ -50 dBm은 아주 가까운 거리(1m 이내), -70 dBm 전후는 일반적인 실내 거리, -90 dBm 이하는 연결이 끊어지기 직전의 한계 수치입니다.",
            relatedApis = listOf("ScanResult.rssi", "BluetoothGatt.readRemoteRssi()")
        ),

        // 2. 검색 & 광고 & 연결
        GlossaryItem(
            term = "Advertising (광고 / 애드버타이징)",
            category = "검색 & 연결 프로세스",
            summary = "주변 기기들이 자신을 찾을 수 있도록 무선 채널로 식별 정보 패킷을 주기적으로 방송(Broadcasting)하는 것.",
            fullExplanation = "스마트밴드나 이어폰 케이스를 열었을 때 스마트폰이 인식할 수 있는 것은 기기가 Advertising 패킷(기기 이름, 서비스 UUID, 제조사 데이터 등)을 계속 쏘고 있기 때문입니다. BLE는 37, 38, 39번 3개의 전용 광고 채널을 사용합니다.",
            relatedApis = listOf("BluetoothLeAdvertiser", "AdvertiseData", "AdvertiseSettings")
        ),
        GlossaryItem(
            term = "Scanning (스캔 / 탐색)",
            category = "검색 & 연결 프로세스",
            summary = "주변 기기들이 송출하는 Advertising 패킷을 라디오 수신기로 청취하여 디바이스를 발견하는 과정.",
            fullExplanation = "스마트폰이 주변 기기를 검색할 때 수행합니다. 스캔을 켜두면 라디오 수신기가 계속 켜져 있어 배터리가 빠르게 소모되므로, 필터(ScanFilter)를 걸고 필요할 때만 잠시 켜거나 화면이 꺼지면 중지하는 것이 일반 앱의 필수 권장사항입니다.",
            relatedApis = listOf("BluetoothLeScanner.startScan()", "ScanFilter", "ScanSettings")
        ),
        GlossaryItem(
            term = "Extended Advertising (확장 광고)",
            category = "검색 & 연결 프로세스",
            summary = "기존 31바이트 광고 패킷 크기 제한을 최대 254바이트 이상으로 확장한 블루투스 5.0 기능.",
            fullExplanation = "레거시 BLE는 광고 패킷 크기가 31바이트에 불과해 이름과 UUID 몇 개만 넣어도 꽉 찼습니다. 확장 광고는 기본 3개 채널에서 포인터만 전송하고 나머지 실제 데이터는 37개 데이터 채널을 통해 대용량으로 전송할 수 있게 해줍니다.",
            relatedApis = listOf("BluetoothAdapter.isLeExtendedAdvertisingSupported()", "AdvertisingSetParameters")
        ),
        GlossaryItem(
            term = "Periodic Advertising (주기적 광고)",
            category = "검색 & 연결 프로세스",
            summary = "일정한 주기마다 동기화된 데이터를 브로드캐스팅하는 블루투스 5.0 광고 방식.",
            fullExplanation = "연결을 맺지 않고도 1:N으로 센서 데이터나 위치 정보, 오디오 스트림(LE Audio Auracast)을 여러 수신기에 동시에 전송할 수 있습니다. 수신기는 송신 주기에 맞춰서만 잠시 깨어나 패킷을 받으므로 배터리를 극대화할 수 있습니다.",
            relatedApis = listOf("BluetoothAdapter.isLePeriodicAdvertisingSupported()", "PeriodicAdvertisingParameters")
        ),
        GlossaryItem(
            term = "Offloaded Filtering & Batching",
            category = "검색 & 연결 프로세스",
            summary = "메인 CPU(AP)를 깨우지 않고 블루투스 컨트롤러 칩셋에서 직접 패킷을 필터링 및 모아두는 하드웨어 최적화 기술.",
            fullExplanation = "스마트폰이 슬립 상태일 때 주변의 수많은 무선 패킷마다 메인 AP를 깨우면 배터리가 금방 닳습니다. 이 기능이 지원되면 블루투스 칩셋이 원하는 기기의 패킷만 골라내거나(Filtering), 버퍼에 일정량 모아두었다가(Batching) 한 번에 깨워 전달합니다.",
            relatedApis = listOf("BluetoothAdapter.isOffloadedFilteringSupported()", "BluetoothAdapter.isOffloadedScanBatchingSupported()")
        ),
        GlossaryItem(
            term = "Pairing vs Bonding (페어링과 본딩)",
            category = "검색 & 연결 프로세스",
            summary = "페어링은 임시 암호화 키를 교환하는 과정이고, 본딩은 그 키를 영구 저장하여 이후 자동 재연결되도록 하는 것.",
            fullExplanation = "• Pairing: 두 기기 간에 보안 연결을 맺고 임시 링크 키(Link Key)를 생성하는 일회성 절차.\n• Bonding: 페어링 과정에서 생성된 장기 키(LTK)를 내부 저장소에 영구 보관하는 것. 본딩된 기기는 다음번에 접근했을 때 사용자 PIN 입력이나 수락 없이 즉시 보안 연결이 복원됩니다.",
            relatedApis = listOf("BluetoothDevice.createBond()", "BluetoothDevice.BOND_BONDED", "BluetoothAdapter.bondedDevices")
        ),

        // 3. 프로토콜 & 프로파일
        GlossaryItem(
            term = "Profile (프로파일)",
            category = "프로토콜 & 프로파일",
            summary = "특정 사용 목적(음악, 통화, 입력장치, 센서 등)을 위해 기기 간 지켜야 할 표준 동작 규격.",
            fullExplanation = "블루투스 하위 계층 통신 프로토콜 위에 얹어지는 응용 소프트웨어 규격입니다. 양쪽 기기가 동일한 프로파일을 구현하고 있어야 상호 연동이 가능합니다. (예: 스마트폰과 이어폰 둘 다 A2DP를 지원해야 음악 재생 가능).",
            relatedApis = listOf("BluetoothProfile", "BluetoothProfile.ServiceListener", "BluetoothAdapter.getProfileProxy()")
        ),
        GlossaryItem(
            term = "A2DP (Advanced Audio Distribution Profile)",
            category = "프로토콜 & 프로파일",
            summary = "고품질 스테레오 오디오 스트리밍을 전송하기 위한 블루투스 클래식 프로파일 (음악 재생 전용).",
            fullExplanation = "스마트폰에서 무선 이어폰이나 카오디오로 음악을 보낼 때 사용됩니다. 단방향(Source -> Sink) 전송이며, SBC, AAC, aptX, LDAC 등의 오디오 코덱을 사용하여 압축된 음악 데이터를 전송합니다.",
            relatedApis = listOf("BluetoothA2dp", "BluetoothProfile.A2DP")
        ),
        GlossaryItem(
            term = "HFP / HSP (Hands-Free Profile / Headset Profile)",
            category = "프로토콜 & 프로파일",
            summary = "음성 통화(양방향 마이크/스피커) 및 통화 수신/종료 등의 버튼 제어를 위한 프로파일.",
            fullExplanation = "전화 통화용 프로파일입니다. 마이크 입력과 스피커 출력이 동시에 이루어져야 하므로 양방향 모노 음성 채널을 사용하며, 전화 걸기, 수신 거부, 음량 조절 등의 원격 제어 명령 규격이 포함되어 있습니다.",
            relatedApis = listOf("BluetoothHeadset", "BluetoothProfile.HEADSET")
        ),
        GlossaryItem(
            term = "GATT (Generic Attribute Profile)",
            category = "프로토콜 & 프로파일",
            summary = "BLE 기기 간에 서비스(Service)와 특성(Characteristic) 계층 구조로 데이터를 읽고 쓰는 핵심 프로파일.",
            fullExplanation = "BLE의 모든 데이터 통신은 GATT 구조를 기반으로 합니다. 서버(Server: 데이터 제공자, 센서 기기 등)와 클라이언트(Client: 데이터 요청자, 스마트폰 등) 관계로 동작하며, 계층 구조는 Profile > Service > Characteristic > Descriptor 로 이루어집니다.",
            relatedApis = listOf("BluetoothGatt", "BluetoothGattService", "BluetoothGattCharacteristic", "BluetoothGattServer")
        ),
        GlossaryItem(
            term = "GAP (Generic Access Profile)",
            category = "프로토콜 & 프로파일",
            summary = "디바이스의 역할(Broadcaster, Observer, Peripheral, Central)과 광고, 연결 수립 절차를 정의하는 기본 규격.",
            fullExplanation = "GATT 이전 단계에서 기기가 서로를 어떻게 발견하고, 어떤 파라미터로 연결을 맺을지를 정의합니다. 스마트폰은 대개 Central/Observer 역할을 하고, 스마트워치나 센서는 Peripheral/Broadcaster 역할을 합니다.",
            relatedApis = listOf("BluetoothManager", "BluetoothAdapter")
        ),
        GlossaryItem(
            term = "UUID (Universally Unique Identifier)",
            category = "프로토콜 & 프로파일",
            summary = "블루투스 서비스, 특성(Characteristic), 프로파일을 전 세계적으로 고유하게 식별하는 128비트 식별자.",
            fullExplanation = "표준 블루투스 SIG 규격(심박수 측정, 배터리 잔량 등)은 16비트 단축 UUID(예: 0x180D)를 사용하며, 제조사 고유 커스텀 서비스는 '0000xxxx-0000-1000-8000-00805F9B34FB' 형태의 완전한 128비트 UUID를 생성하여 충돌 없이 통신합니다.",
            relatedApis = listOf("java.util.UUID", "ParcelUuid", "BluetoothGattCharacteristic.uuid")
        ),

        // 4. AOSP & 시스템 아키텍처
        GlossaryItem(
            term = "AOSP (Android Open Source Project)",
            category = "AOSP & 시스템 아키텍처",
            summary = "구글이 주도하여 개발 및 공개하는 오픈소스 안드로이드 운영체제 플랫폼 프로젝트.",
            fullExplanation = "순정 안드로이드 소스코드 전체를 의미합니다. 스마트폰 제조사(삼성, 샤오미 등)나 자동차 IVI(Android Automotive), 임베디드 기기 개발자는 AOSP 소스(packages/modules/Bluetooth 등)를 기반으로 자사 하드웨어에 맞게 수정·빌드합니다.",
            relatedApis = listOf("android.bluetooth", "com.android.bluetooth")
        ),
        GlossaryItem(
            term = "Fluoride / GD / Rust Bluetooth Stack",
            category = "AOSP & 시스템 아키텍처",
            summary = "AOSP 안드로이드의 메인 블루투스 소프트웨어 스택 구현체.",
            fullExplanation = "과거 BlueZ에서 구글이 독자 개발한 C++ 기반 Fluoride 스택으로 전환되었고, 이후 아키텍처를 현대화한 GD(Google Drops)를 거쳐, 현재는 메모리 안전성(Memory Safety) 취약점을 원천 방지하기 위해 Rust 기반 스택으로 전환 중입니다.",
            relatedApis = listOf("packages/modules/Bluetooth/system", "libbluetooth")
        ),
        GlossaryItem(
            term = "HCI (Host Controller Interface)",
            category = "AOSP & 시스템 아키텍처",
            summary = "OS 블루투스 스택(Host)과 블루투스 무선 칩셋 펌웨어(Controller) 사이의 표준 통신 규격.",
            fullExplanation = "UART, USB, PCIe 등의 물리 버스를 통해 Host와 Controller가 통신하는 명령어/이벤트/데이터 인터페이스입니다. AOSP Bluetooth 스택 내부의 가장 아래 계층에 위치하여 하드웨어 제어 명령을 직접 전송합니다.",
            relatedApis = listOf("android.hardware.bluetooth@1.x (HAL)", "HciPacket")
        ),
        GlossaryItem(
            term = "HCI Snoop Log",
            category = "AOSP & 시스템 아키텍처",
            summary = "Host와 Controller 간에 오고 간 모든 패킷을 캡처하여 저장한 바이너리 로그 파일.",
            fullExplanation = "안드로이드 개발자 옵션에서 'Bluetooth HCI 정보 수집 로그 사용'을 켜면 활성화됩니다. 저장된 로그 파일(btsnoop_hci.log)을 PC로 가져와 Wireshark 프로그램으로 열면 프로토콜 패킷, 암호화 협상, 오디오 패킷 흐름을 완벽하게 디버깅할 수 있어 블루투스 개발자의 필수 도구입니다.",
            relatedApis = listOf("btsnoop_hci.log", "Wireshark packet analyzer")
        ),
        GlossaryItem(
            term = "HAL (Hardware Abstraction Layer)",
            category = "AOSP & 시스템 아키텍처",
            summary = "안드로이드 프레임워크와 칩셋 벤더(Qualcomm, Broadcom, MediaTek 등) 하드웨어를 연결하는 추상화 계층.",
            fullExplanation = "AIDL / HIDL 인터페이스 규격으로 정의되며, 벤더사는 이 인터페이스에 맞춰 칩셋 드라이버 라이브러리(`bluetooth.default.so`)를 공급합니다. AOSP 스택은 칩셋 모델에 관계없이 일관된 API로 블루투스를 제어할 수 있습니다.",
            relatedApis = listOf("android.hardware.bluetooth.IBluetoothHci", "vendor/bt_vendor.conf")
        ),
        GlossaryItem(
            term = "@SystemApi & @hide",
            category = "AOSP & 시스템 아키텍처",
            summary = "일반 3rd-Party SDK 개발자에게는 숨겨져 있고, 시스템/제조사 앱에만 허용되는 내부 API.",
            fullExplanation = "안드로이드 소스코드 상에서 Javadoc 태그로 `@SystemApi` 또는 `@hide`가 붙은 클래스나 메서드입니다. 블루투스 강제 연결/해제(`connect`/`disconnect`), 페어링 강제 해제(`removeBond`), 스캔 모드 강제 변경 등은 모두 일반 앱 오남용을 막기 위해 숨겨져 있습니다.",
            relatedApis = listOf("BluetoothA2dp.connect()", "BluetoothDevice.removeBond()", "BluetoothAdapter.setScanMode()")
        ),
        GlossaryItem(
            term = "BLUETOOTH_PRIVILEGED",
            category = "AOSP & 시스템 아키텍처",
            summary = "시스템 이미지에 사전 탑재된 앱이나 동일한 시스템 서명(signature)을 가진 앱만 취득 가능한 특권 권한.",
            fullExplanation = "일반 개발자가 AndroidManifest.xml에 선언해도 시스템이 승인해주지 않는 `signature|privileged` 권한입니다. 시스템 설정 앱(Settings)이나 SystemUI처럼 기기를 완전히 제어해야 하는 시스템 애플리케이션만 이 권한을 통해 숨겨진 SystemApi를 호출할 수 있습니다.",
            relatedApis = listOf("android.permission.BLUETOOTH_PRIVILEGED", "Settings app", "BluetoothManagerService")
        )
    )
}
