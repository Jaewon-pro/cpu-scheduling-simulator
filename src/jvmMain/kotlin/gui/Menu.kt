package gui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import utils.ProgramData
import utils.Task
import utils.parseFromCSV
import java.awt.FileDialog
import java.io.File
import java.io.FileInputStream
import kotlin.system.measureTimeMillis

private enum class Policy(val label: String) {
    FCFS("FCFS"), SRTF("Shortest-Remaining-First"), PRIORITY("Priority"), ROUND_ROBIN("Round Robin")
    ;
    companion object {
        fun fromString(name: String): Policy =
            values().find { it.label.equals(name, ignoreCase = true) } ?: throw IllegalArgumentException()
    }
}

@Composable
fun menu(window: ComposeWindow, tasks: List<Task>?, onLoader: (List<Task>) -> Unit, onPerformed: (ProgramData) -> Unit) {
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
            onValueSaved = { savedValue -> onLoader(savedValue) },
            onFileNameSaved = { fileName = it })
        policySelector(
            items = Policy.values().map { it.label },
            selectedItem = selectedPolicy.label,
            onItemSelected = { selectedPolicy = Policy.fromString(it) })
        if (selectedPolicy === Policy.ROUND_ROBIN) { inputQuantum(quantum) { quantum = it } }
        runButton((tasks != null),
            selectedPolicy, tasks,
            if (quantum.isEmpty()) 0 else quantum.toInt(),
            onValueSaved = { result -> onPerformed(result) })
        Text(text = fileName, modifier = Modifier.padding(16.dp).align(Alignment.Top))
    }
}
//Icon(Icons.Default.PlayArrow,null)
@Composable
private fun openFileButton(window: ComposeWindow, onValueSaved: (List<Task>) -> Unit, onFileNameSaved: (String) -> Unit) {
    Box {
        Button(onClick = {
            val openedFiles = openFileDialog(window, "Select a CSV file to run", listOf(".csv"), allowMultiSelection = false)
            if (openedFiles.isEmpty()) { println("Please, select the file") }
            else {
                val openedTasks = parseFile(FileInputStream(openedFiles.first()))
                onValueSaved(openedTasks)
                onFileNameSaved(openedFiles.first().name)
                println("파일 선택됨: ${openedFiles.first().absoluteFile}")
            }
        }) {
            Icon(Icons.Default.Add,null)
            Text("Open File")
        }
    }
}

private fun openFileDialog(window: ComposeWindow, title: String, allowedExtensions: List<String>, allowMultiSelection: Boolean = true): Set<File> {
    return FileDialog(window, title, FileDialog.LOAD).apply {
        isMultipleMode = allowMultiSelection

        // windows
        file = allowedExtensions.joinToString(";") { "*$it" } // e.g. '.jpg'

        // linux
        setFilenameFilter { _, name -> allowedExtensions.any(name::endsWith) }
        isVisible = true
    }.files.toSet()
}

private fun parseFile(fileInputStream: FileInputStream): List<Task> {
    val tasks = parseFromCSV(fileInputStream)
    fileInputStream.close()
    return tasks
}

@Composable
fun policySelector(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .wrapContentSize()
        .clickable(onClick = { expanded = true })
    ) {
        Icon(Icons.Default.List,null)
        Text(modifier = Modifier.padding(16.dp), text = selectedItem)
        DropdownMenu(expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                        println("Policy selected: [$item]")
                    }
                ) { Text(text = item) }
            }
        }
    }
}

@Composable
private fun inputQuantum(quantum: String, onChanged: (String) -> Unit) {
    TextField(
        value = quantum,
        onValueChange = {
                newValue ->
            val newQuantum = newValue.replace(Regex("\\D+"), "") // To permit only digit
            val maxLen = 5
            if (newQuantum.length > maxLen) newQuantum.slice(IntRange(0, maxLen))
            onChanged(newQuantum)
        },
        modifier = Modifier.size(100.dp)
    )
}

@Composable
private fun runButton(enable: Boolean, selectedPolicy: Policy, tasks: List<Task>?, quantum: Int, onValueSaved: (ProgramData) -> Unit) {
    Box {
        Button(enabled = enable,
            onClick = {
                if (tasks == null) { return@Button }

                if (tasks[0].isFinished()) { tasks.forEach { it.reset() } }  // 이미 한번 실행해서 남은 시간이 0인 경우
                val duration = measureTimeMillis {
                    val result: ProgramData = when (selectedPolicy) {
                        Policy.FCFS -> policy.executeFCFS(tasks)
                        Policy.SRTF -> policy.executeSRTF(tasks)
                        Policy.PRIORITY -> policy.executePriority(tasks)
                        Policy.ROUND_ROBIN -> policy.executeRoundRobin(tasks, quantum)
                    }
                    onValueSaved(result) // To save result data into Main
                }
                println("[${selectedPolicy.label}] Duration: $duration ms")

            }) { Icon(Icons.Default.PlayArrow,null); Text("Run") }
    }
}
