package com.kania.androidplayground

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kania.androidplayground.ui.LazyColumnActivity
import com.kania.androidplayground.ui.theme.AndroidPlaygroundTheme
import com.kania.androidplayground.ui.NotificationActivity

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaygroundListScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White)
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
    LazyColumn(modifier = modifier) {
        playgroundListItem(
            nameStringId = R.string.playground_notification,
            onClick = {
                context.startActivity(Intent(context, NotificationActivity::class.java))
            }
        )
        playgroundListItem(
            nameStringId = R.string.playground_lazycolumn,
            onClick = {
                context.startActivity(Intent(context, LazyColumnActivity::class.java))
            }
        )
    }
}

private fun LazyListScope.playgroundListItem(
    iconPainter: Painter? = null,
    nameStringId: Int,
    onClick: () -> Unit
) {
    item {
        Box(modifier = Modifier.clickable { onClick() }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                iconPainter?.let { painter ->
                    Icon(
                        painter = painter,
                        contentDescription = null
                    )
                }
                Text(text = stringResource(nameStringId))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidPlaygroundTheme {
        PlaygroundList()
    }
}