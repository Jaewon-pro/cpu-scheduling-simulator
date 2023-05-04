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
import utils.Process
import utils.taskHeader
import kotlin.math.roundToInt

@Composable
private fun RowScope.tableCell(
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
fun showTasksTable(processes: List<Process>) {
    if (processes[0].isFinished()) return
    // Each cell of a column must have the same weight.
    val weight: Float = (taskHeader.size / 100.0f) * 100 / 100.0f.roundToInt() // 요소를 100으로 나눈 퍼센트값
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // The LazyColumn will be our table. Notice the use of the weights below
    LazyColumn(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f).padding(16.dp)//.height(500.dp)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        scrollState.scrollBy(-delta)
                    }
                },
            )
    ) {
        item {
            Row(Modifier.background(Color(230, 230, 250))) {
                taskHeader.forEach { tableCell(text = it, weight = weight) }
            }
        }
        // Here are all the lines of your table.
        items(processes) {
            val (pid, arrival, burst) = it
            Row(Modifier.fillMaxWidth()) {
                tableCell(text = pid.toString(), weight = weight)
                tableCell(text = arrival.toString(), weight = weight)
                tableCell(text = burst.toString(), weight = weight)
                if (taskHeader.contains("Priority")) {
                    tableCell(text = it.priority.toString(), weight = weight)
                }
            }
        }
    }
}

@Composable
fun showResult(processes: List<Process>, info: List<ChartInfo>) { // 성능 지표 테이블
    Column {
        val averages = getAvgList(processes)
        val totalTime = info.last().timestamp
        val throughput: Float = processes.size / totalTime.toFloat()
        val contextSwitched = info.size - 2
        averageResultTable(throughput, averages, contextSwitched)
        resultTable(processes)
    }
}

private fun getAvgList(processes: List<Process>): List<Float> {
    var avgWaitedTime = 0.0f // Store the average of the array
    var avgResponseTime = 0.0f
    var avgTurnaroundTime = 0.0f

    for (i in processes.indices) { // Traverse tasks
        avgWaitedTime += ((processes[i].waitedTime - avgWaitedTime) / (i + 1))
        avgResponseTime += ((processes[i].responseTime - avgResponseTime) / (i + 1))
        avgTurnaroundTime += ((processes[i].turnaroundTime() - avgTurnaroundTime) / (i + 1))
    }
//    println(listOf(avgWaitedTime, avgResponseTime, avgTurnaroundTime))
    return listOf(avgWaitedTime, avgResponseTime, avgTurnaroundTime)
}

@Composable
private fun averageResultTable(throughput: Float, averages: List<Float>, contextSwitched: Int) {
    val weight: Float = (5 / 100.0f) * 100 / 100.0f.roundToInt() // 요소를 100으로 나눈 퍼센트값

    LazyColumn(modifier = Modifier.fillMaxWidth().height(100.dp).padding(16.dp)) {
        item {
            Row(Modifier.background(Color(230, 230, 250))) {
                tableCell(text = "Throughput", weight = weight)
                tableCell(text = "Context Switch", weight = weight)
                tableCell(text = "Avg Waited", weight = weight)
                tableCell(text = "Avg Response", weight = weight)
                tableCell(text = "Avg Turnaround", weight = weight)
            }
        }
        items(1) {
            Row(Modifier.fillMaxWidth()) {
                tableCell(text = throughput.toString(), weight = weight)
                tableCell(text = contextSwitched.toString(), weight = weight)
                tableCell(text = averages[0].toString(), weight = weight)
                tableCell(text = averages[1].toString(), weight = weight)
                tableCell(text = averages[2].toString(), weight = weight)
            }
        }
    }
}

@Composable
private fun resultTable(processes: List<Process>) {
    val weight: Float = ((taskHeader.size + 3) / 100.0f) * 100 / 100.0f.roundToInt() // Column 요소 개수를 100으로 나눈 퍼센트 값
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        state = scrollState,
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
            Row(Modifier.background(Color(230, 230, 250))) {
                taskHeader.forEach { tableCell(text = it, weight = weight) }
                tableCell(text = "Waited", weight = weight)
                tableCell(text = "Response", weight = weight)
                tableCell(text = "Completed", weight = weight)
                tableCell(text = "Turnaround", weight = weight)
            }
        }
        items(processes) {
            Row(Modifier.fillMaxWidth()) {
                val (pid, arrival, burst) = it
                tableCell(text = pid.toString(), weight = weight)
                tableCell(text = arrival.toString(), weight = weight)
                tableCell(text = burst.toString(), weight = weight)
                if (taskHeader.contains("Priority")) {
                    tableCell(text = it.priority.toString(), weight = weight)
                }

                tableCell(text = it.waitedTime.toString(), weight = weight)
                tableCell(text = it.responseTime.toString(), weight = weight)
                tableCell(text = it.completedTime().toString(), weight = weight)
                tableCell(text = it.turnaroundTime().toString(), weight = weight)
            }
        }
    }
}
