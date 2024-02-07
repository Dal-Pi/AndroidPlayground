package com.kania.androidplayground.ui.lazycolumn

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import java.io.IOException

class LazyListPagingTestViewModel {
    val maxCount = 100

    val items = Pager(
        config = PagingConfig(
            pageSize = 30,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            object : PagingSource<Int, LazyListItem>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LazyListItem> {
                    val pageIndex = params.key ?: 0

                    val result = if (pageIndex < 100) {
                        delay(1000)
                        pageIndex.rangeTo(pageIndex + 4).toList().map{ id -> LazyListItem(id, "${id + 1}") }
                    } else {
                        emptyList()
                    }

                    val prevKey = if (pageIndex == 0) null else pageIndex

                    val nextKey = if (result.isEmpty()) {
                        null
                    } else {
                        (pageIndex + result.size)
                    }

                    try {
                        return LoadResult.Page(
                            data = result,
                            prevKey = prevKey,
                            nextKey = nextKey
                        )
                    } catch(e: IOException)  {
                        e.printStackTrace()
                        return LoadResult.Error(e)
                    }
                }
                override fun getRefreshKey(state: PagingState<Int, LazyListItem>): Int? {
                    return state.anchorPosition
                }
            }
        }
    ).flow
}