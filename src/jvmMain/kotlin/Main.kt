
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import gui.menu
import gui.showChart
import gui.showResult
import gui.showTable
import utils.Info
import utils.Task

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        window.title = "CPU Scheduler"
        Column {
            //var data: ProgramData? by remember { mutableStateOf(null) }
            var tasks: List<Task>? by remember { mutableStateOf(null) }
            var info: List<Info>? by remember { mutableStateOf(null) }
            var showResult1: Boolean by remember { mutableStateOf(false) }

            menu(window,
                tasks,
                onLoader = { tasks = it; showResult1 = false },
                onPerformed = { info = it.info; showResult1 = true })
            if (!showResult1 && tasks != null) showTable(tasks!!)
            if (showResult1 && tasks != null && info != null) {
                showResult(tasks!!, info!!)
                showChart(info!!)
            }
        }
    }
}
