
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
import utils.ProgramData
import utils.Task

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        window.title = "CPU Scheduler"
        Column {
            var data: ProgramData? by remember { mutableStateOf(null) }
            var tasks: List<Task>? by remember { mutableStateOf(null) }
            var showResult1: Boolean by remember { mutableStateOf(false) }

            menu(window,
                tasks,
                onLoader = { tasks = it; showResult1 = false },
                //onPerformed = { data = it; tasks = null })
                onPerformed = { data = it; showResult1 = true })
            if (!showResult1 && tasks != null) showTable(tasks!!)
            if (showResult1 && data != null) {
                showResult(data)
                showChart(data!!.info)
            }
        }
    }
}

//if (tasks != null) showTable(tasks!!)
//if (tasks == null && data != null) {
//    showResult(data)
//    showChart(data!!.info)
//}