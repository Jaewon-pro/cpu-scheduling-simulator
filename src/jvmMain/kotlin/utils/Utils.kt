package utils

import java.io.File

var taskHeader: List<String> = listOf()

data class ProgramData (
    val tasks: List<Task>,
    val info: List<ChartInfo>,
)

data class ChartInfo ( // 시간순으로 실행한 Task 들 단위 시간 저장, 나중에 간트차트 출력을 위해서
    val pid: Int,
    val timestamp: Int, // Since when
    val ranTime: Int, // How long executed
)

data class Task ( // PID 를 기준으로 Task 단위 저장
    val pid: Int,
    val arrivalTime: Int,
    val executionTime: Int,
    val priority: Int,
    val idx: Int, // csv 파일에서의 순서
) {
    var remainedTime: Int = executionTime
    var waitedTime: Int = 0 // 이건 1틱마다가 아닌 task 사이클 마다 계산
    var responseTime: Int = -1 // 처음으로 응답한 시간

    fun completedTime() = arrivalTime + executionTime + waitedTime
    fun turnaroundTime() = executionTime + waitedTime
    fun isFinished() = this.remainedTime == 0
    fun reset() {
        this.remainedTime = this.executionTime
        this.waitedTime = 0
        this.responseTime = -1
    }

    override fun toString(): String {
        var str = "Task PID: $pid, Arrival time: $arrivalTime, Burst time: $executionTime"
        if (priority >= 0) str += ", Priority: $priority"
        if (waitedTime != -1) str += ", Waited time: $waitedTime"
        return str
    }
}

fun parseTasksFromFile(file: File): List<Task> {
    val reader = file.bufferedReader()
    taskHeader = reader.readLine().split(',', ignoreCase = false)
    var index = 0
    return reader.lineSequence()
        .filter { it.isNotBlank() }
        .map { parseStringToTask(it, ++index) }.toList()
}

private fun parseStringToTask(string: String, index: Int): Task {
    return if (taskHeader.contains("Priority")) {
        val (pid, arrival, execution, priority) = string.split(',', ignoreCase = false).map { it.toInt() }
        Task(pid, arrival, execution, priority, index)
    } else {
        val (pid, arrival, execution) = string.split(',', ignoreCase = false).map { it.toInt() }
        Task(pid, arrival, execution, -1, index)
    }
}
