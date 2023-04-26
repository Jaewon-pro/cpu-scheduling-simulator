
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
import utils.Task

fun main() = application {
    //val icon = painterResource("icon.ico")
    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(width = 800.dp, height = 600.dp),
        title = "CPU Scheduling Simulator",
    ) {
        window.minimumSize = window.size
        Column {
            var tasks: List<Task>? by remember { mutableStateOf(null) }
            var info: List<ChartInfo>? by remember { mutableStateOf(null) }
            var taskCompleted: Boolean by remember { mutableStateOf(false) }

            menu(window, tasks,
                onFileLoaded = { tasks = it; info = null; taskCompleted = false },
                onPerformed = { info = it; taskCompleted = true }
            )
            if (!taskCompleted && tasks != null) { showTasksTable(tasks!!) }
            if (taskCompleted && tasks != null && info != null) {
                showResult(tasks!!, info!!)
                showChartAll(info!!)
            }
        }
    }
}
