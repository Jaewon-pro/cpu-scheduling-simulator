
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import gui.menu
import gui.showChartAll
import gui.showResult
import gui.showTasksTable
import utils.ChartInfo
import utils.Process

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(width = 800.dp, height = 600.dp),
        title = "CPU Scheduling Simulator",
    ) {
        window.minimumSize = window.size
        Column {
            var processes: List<Process>? by remember { mutableStateOf(null) }
            var info: List<ChartInfo>? by remember { mutableStateOf(null) }
            var taskCompleted: Boolean by remember { mutableStateOf(false) }

            menu(window, processes,
                onFileLoaded = { processes = it; info = null; taskCompleted = false },
                onPerformed = { info = it; taskCompleted = true }
            )
            if (!taskCompleted && processes != null) {
                showTasksTable(processes!!)
            }
            if (taskCompleted && processes != null && info != null) {
                showResult(processes!!, info!!)
                showChartAll(info!!)
            }
        }
    }
}
