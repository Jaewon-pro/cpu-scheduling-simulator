package utils

import java.io.FileInputStream
import java.util.*

var taskHeader: List<String> = listOf()

data class ProgramData (
    val tasks: List<Task>,
    val info: List<Info>,
    //val contextSwitched: Int // 문맥교환 횟수
)

data class Info (
    val pid: Int,
    val time: Int,
    val ranTime: Int,
)

data class Task (
    val pid: Int,
    val arrivalTime: Int,
    val executionTime: Int,
    val priority: Int,
    val idx: Int, // csv 파일에서의 순서
) {
    var remainedTime: Int = executionTime
    var waitedTime: Int = 0 // 이건 1틱마다가 아닌 task 사이클 마다 계산
    var insertedTime: Int = -1
    var responseTime: Int = -1 // 처음으로 응답한 시간

    fun completedTime() = arrivalTime + executionTime + waitedTime
    fun turnaroundTime() = executionTime + waitedTime

    override fun toString(): String {
        var str: String = "Task PID: $pid, Arrival time: $arrivalTime, Burst time: $executionTime"
        if (priority >= 0) str += ", Priority: $priority"
        if (waitedTime != -1) str += ", Waited time: $waitedTime"
        return str
    }
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

fun parseFromCSV(fileInputStream: FileInputStream): List<Task> {

    val reader = fileInputStream.bufferedReader()
    taskHeader = reader.readLine().split(',', ignoreCase = false)
    var index = 0
    return reader.lineSequence()
        .filter { it.isNotBlank() }
        .map { parseStringToTask(it, ++index) }.toList()
}

fun printResult(data: ProgramData): ProgramData {
    data.tasks.forEach{ println(it) }
    return data
}


fun runTaskPreemptive(tasks: List<Task>, compare: (Task, Task) -> Int): ProgramData { // 선점형, priority, sjf 공용
    var currentRunTime = 0
    val readyPool = PriorityQueue(compare)
    var currentRunTask: Task? = null
    var idx = 0
    val info: MutableList<Info> = mutableListOf(Info(-1, tasks[0].arrivalTime, 0))

    while (idx < tasks.size) {
        println("idx: $idx")
        if (currentRunTime == tasks[idx].arrivalTime) {

            if (currentRunTask === null) currentRunTask = tasks[idx]
            else if (compare(currentRunTask, tasks[idx]) <= 0) { // 기존 Task 의 우선순위가 더 크면, 변화 X, current 계속 실행
                tasks[idx].insertedTime = currentRunTime
                readyPool.add(tasks[idx])
            } else { // 새로 비교할 Task 의 우선순위가 더 크면
                val ranTime = currentRunTime - info.last().time
                if (ranTime != 0)  info += Info(currentRunTask.pid, currentRunTime, ranTime) // ranTime 이 0인 경우 방지

                currentRunTask.insertedTime = currentRunTime
                readyPool.add(currentRunTask)
                currentRunTask = tasks[idx]
            }
            ++idx
        }
        ++currentRunTime
        if (currentRunTask === null) continue

        --currentRunTask.remainedTime
        if (currentRunTask.remainedTime == 0) {
            print(currentRunTask.pid)
            println(": finished // at $currentRunTime")
            info += Info(currentRunTask.pid, currentRunTime, currentRunTime - info.last().time)

            if (readyPool.size == 0) { currentRunTask = null }
            else {
                val newRun = readyPool.remove()
                newRun.waitedTime += currentRunTime - newRun.insertedTime
                currentRunTask = newRun
            }
        }
    }
    if (currentRunTask != null) {
        currentRunTime += currentRunTask.remainedTime
        info += Info(currentRunTask.pid, currentRunTime, currentRunTask.remainedTime)
        currentRunTask.remainedTime = 0
    }
    while (readyPool.isNotEmpty()) {
        val newRun = readyPool.remove()

        info += Info(newRun.pid, currentRunTime + newRun.remainedTime, newRun.remainedTime)
        newRun.waitedTime += currentRunTime - newRun.insertedTime
        currentRunTime += newRun.remainedTime
        newRun.remainedTime = 0
        print(newRun.pid)
        println(": finished at $currentRunTime")
    }
    return ProgramData(tasks, info)
}

fun lazarus(tasks : List<Task>) { // reset tasks
    tasks.forEach {
        it.remainedTime = it.executionTime
        it.insertedTime = -1
        it.waitedTime = 0
    }
}