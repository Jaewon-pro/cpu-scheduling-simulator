
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import gui.menu
import gui.showChart
import gui.showTable
import utils.ProgramData
import utils.Task

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        Column {
            var data: ProgramData? by remember { mutableStateOf(null) }
            var tasks: List<Task>? by remember { mutableStateOf(null) }

            menu(window,
                tasks,
                onLoader = { tasks = it },
                onPerformed = { data = it })
            showTable(tasks)
            if (data != null) showChart(data!!.info!!)
        }
    }
}

//////https://github.com/JetBrains/compose-multiplatform/blob/master/examples/notepad/src/main/kotlin/window/NotepadWindowState.kt
