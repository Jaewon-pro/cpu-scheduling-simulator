package gui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import utils.ChartInfo
import utils.Process
import utils.ProgramData
import utils.parseTasksFromFile
import java.awt.FileDialog
import java.io.File

private enum class Policy(val label: String) {
    FCFS("FCFS"), SJF("SJF(Non-Preemptive)"), SRTF("SRTF(Preemptive)"),
    PRIORITY_NP("Priority(Non-Preemptive)"), PRIORITY("Priority(Preemptive)"), ROUND_ROBIN("Round Robin"),
    ;

    companion object {
        fun fromString(name: String): Policy =
            values().find { it.label.equals(name, ignoreCase = true) } ?: throw IllegalArgumentException()
    }
}

@Composable
fun menu(
    window: ComposeWindow,
    processes: List<Process>?,
    onFileLoaded: (List<Process>) -> Unit,
    onPerformed: (List<ChartInfo>) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.Magenta)
            .fillMaxWidth().height(50.dp)
    ) {
        var selectedPolicy: Policy by remember { mutableStateOf(Policy.FCFS) }
        var quantum: String by rememberSaveable { mutableStateOf("4") }
        var fileName: String by remember { mutableStateOf("") } // 로딩된 파일의 이름 저장

        openFileButton(
            window = window,
            onValueSaved = { savedValue -> onFileLoaded(savedValue) },
            onFileNameSaved = { fileName = it }
        )
        policySelector(
            items = Policy.values().map { it.label },
            selectedPolicy = selectedPolicy.label,
            onPolicySelected = { selectedPolicy = Policy.fromString(it) }
        )
        if (selectedPolicy == Policy.ROUND_ROBIN) {
            inputDigit("Quantum:", quantum) { quantum = it }
        }
        val isRunnable: Boolean = (processes != null) && !((selectedPolicy == Policy.ROUND_ROBIN) && quantum.isEmpty())
        runButton(
            isRunnable, selectedPolicy, processes,
            if (quantum.isEmpty()) 0 else quantum.toInt(),
            onValueSaved = { result -> onPerformed(result.info) }
        )
        Text(text = fileName, modifier = Modifier.padding(16.dp).align(Alignment.Top)) // A file name of the loaded one.
    }
}

@Composable
private fun openFileButton(
    window: ComposeWindow,
    onValueSaved: (List<Process>) -> Unit,
    onFileNameSaved: (String) -> Unit
) {
    Box {
        Button(onClick = {
            val openedFile: File? = openFileDialog(window, "Select a CSV file to run", listOf(".csv"))
            if (openedFile != null) {
                val openedTasks = parseTasksFromFile(openedFile)
                onValueSaved(openedTasks)
                onFileNameSaved(openedFile.name)
                //println("File Selected: ${openedFile.absoluteFile}")
            }
        }) {
            Icon(Icons.Default.Search, null)
            Text("Open File")
        }
    }
}

private fun openFileDialog(window: ComposeWindow, title: String, allowedExtensions: List<String>): File? {
    val fileSet = FileDialog(window, title, FileDialog.LOAD).apply {
        isMultipleMode = false
        // windows
        file = allowedExtensions.joinToString(";") { "*$it" } // e.g. '.jpg'
        // linux
        setFilenameFilter { _, name -> allowedExtensions.any(name::endsWith) }
        isVisible = true
    }.files.toSet()
    return if (fileSet.isEmpty()) null else fileSet.first()
}

@Composable
fun policySelector(
    items: List<String>,
    selectedPolicy: String,
    onPolicySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .wrapContentSize()
            .clickable(onClick = { expanded = true }),
        verticalAlignment = Alignment.CenterVertically // For Icon
    ) {
        Icon(Icons.Default.List, null)
        Text(modifier = Modifier.padding(16.dp), text = selectedPolicy)
        DropdownMenu(expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onPolicySelected(item)
                        expanded = false
                        //println("Policy selected: [$item]")
                    }
                ) { Text(text = item) }
            }
        }
    }
}

@Composable
private fun inputDigit(label: String, inputLabel: String, onChanged: (String) -> Unit) {
    Text(label)
    TextField(
        value = inputLabel,
        modifier = Modifier.size(100.dp),
        onValueChange = { newValue ->
            val matchResult = Regex("^[0-9]{0,5}\$").find(newValue) // To permit only digit
            if (matchResult != null) {
                val newQuantum: String =
                    if (matchResult.value == "0" || matchResult.value == "") "" else matchResult.value
                onChanged(newQuantum)
            }
        },
    )
}

@Composable
private fun runButton(
    enable: Boolean,
    selectedPolicy: Policy,
    processes: List<Process>?,
    quantum: Int,
    onValueSaved: (ProgramData) -> Unit
) {
    Box {
        Button(
            enabled = enable,
            onClick = {
                if (processes == null) {
                    return@Button
                }
                if (processes[0].isFinished()) {
                    processes.forEach { it.reset() }
                }  // 이미 한번 실행해서 남은 시간이 0인 경우
                val result: ProgramData = when (selectedPolicy) {
                    Policy.FCFS -> policy.executeFCFS(processes)
                    Policy.SJF -> policy.executeSJF(processes)
                    Policy.SRTF -> policy.executeSRTF(processes)
                    Policy.PRIORITY_NP -> policy.executePriorityNonPreemptive(processes)
                    Policy.PRIORITY -> policy.executePriority(processes)
                    Policy.ROUND_ROBIN -> policy.executeRoundRobin(processes, quantum)
                }
                onValueSaved(result) // To save result data into Main
            }) { Icon(Icons.Default.PlayArrow, null); Text("Run") }
    }
}
