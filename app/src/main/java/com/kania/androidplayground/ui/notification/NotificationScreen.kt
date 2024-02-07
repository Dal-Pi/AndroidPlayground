package com.kania.androidplayground.ui.notification

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kania.androidplayground.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationScreen(
    areNotificationsEnabled: Boolean?,
    onClickNotificationSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            val activity = LocalContext.current as? Activity
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.playground_notification))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    scrolledContainerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            activity?.finish()
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Body(
            modifier = Modifier.padding(innerPadding),
            areNotificationsEnabled = areNotificationsEnabled,
            onClickNotificationSettings = onClickNotificationSettings
        )
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    areNotificationsEnabled: Boolean?,
    onClickNotificationSettings: () -> Unit
) {
    Column(
        modifier = modifier.padding(
            horizontal = 20.dp,
            vertical = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Notifications Enabled : ${areNotificationsEnabled ?: "Unknown"}"
        )
        Button(
            onClick = onClickNotificationSettings
        ) {
            Text(text = "Notification Settings")
        }
    }
}

