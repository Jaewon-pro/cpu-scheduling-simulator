
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import gui.menu
import gui.showChartAll
import gui.showResult
import gui.showTasksTable
import utils.ChartInfo
import utils.Task
import java.awt.Dimension

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        window.title = "CPU Scheduler"
        window.minimumSize = Dimension(800, 600)
        Column {
            var tasks: List<Task>? by remember { mutableStateOf(null) }
            var info: List<ChartInfo>? by remember { mutableStateOf(null) }
            var showResult1: Boolean by remember { mutableStateOf(false) }

            menu(window,
                tasks,
                onLoader = { tasks = it; info = null; showResult1 = false },
                onPerformed = { info = it.info; showResult1 = true })
            if (!showResult1 && tasks != null) { showTasksTable(tasks!!) }
            if (showResult1 && tasks != null && info != null) {
                showResult(tasks!!, info!!)
                showChartAll(info!!)
            }
        }
    }
}
