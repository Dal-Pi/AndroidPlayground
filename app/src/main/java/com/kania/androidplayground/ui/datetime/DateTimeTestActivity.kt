package com.kania.androidplayground.ui.datetime

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kania.androidplayground.R
import com.kania.androidplayground.ui.theme.AndroidPlaygroundTheme
import java.time.LocalDateTime

class DateTimeTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidPlaygroundTheme {
                DateTimeTestScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimeTestScreen() {
    Scaffold(
        topBar = {
            val activity = LocalContext.current as? Activity
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.playground_datetime))
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
            currentLocalDateTime = LocalDateTime.now()
        )
    }
}

@Composable
private fun Body(
    modifier: Modifier = Modifier,
    currentLocalDateTime: LocalDateTime
) {
    Column(
        modifier = modifier
            .padding(
                horizontal = 20.dp,
                vertical = 20.dp
            )
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        ListItem(
            codeText = "LocalDateTime.toString()",
            resultText = currentLocalDateTime.toString()
        )

        val patternInSeconds = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        val timeDateStringInSeconds = currentLocalDateTime.toStringAsPattern(patternInSeconds)
        ListItem(
            codeText = "LocalDateTime -> $patternInSeconds",
            resultText = timeDateStringInSeconds
        )

        val patternInMicroseconds = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
        val timeDateStringInMicroseconds = currentLocalDateTime.toStringAsPattern(patternInMicroseconds)
        ListItem(
            codeText = "LocalDateTime -> $patternInMicroseconds",
            resultText = timeDateStringInMicroseconds
        )

        timeDateStringInSeconds.let {
            val parsedLocalDateTime = getLocalDateTimeFromUtcStringInSeconds(it)
            ListItem(
                codeText = "$it -> LocalDateTime",
                resultText = parsedLocalDateTime.toString()
            )
        }
//        val parsedLocalDateTime = getLocalDateTimeFromStringInSeconds(timeDateStringInMicroseconds)
//        ListItem(
//            codeText = "$timeDateStringInMicroseconds -> LocalDateTime",
//            resultText = parsedLocalDateTime.toString()
//        )
    }
}

@Composable
private fun ListItem(
    codeText: String,
    resultText: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
    ) {
        Text(
            text = codeText,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = resultText,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(5.dp))
        Divider()
    }

}

@Preview(apiLevel = 33)
@Composable
private fun DateTimeTestScreenPreview() {
    AndroidPlaygroundTheme {
        Body(
            currentLocalDateTime = LocalDateTime.now()
        )
    }
}