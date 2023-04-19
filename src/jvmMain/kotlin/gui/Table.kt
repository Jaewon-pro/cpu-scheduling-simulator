package gui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import utils.ProgramData
import utils.Task
import utils.taskHeader
import kotlin.math.roundToInt

@Composable
private fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
fun showTable(tasks: List<Task>) {
    if (tasks[0].executionTime == tasks[0].remainedTime)
        table(tasks)
}

@Composable
fun showResult(data: ProgramData?) {
    if (data == null) return
    Column {
        val averageWaitedTime = data.tasks.sumOf { it.waitedTime } / data.tasks.size.toFloat()
        val averageTurnaroundTime = data.tasks.sumOf { it.turnaroundTime() } / data.tasks.size.toFloat()
        val totalTime = data.info.last().time
        averageResultTable(totalTime, averageWaitedTime, averageTurnaroundTime, data.contextSwitched)
        resultTable(data.tasks)
    }
}



@Composable
private fun table(tasks: List<Task>) {
    // Each cell of a column must have the same weight.
    val weight: Float = ((tasks.size) / 100.0f) * 100 / 100.0f.roundToInt() // 요소를 100으로 나눈 퍼센트값
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // The LazyColumn will be our table. Notice the use of the weights below
    LazyColumn(modifier = Modifier
        .fillMaxWidth().height(500.dp).padding(16.dp).simpleVerticalScrollbar(scrollState)
        .draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                coroutineScope.launch {
                    scrollState.scrollBy(-delta)
                }
            },
        )) {
        // Here is the header
        item {
            Row(Modifier.background(Color(230,230,250))) {
                taskHeader.forEach { TableCell(text = it, weight = weight) }
            }
        }
        // Here are all the lines of your table.
        items(tasks) {
            val (pid, arrival, burst) = it
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = pid.toString(), weight = weight)
                TableCell(text = arrival.toString(), weight = weight)
                TableCell(text = burst.toString(), weight = weight)
                if (taskHeader.contains("Priority")) { TableCell(text = it.priority.toString(), weight = weight) }
            }
        }
    }
}

@Composable
private fun averageResultTable(totalTime: Int, avgWaited: Float, avgTurnaround: Float, contextSwitched: Int) {
    val weight: Float = (2 / 100.0f) * 100 / 100.0f.roundToInt() // 요소를 100으로 나눈 퍼센트값
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // The LazyColumn will be our table. Notice the use of the weights below
    LazyColumn(modifier = Modifier
        .fillMaxWidth().height(100.dp).padding(16.dp).simpleVerticalScrollbar(scrollState)
        .draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                coroutineScope.launch {
                    scrollState.scrollBy(-delta)
                }
            },
        )) {
        item {
            Row(Modifier.background(Color(230,230,250))) {
                TableCell(text = "Total Runtime", weight = weight)
                TableCell(text = "Context Switch", weight = weight)
                TableCell(text = "Average Turnaround", weight = weight)
                TableCell(text = "Average Turnaround", weight = weight)
            }
        }
        // Here are all the lines of your table.
        items(1) {
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = totalTime.toString(), weight = weight)
                TableCell(text = contextSwitched.toString(), weight = weight)
                TableCell(text = avgWaited.toString(), weight = weight)
                TableCell(text = avgTurnaround.toString(), weight = weight)
            }
        }
    }
}

@Composable
private fun resultTable(tasks: List<Task>) {
    val weight: Float = ((taskHeader.size + 3) / 100.0f) * 100 / 100.0f.roundToInt() // Column 요소 개수를 100으로 나눈 퍼센트 값
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        state = scrollState,
        modifier = Modifier.fillMaxWidth().height(300.dp).padding(16.dp).simpleVerticalScrollbar(scrollState)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        scrollState.scrollBy(-delta)
                    }
                },
            )
    ) {
        item { // Table Header
            Row(Modifier.background(Color(230,230,250))) {
                taskHeader.forEach { TableCell(text = it, weight = weight) }
                TableCell(text = "Waited", weight = weight)
                TableCell(text = "Completed", weight = weight)
                TableCell(text = "Turnaround", weight = weight)
            }
        }
        items(tasks) {
            Row(Modifier.fillMaxWidth()) {
                val (pid, arrival, burst) = it
                TableCell(text = pid.toString(), weight = weight)
                TableCell(text = arrival.toString(), weight = weight)
                TableCell(text = burst.toString(), weight = weight)
                if (taskHeader.contains("Priority")) { TableCell(text = it.priority.toString(), weight = weight) }
                TableCell(text = it.waitedTime.toString(), weight = weight)
                TableCell(text = it.completedTime().toString(), weight = weight)
                TableCell(text = it.turnaroundTime().toString(), weight = weight)
            }
        }
    }
}


@Composable
fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    width: Dp = 8.dp
): Modifier {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 150 else 500

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration)
    )

    return drawWithContent {
        drawContent()

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f

        // Draw scrollbar if scrolling or if the animation is still running and lazy column has content
        if (needDrawScrollbar && firstVisibleElementIndex != null) {
            val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

            drawRect(
                color = Color.Red,
                topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                alpha = alpha
            )
        }
    }
}
