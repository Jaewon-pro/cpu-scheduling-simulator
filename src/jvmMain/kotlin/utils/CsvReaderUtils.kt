package utils

import java.io.File

class CsvReaderUtils {
    companion object {

        var taskHeader: List<String> = listOf()

        fun parseTasksFromFile(file: File): List<model.Process> {
            val reader = file.bufferedReader()
            taskHeader = reader.readLine().split(',', ignoreCase = false)
            var index = 0
            return reader.lineSequence()
                .filter { it.isNotBlank() }
                .map { parseStringToTask(it, ++index) }.toList()
        }

        private fun parseStringToTask(string: String, index: Int): model.Process {
            return if (taskHeader.contains("Priority")) {
                val (pid, arrival, execution, priority) = string.split(',', ignoreCase = false).map { it.toInt() }
                model.Process(pid, arrival, execution, priority, index)
            } else {
                val (pid, arrival, execution) = string.split(',', ignoreCase = false).map { it.toInt() }
                model.Process(pid, arrival, execution, -1, index)
            }
        }
    }
}