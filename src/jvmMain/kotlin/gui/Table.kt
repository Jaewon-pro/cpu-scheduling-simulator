package gui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import utils.ChartInfo
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
        modifier = Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
fun showTasksTable(tasks: List<Task>) {
    if (tasks[0].isFinished()) return
    // Each cell of a column must have the same weight.
    val weight: Float = (taskHeader.size / 100.0f) * 100 / 100.0f.roundToInt() // 요소를 100으로 나눈 퍼센트값
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // The LazyColumn will be our table. Notice the use of the weights below
    LazyColumn(modifier = Modifier.fillMaxHeight(0.9f)
        .fillMaxWidth().padding(16.dp)//.height(500.dp)
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
fun showResult(tasks: List<Task>, info: List<ChartInfo>) { // 성능 지표 테이블
    Column {
        val avgWaitedTime = tasks.sumOf { it.waitedTime } / tasks.size.toFloat()
        val avgResponseTime = tasks.sumOf { it.responseTime } / tasks.size.toFloat()
        val avgTurnaroundTime = tasks.sumOf { it.turnaroundTime() } / tasks.size.toFloat()
        val totalTime = info.last().timestamp
        val throughput: Float = tasks.size / totalTime.toFloat()
        val contextSwitched = info.size - 2
        averageResultTable(throughput, avgWaitedTime, avgResponse = avgResponseTime, avgTurnaroundTime, contextSwitched)
        resultTable(tasks)
    }
}


@Composable
private fun averageResultTable(throughput: Float, avgWaited: Float, avgResponse: Float, avgTurnaround: Float, contextSwitched: Int) {
    val weight: Float = (5 / 100.0f) * 100 / 100.0f.roundToInt() // 요소를 100으로 나눈 퍼센트값
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(modifier = Modifier
        .fillMaxWidth().padding(16.dp).height(80.dp)
    ) {
        item {
            Row(Modifier.background(Color(230,230,250))) {
                TableCell(text = "Throughput", weight = weight)
                TableCell(text = "Context Switch", weight = weight)
                TableCell(text = "Avg Waited", weight = weight)
                TableCell(text = "Avg Response", weight = weight)
                TableCell(text = "Avg Turnaround", weight = weight)
            }
        }
        items(1) {
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = throughput.toString(), weight = weight)
                TableCell(text = contextSwitched.toString(), weight = weight)
                TableCell(text = avgWaited.toString(), weight = weight)
                TableCell(text = avgResponse.toString(), weight = weight)
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
        //modifier = Modifier.fillMaxWidth().height(300.dp).padding(16.dp)
        modifier = Modifier
            .fillMaxWidth().fillMaxHeight(0.8f).padding(16.dp)
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
                TableCell(text = "Response", weight = weight)
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
                TableCell(text = it.responseTime.toString(), weight = weight)
                TableCell(text = it.completedTime().toString(), weight = weight)
                TableCell(text = it.turnaroundTime().toString(), weight = weight)
            }
        }
    }
}
