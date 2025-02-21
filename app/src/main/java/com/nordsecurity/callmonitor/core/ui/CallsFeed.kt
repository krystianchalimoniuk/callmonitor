package com.nordsecurity.callmonitor.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nordsecurity.callmonitor.core.model.CallResource
import com.nordsecurity.callmonitor.feature.home.HomeFeedUiState

fun LazyStaggeredGridScope.callsFeed(
    feedUiState: HomeFeedUiState
) {
    when (feedUiState) {
        is HomeFeedUiState.Loading -> Unit
        is HomeFeedUiState.Success -> {
            items(items = feedUiState.feed,
                key = { it.id },
                contentType = { "callsFeed" }) { item: CallResource ->
                CallResourceCard(
                    callLogEntry = item,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .animateItem(),
                )
            }
        }

    }
}