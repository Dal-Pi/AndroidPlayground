package com.kania.androidplayground

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kania.androidplayground.ui.LazyListPagingTestActivity
import com.kania.androidplayground.ui.battery.BatteryActivity
import com.kania.androidplayground.ui.bluetooth.BluetoothTestActivity
import com.kania.androidplayground.ui.devsettings.DeveloperSettingsActivity
import com.kania.androidplayground.ui.deviceinfo.DeviceInfoActivity
import com.kania.androidplayground.ui.notification.NotificationActivity
import com.kania.androidplayground.ui.theme.AndroidPlaygroundTheme
import com.kania.androidplayground.ui.wifi.WifiTestActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidPlaygroundTheme {
                PlaygroundListScreen()
            }
        }
    }
}

private data class PlaygroundItem(
    val titleResId: Int,
    val description: String,
    val icon: ImageVector,
    val iconTint: Color,
    val iconBgColor: Color,
    val targetActivity: Class<out ComponentActivity>
)

private data class PlaygroundSection(
    val title: String,
    val items: List<PlaygroundItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaygroundListScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        PlaygroundList(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
private fun PlaygroundList(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sections = remember { getPlaygroundSections() }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(sections) { section ->
            SectionView(
                section = section,
                onItemClick = { item ->
                    context.startActivity(Intent(context, item.targetActivity))
                }
            )
        }
    }
}

@Composable
private fun SectionView(
    section: PlaygroundSection,
    onItemClick: (PlaygroundItem) -> Unit
) {
    Column {
        Text(
            text = section.title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
        )

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                section.items.forEachIndexed { index, item ->
                    PlaygroundListItemRow(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                    if (index < section.items.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 72.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaygroundListItemRow(
    item: PlaygroundItem,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) item.iconTint.copy(alpha = 0.22f) else item.iconBgColor
    val iconColor = if (isDark) item.iconTint.copy(alpha = 0.95f) else item.iconTint

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(color = bgColor, shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(item.titleResId),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun getPlaygroundSections(): List<PlaygroundSection> {
    return listOf(
        PlaygroundSection(
            title = "시스템 & 도구",
            items = listOf(
                PlaygroundItem(
                    titleResId = R.string.playground_developer_settings,
                    description = "개발자 옵션 / USB 디버깅 토글 및 상단바 타일",
                    icon = Icons.Default.DeveloperMode,
                    iconTint = Color(0xFFE91E63),
                    iconBgColor = Color(0xFFFCE4EC),
                    targetActivity = DeveloperSettingsActivity::class.java
                ),
                PlaygroundItem(
                    titleResId = R.string.playground_device_info,
                    description = "디바이스 모델명, Android 버전, 제조사 스펙",
                    icon = Icons.Default.PhoneAndroid,
                    iconTint = Color(0xFF3F51B5),
                    iconBgColor = Color(0xFFE8EAF6),
                    targetActivity = DeviceInfoActivity::class.java
                )
            )
        ),
        PlaygroundSection(
            title = "하드웨어 & 통신",
            items = listOf(
                PlaygroundItem(
                    titleResId = R.string.playground_battery,
                    description = "실시간 충전 상태, 전압/전류, 와트(W) 계산",
                    icon = Icons.Default.BatteryChargingFull,
                    iconTint = Color(0xFF4CAF50),
                    iconBgColor = Color(0xFFE8F5E9),
                    targetActivity = BatteryActivity::class.java
                ),
                PlaygroundItem(
                    titleResId = R.string.playground_wifi,
                    description = "Wi-Fi 네트워크 상태 확인 및 스캔 테스트",
                    icon = Icons.Default.Wifi,
                    iconTint = Color(0xFF00ACC1),
                    iconBgColor = Color(0xFFE0F7FA),
                    targetActivity = WifiTestActivity::class.java
                ),
                PlaygroundItem(
                    titleResId = R.string.playground_bluetooth,
                    description = "BLE 스캔 및 블루투스 기기 연결 테스트",
                    icon = Icons.Default.Bluetooth,
                    iconTint = Color(0xFF2196F3),
                    iconBgColor = Color(0xFFE3F2FD),
                    targetActivity = BluetoothTestActivity::class.java
                )
            )
        ),
        PlaygroundSection(
            title = "UI & 컴포넌트",
            items = listOf(
                PlaygroundItem(
                    titleResId = R.string.playground_notification,
                    description = "알림 채널 생성, 우선순위 및 알림 발송 테스트",
                    icon = Icons.Default.Notifications,
                    iconTint = Color(0xFFFF9800),
                    iconBgColor = Color(0xFFFFF3E0),
                    targetActivity = NotificationActivity::class.java
                ),
                PlaygroundItem(
                    titleResId = R.string.playground_lazylistpaging,
                    description = "Paging 3 라이브러리 연동 대용량 스크롤 테스트",
                    icon = Icons.AutoMirrored.Filled.List,
                    iconTint = Color(0xFF9C27B0),
                    iconBgColor = Color(0xFFF3E5F5),
                    targetActivity = LazyListPagingTestActivity::class.java
                )
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun GreetingPreview() {
    AndroidPlaygroundTheme {
        PlaygroundList()
    }
}
