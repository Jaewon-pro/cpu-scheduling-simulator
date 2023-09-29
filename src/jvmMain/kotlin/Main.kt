import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import gui.menu
import gui.showChartAll
import gui.showResult
import gui.showTasksTable
import model.ChartInfo
import model.Process


fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(width = 800.dp, height = 600.dp),
        title = "CPU Scheduling Simulator",
    ) {
        window.minimumSize = window.size
        Column {
            program(window)
        }
    }
}


@Composable
private fun program(window: ComposeWindow) {
    var processes: List<Process> by remember { mutableStateOf(listOf()) }
    var info: List<ChartInfo> by remember { mutableStateOf(listOf()) }
    var isTaskCompleted: Boolean by remember { mutableStateOf(false) }

    menu(window, processes,
        onFileLoaded = { processes = it; info = listOf(); isTaskCompleted = false },
        onPerformed = { info = it; isTaskCompleted = true }
    )
    if (processes.isEmpty()) return

    if (!isTaskCompleted) {
        showTasksTable(processes)
        return
    }

    resultView(processes, info)
}

@Composable
private fun resultView(processes: List<Process>, info: List<ChartInfo>) {
    if (processes.isEmpty() || info.isEmpty()) return
    showResult(processes, info)
    showChartAll(info)
}
