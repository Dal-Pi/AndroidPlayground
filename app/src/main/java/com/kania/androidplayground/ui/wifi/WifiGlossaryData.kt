package com.kania.androidplayground.ui.wifi

object WifiGlossaryData {
    val items = listOf(
        // 1. 무선 규격 & 대역
        WifiGlossaryItem(
            term = "Wi-Fi Standards (Wi-Fi 4 ~ 7)",
            category = "무선 규격 & 대역",
            summary = "IEEE 802.11 기술 규격을 세대별로 알기 쉽게 부르는 명칭.",
            fullExplanation = "• Wi-Fi 4 (802.11n): 2.4GHz / 5GHz, 최대 600Mbps 지원.\n• Wi-Fi 5 (802.11ac): 5GHz 전용 고속 기가비트 Wi-Fi.\n• Wi-Fi 6 / 6E (802.11ax): OFDMA, 1024-QAM, 6GHz 대역 추가(6E)로 다중 기기 혼잡 환경 개선.\n• Wi-Fi 7 (802.11be): 320MHz 대역폭, MLO(Multi-Link Operation), 최대 30Gbps 이상의 초고속 전송.",
            relatedApis = listOf("ScanResult.getWifiStandard()", "WifiInfo.getWifiStandard()")
        ),
        WifiGlossaryItem(
            term = "Frequency Bands (2.4GHz, 5GHz, 6GHz)",
            category = "무선 규격 & 대역",
            summary = "Wi-Fi 신호가 전송되는 무선 주파수 대역.",
            fullExplanation = "• 2.4GHz: 회절성과 도달 거리가 길지만, 채널 수가 적고 블루투스·전자레인지 등과 전파 간섭이 심함.\n• 5GHz: 대역폭이 넓고 전송 속도가 매우 빠르나 벽 등 장애물 통과 시 신호 감쇄가 큼.\n• 6GHz (Wi-Fi 6E/7): 완전히 새로운 청정 대역으로 간섭이 거의 없고 초광대역(160/320MHz) 전송 가능.",
            relatedApis = listOf("ScanResult.frequency", "WifiManager.is5GHzBandSupported()", "WifiManager.is6GHzBandSupported()")
        ),
        WifiGlossaryItem(
            term = "Channel & Bandwidth (대역폭)",
            category = "무선 규격 & 대역",
            summary = "주파수 대역 내에서 분할된 개별 통신 통로(채널)와 데이터가 지나가는 폭(20/40/80/160MHz).",
            fullExplanation = "차선이 넓을수록(대역폭 20MHz -> 160MHz) 한 번에 전송할 수 있는 데이터양이 늘어나 전송 속도가 비약적으로 빨라집니다. 다만 대역폭을 넓게 잡을수록 주변 다른 AP와의 전파 간섭 위험도 커집니다.",
            relatedApis = listOf("ScanResult.channelWidth")
        ),
        WifiGlossaryItem(
            term = "RSSI & Link Speed (신호 세기 & 링크 속도)",
            category = "무선 규격 & 대역",
            summary = "단말기가 수신한 신호의 세기(dBm)와 현재 단말기와 공유기 사이에 협상된 물리 계층 전송 속도(Mbps).",
            fullExplanation = "• RSSI: -30 ~ -50 dBm(최상), -65 dBm(양호), -80 dBm 이하(불안정/끊김).\n• Link Speed: 현재 단말기와 AP 간에 실제 무선으로 전송 가능한 이론적 최대 속도(인터넷 실제 다운로드 속도와는 구분됨).",
            relatedApis = listOf("WifiInfo.rssi", "WifiInfo.linkSpeed", "WifiManager.calculateSignalLevel()")
        ),

        // 2. 식별자 & 네트워크 구성
        WifiGlossaryItem(
            term = "SSID (Service Set Identifier)",
            category = "식별자 & 네트워크",
            summary = "무선 LAN을 식별하기 위해 사용자가 알아볼 수 있도록 붙인 고유 무선 네트워크 이름.",
            fullExplanation = "우리가 카페나 집에서 Wi-Fi 목록을 볼 때 표시되는 이름(예: 'MyHome_5G')입니다. 최대 32바이트 길이이며, 보안을 위해 SSID 브로드캐스트를 숨기는 'Hidden SSID'로 설정할 수도 있습니다.",
            relatedApis = listOf("WifiInfo.ssid", "ScanResult.SSID", "WifiConfiguration.SSID")
        ),
        WifiGlossaryItem(
            term = "BSSID (Basic Service Set Identifier)",
            category = "식별자 & 네트워크",
            summary = "특정 무선 공유기(AP) 또는 무선 안테나의 고유 하드웨어 MAC 주소.",
            fullExplanation = "동일한 SSID를 가진 대규모 사내 네트워크라도 각 층과 방마다 설치된 개별 AP 장비는 서로 다른 BSSID(예: 00:11:22:33:44:55)를 갖습니다. 단말기는 이동할 때 더 강한 BSSID로 로밍(Roaming)합니다. Android에서는 위치 추적 위험으로 BSSID 취득에 위치 권한을 요구합니다.",
            relatedApis = listOf("WifiInfo.bssid", "ScanResult.BSSID")
        ),
        WifiGlossaryItem(
            term = "MAC Randomization (무작위 MAC 주소)",
            category = "식별자 & 네트워크",
            summary = "공공장소에서 단말기 이동 경로 추적을 방지하기 위해 가짜 MAC 주소를 사용하는 개인정보 보호 기술.",
            fullExplanation = "Android 10부터 기본 활성화되었습니다. AP에 연결하거나 프로브 요청(Probe Request)을 보낼 때 실제 스마트폰 하드웨어 MAC 주소 대신 무작위로 생성된 가상 MAC 주소를 사용합니다. 일반 앱은 이제 실제 MAC 주소를 절대 취득할 수 없습니다.",
            relatedApis = listOf("WifiConfiguration.macRandomizationSetting")
        ),
        WifiGlossaryItem(
            term = "DHCP & IP Configuration",
            category = "식별자 & 네트워크",
            summary = "Wi-Fi에 연결된 후 공유기로부터 사설 IP, 서브넷 마스크, 게이트웨이, DNS 주소를 동적으로 할당받는 절차.",
            fullExplanation = "Wi-Fi 암호 인증이 끝나면 단말기는 DHCP 프로토콜을 통해 공유기(DHCP 서버)로부터 네트워크에서 사용할 IP 주소를 부여받고 인터넷 경로(Gateway)를 설정합니다.",
            relatedApis = listOf("DhcpInfo", "WifiManager.dhcpInfo", "LinkProperties")
        ),

        // 3. 보안 & 인증
        WifiGlossaryItem(
            term = "WPA / WPA2 / WPA3",
            category = "보안 & 인증",
            summary = "Wi-Fi 네트워크 통신을 암호화하고 무단 접근을 방지하는 보안 표준 프로토콜.",
            fullExplanation = "• WPA2 (AES-CCMP): 가장 널리 보급된 보안 표준이지만 오프라인 사전 공격(Dictionary Attack) 및 KRACK 취약점이 존재.\n• WPA3: 192비트 엔터프라이즈 보안, 사전 공격 방어(SAE 도입), 공공 와이파이 개별 암호화(OWE)가 적용된 최신 표준.",
            relatedApis = listOf("ScanResult.capabilities", "WifiManager.isWpa3SaeSupported()")
        ),
        WifiGlossaryItem(
            term = "SAE (Simultaneous Authentication of Equals)",
            category = "보안 & 인증",
            summary = "WPA3-Personal에 도입된 강력한 동등 대 동등 인증 방식 (Dragonfly 키 교환).",
            fullExplanation = "WPA2의 단순 4-way 핸드셰이크는 패킷을 도청한 뒤 오프라인에서 무차별 대입 공격이 가능했지만, SAE는 암호가 틀리더라도 공격자가 키를 유추할 수 없도록 수학적 방어(전방향 보안성, Forward Secrecy)를 제공합니다.",
            relatedApis = listOf("WifiConfiguration.KeyMgmt.SAE", "WifiManager.isWpa3SaeSupported()")
        ),
        WifiGlossaryItem(
            term = "OWE (Enhanced Open / Opportunistic Wireless Encryption)",
            category = "보안 & 인증",
            summary = "비밀번호가 없는 공개(Open) Wi-Fi에서도 통신 구간을 개별 암호화해주는 WPA3 표준 기술.",
            fullExplanation = "카페나 공항 등 암호가 없는 오픈 Wi-Fi는 주변 사람이 패킷을 쉽게 스니핑할 수 있었습니다. OWE는 비밀번호 입력 없이 연결하더라도 Diffie-Hellman 키 교환을 통해 각 단말기마다 고유한 암호화 키를 생성하여 무선 구간을 암호화합니다.",
            relatedApis = listOf("WifiManager.isEnhancedOpenSupported()", "ScanResult.capabilities")
        ),
        WifiGlossaryItem(
            term = "DPP (Easy Connect / Device Provisioning Protocol)",
            category = "보안 & 인증",
            summary = "화면이나 키보드가 없는 IoT 기기(스마트 전구 등)를 QR 코드로 안전하게 Wi-Fi에 연결하는 프로토콜.",
            fullExplanation = "보안 취약점이 많았던 레거시 WPS(PIN/버튼 방식)를 대체하기 위해 Wi-Fi Alliance에서 제정한 최신 표준입니다. 스마트폰으로 기기의 QR 코드를 스캔하여 공개키 기반으로 안전하게 공유기 접속 정보를 전달합니다.",
            relatedApis = listOf("WifiManager.isEasyConnectSupported()", "WifiManager.startEasyConnectAsConfiguratorInitiator()")
        ),

        // 4. Android 연결 API & 제약
        WifiGlossaryItem(
            term = "WifiNetworkSuggestion (Wi-Fi 추천 API)",
            category = "Android 연결 API",
            summary = "Android 10(Q)부터 도입된 일반 앱의 공식 Wi-Fi 연결 제안 방식.",
            fullExplanation = "일반 앱이 특정 Wi-Fi에 강제로 연결하거나 Wi-Fi를 켜는 구버전 API가 모두 차단되었습니다. 대신 앱은 OS에게 '이 Wi-Fi 네트워크를 추천한다'고 Suggestion 목록에 등록하면, 안드로이드 시스템이 신호 품질과 네트워크 점수를 판단하여 최적일 때 자동으로 연결합니다.",
            relatedApis = listOf("WifiNetworkSuggestion", "WifiManager.addNetworkSuggestions()", "WifiManager.removeNetworkSuggestions()")
        ),
        WifiGlossaryItem(
            term = "WifiNetworkSpecifier (로컬 P2P 연결 API)",
            category = "Android 연결 API",
            summary = "인터넷이 없는 로컬 Wi-Fi 기기(액션캠, 드론, 스마트 가전 설정용)에 임시 연결하기 위한 API.",
            fullExplanation = "Android 10+에서 일반 앱이 특정 SSID/암호를 가진 장치에 연결하고 싶을 때 사용합니다. 호출 시 시스템 다이얼로그가 화면에 나타나 사용자가 승인해야 연결되며, 앱이 종료되거나 요청을 해제하면 자동으로 연결이 끊어집니다.",
            relatedApis = listOf("WifiNetworkSpecifier.Builder", "NetworkRequest.Builder.setNetworkSpecifier()", "ConnectivityManager.requestNetwork()")
        ),
        WifiGlossaryItem(
            term = "Scan Throttling (Wi-Fi 스캔 제한)",
            category = "Android 연결 API",
            summary = "배터리 수명 보호와 무선 성능 저하 방지를 위해 안드로이드 OS가 앱의 Wi-Fi 스캔 빈도를 강제 제한하는 정책.",
            fullExplanation = "Android 9(Pie)부터 도입되었습니다.\n• 포그라운드 앱: 2분 동안 최대 4회까지만 스캔 허용.\n• 백그라운드 앱: 30분에 1회까지만 스캔 허용.\n이 빈도를 초과하여 wifiManager.startScan()을 호출하면 요청이 무시됩니다.",
            relatedApis = listOf("WifiManager.startScan()", "WifiManager.ACTION_SCAN_RESULTS_AVAILABLE")
        ),
        WifiGlossaryItem(
            term = "Wi-Fi Direct (P2P / Wi-Fi Aware / NAN)",
            category = "Android 연결 API",
            summary = "공유기(AP)를 거치지 않고 스마트폰 기기끼리 고속으로 직접 연결하여 데이터를 주고받는 기술.",
            fullExplanation = "근거리 기기 간 파일 공유(Nearby Share / Quick Share 등)에 핵심적으로 사용됩니다. Wi-Fi Aware(NAN)는 인터넷 연결 없이도 주변 기기를 초저전력으로 발견하고 직접 고속 소켓 통신을 연결할 수 있습니다.",
            relatedApis = listOf("WifiP2pManager", "WifiAwareManager", "WifiAwareSession")
        ),
        WifiGlossaryItem(
            term = "SoftAP (모바일 핫스팟 / 테더링)",
            category = "Android 연결 API",
            summary = "스마트폰을 하나의 소프트웨어 기반 공유기(Access Point)로 동작시켜 주변 기기에 인터넷을 공유하는 기능.",
            fullExplanation = "일반 앱은 백그라운드에서 임의로 테더링을 켤 수 없으며 Intent로 시스템 설정 화면을 유도해야 합니다. AOSP 시스템 앱은 TetheringManager 및 hostapd 데몬을 통해 프로그래밍 방식으로 핫스팟을 직접 켜고 끌 수 있습니다.",
            relatedApis = listOf("TetheringManager", "WifiManager.LocalOnlyHotspotCallback", "hostapd")
        ),

        // 5. AOSP & 시스템 아키텍처
        WifiGlossaryItem(
            term = "wpa_supplicant",
            category = "AOSP & 시스템 아키텍처",
            summary = "리눅스 및 안드로이드 AOSP에서 단말기(Station, STA) 모드의 Wi-Fi 키 협상 및 WPA 인증을 담당하는 핵심 네이티브 데몬.",
            fullExplanation = "C 언어로 작성된 표준 오픈소스 데몬입니다. WPA/WPA2/WPA3 4-Way 핸드셰이크, EAP 엔터프라이즈 인증, 로밍 판단을 수행하며, AOSP Wi-Fi 스택의 심장 역할을 합니다. 안드로이드 프레임워크와는 HIDL/AIDL 인터페이스를 통해 통신합니다.",
            relatedApis = listOf("external/wpa_supplicant_8", "android.hardware.wifi.supplicant")
        ),
        WifiGlossaryItem(
            term = "hostapd",
            category = "AOSP & 시스템 아키텍처",
            summary = "단말기를 무선 공유기(AP, 핫스팟)로 동작시킬 때 접속자 관리와 비콘 송출, 인증을 총괄하는 네이티브 데몬.",
            fullExplanation = "스마트폰에서 모바일 핫스팟(SoftAP)을 켰을 때 구동되는 데몬입니다. 클라이언트 기기들의 접속 승인, WPA 암호화 핸드셰이크를 처리합니다.",
            relatedApis = listOf("external/wpa_supplicant_8/hostapd", "android.hardware.wifi.hostapd")
        ),
        WifiGlossaryItem(
            term = "wificond",
            category = "AOSP & 시스템 아키텍처",
            summary = "안드로이드 프레임워크와 리눅스 커널의 nl80211(cfg80211) 무선 서브시스템 사이를 중계하는 C++ 네이티브 데몬.",
            fullExplanation = "AOSP 8.0(Oreo)부터 도입되었습니다. Wi-Fi 스캔 명령 전달, PNO(Preferred Network Offload) 스캔, AP 모드 상태 감시 등 커널 레벨의 드라이버 제어를 Binder IPC 인터페이스로 감싸 프레임워크에 제공합니다.",
            relatedApis = listOf("system/connectivity/wificond", "nl80211 / cfg80211")
        ),
        WifiGlossaryItem(
            term = "WifiService & WifiManagerService",
            category = "AOSP & 시스템 아키텍처",
            summary = "안드로이드 시스템 서버(system_server) 내부에서 Wi-Fi의 전체 생명주기, 스캔, 연결 정책을 관장하는 최상위 자바 서비스.",
            fullExplanation = "packages/modules/Wifi 내부에 위치합니다. WifiStateMachine(현재의 ClientModeImpl)을 기반으로 스캔 스케줄링, Wi-Fi 점수 산정(NetworkScore), 자동 연결(Auto-join), 네트워크 추천 처리 등을 총괄합니다.",
            relatedApis = listOf("com.android.server.wifi.WifiServiceImpl", "com.android.server.wifi.ClientModeImpl")
        ),
        WifiGlossaryItem(
            term = "WiFi HAL (Hardware Abstraction Layer)",
            category = "AOSP & 시스템 아키텍처",
            summary = "안드로이드 프레임워크와 칩셋 제조사(퀄컴, 브로드컴 등)의 무선 펌웨어 드라이버를 연결하는 하드웨어 추상화 계층.",
            fullExplanation = "Vendor HAL(`android.hardware.wifi`), Supplicant HAL, Hostapd HAL로 나뉩니다. 제조사는 칩셋 고유의 기능(고급 패킷 필터링, RTT 거리 측정, 다중 인터페이스 제어 등)을 HAL 인터페이스 규격에 맞춰 구현하여 안드로이드에 탑재합니다.",
            relatedApis = listOf("hardware/interfaces/wifi", "android.hardware.wifi.IWifi")
        ),
        WifiGlossaryItem(
            term = "@SystemApi Wi-Fi 제어",
            category = "AOSP & 시스템 아키텍처",
            summary = "일반 앱에는 완전히 차단되고 시스템 설정 앱(Settings)이나 시스템 관리자만 호출 가능한 AOSP 전용 제어 메서드들.",
            fullExplanation = "• wifiManager.setWifiEnabled(boolean): Wi-Fi 강제 켜기/끄기 (Android 10+ 일반 앱 차단).\n• wifiManager.connect() / disconnect(): 특정 네트워크 강제 연결/해제.\n• wifiManager.save() / forget(): 네트워크 프로파일 강제 저장/삭제.\n이 기능들은 일반 앱이 무단으로 네트워크를 조작하지 못하도록 @SystemApi 및 NETWORK_SETTINGS 특권 권한으로 보호됩니다.",
            relatedApis = listOf("android.permission.NETWORK_SETTINGS", "android.permission.NETWORK_SETUP_WIZARD")
        )
    )
}
