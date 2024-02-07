package com.kania.androidplayground.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kania.androidplayground.R
import com.kania.androidplayground.ui.lazycolumn.LazyListItem
import com.kania.androidplayground.ui.lazycolumn.LazyListPagingTestViewModel
import com.kania.androidplayground.ui.theme.AndroidPlaygroundTheme
import kotlinx.coroutines.delay

class LazyListPagingTestActivity : ComponentActivity() {

    val viewModel = LazyListPagingTestViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val pagingItems = viewModel.items.collectAsLazyPagingItems()

            AndroidPlaygroundTheme {
                LazyListPagingTestScreen(
                    pagingItems = pagingItems,
                    maxCount = viewModel.maxCount
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LazyListPagingTestScreen(
    pagingItems: LazyPagingItems<LazyListItem>,
    maxCount: Int? = null
) {
    Scaffold(
        topBar = {
            val activity = LocalContext.current as? Activity
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.playground_lazylistpaging))
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
        ListViewScreen(
            modifier = Modifier.padding(innerPadding),
            pagingItems = pagingItems,
            maxCount = maxCount
        )
    }
}

@Composable
private fun ListViewScreen(
    modifier: Modifier = Modifier,
    pagingItems: LazyPagingItems<LazyListItem>,
    maxCount: Int? = null
) {
    val numbers = remember { mutableStateListOf<Int>() }
    LaunchedEffect(Unit) {
        while (numbers.size < 1000) {
            delay(2000)
            numbers.addAll(List(10) { numbers.size + it })
        }
    }

    LazyColumn(modifier = modifier) {
        items(
            count = pagingItems.itemCount
        ) { index ->
            val item = pagingItems[index]

            if (item != null) {
                ListItemComponent(item = item)
            }
        }
        if (maxCount != null && pagingItems.itemCount < maxCount) {
            item {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(modifier = Modifier.wrapContentSize())
                }
            }
        } else {
            item {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("End of list")
                }
            }
        }
    }
}

@Composable
private fun ListItemComponent(
    modifier: Modifier = Modifier,
    item: LazyListItem
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .height(40.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable{ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(5.dp),
                text = "[${item.id}] ${item.title}"
            )
        }
        Divider(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 5.dp)
        )
    }
}
