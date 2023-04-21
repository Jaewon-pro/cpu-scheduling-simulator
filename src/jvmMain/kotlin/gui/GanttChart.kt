package gui

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import utils.Info


@Composable
fun chart(width: Dp, label: String, time: Int) { // 차트 한칸
    Column {
        Card(Modifier
            .border(width = 0.dp, color = Color.Blue)
            .size(width, 40.dp)
        ) {
            Box(contentAlignment = Alignment.Center
            ) { Text(label) }
        }
        Text(modifier = Modifier.align(Alignment.End), text = time.toString())
    }
}

@Composable
fun showChart(info: List<Info>) {
    println(info)
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LazyRow (
        state = scrollState,
        modifier = Modifier
            .padding(12.dp)
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    coroutineScope.launch { scrollState.scrollBy(-delta) }
                },
            )
    ) {
        items(info) {
            chart((it.ranTime * 20).dp, it.pid.toString(), it.timestamp)
        }
    }
}
