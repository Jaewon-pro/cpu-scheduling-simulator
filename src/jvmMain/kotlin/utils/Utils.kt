package utils

import java.io.FileInputStream
import java.util.*

var taskHeader: List<String> = listOf()

data class ProgramData (
    val tasks: List<Task>,
    val info: List<Info>?,
    val totalRunTime: Int, // 총 실행 시간
    val contextSwitched: Int // 문맥교환 횟수
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
    var completedTime: Int = 0 ////// 나중에 지우기
//    var turnaroundTime: Int = -1 // 프로세스가 완료된 시간에서 프로세스가 도착한 시간을 뺀 시간
//    var responseTime: Int = -1 // 처음 실행되는 시간에서 프로세스가 도착한 시간을 뺀 시간
    var insertedTime: Int = -1

    fun completedTime() = arrivalTime + executionTime + waitedTime
    fun turnaroundTime() = executionTime + waitedTime

    // FCFS
    constructor(pid: Int, arrivalTime: Int, executionTime: Int, idx: Int) : this(pid, arrivalTime, executionTime, -1, idx)
    // SJF
    constructor(pid: Int, arrivalTime: Int, executionTime: Int) : this(pid, arrivalTime, executionTime, -1, -1)

    override fun toString(): String {
        var str: String = "Task PID: $pid, Arrival time: $arrivalTime, Burst time: $executionTime"
        if (priority >= 0) str += ", Priority: $priority"
        if (waitedTime != -1) str += ", Waited time: $waitedTime"
        return str
    }
}

fun <T> parseFromCSV(fileInputStream: FileInputStream, parse: (String, Int) -> T): List<T> {

    val reader = fileInputStream.bufferedReader()
    taskHeader = reader.readLine().split(',', ignoreCase = false)
    var index = 0
    return reader.lineSequence()
        .filter { it.isNotBlank() }
        .map { parse(it, ++index) }.toList()
}

fun <T> parseFromCSV(fileInputStream: FileInputStream, parse: (String) -> T): List<T> {
    //val inputStream: InputStream = FileInputStream(csvPath)
    val reader = fileInputStream.bufferedReader()
    taskHeader = reader.readLine().split(',', ignoreCase = false)
    return reader.lineSequence()
        .filter { it.isNotBlank() }
        .map { parse(it) }.toList()
}

fun printResult (data: ProgramData): ProgramData {
    val tasks = data.tasks
    tasks.forEach{ println(it) }
    return data
}


fun runTaskPreemptive(tasks: List<Task>, compare: (Task, Task) -> Int): ProgramData { // 선점형, priority, sjf 공용
    var currentRunTime = 0
    val readyPool = PriorityQueue(compare)
    var currentRunTask: Task? = null
    var idx = 0
    var contextSwitched = 0
    val info: MutableList<Info> = mutableListOf()

    while (idx < tasks.size) {
        if (currentRunTime == tasks[idx].arrivalTime) {
            if (currentRunTask === null) currentRunTask = tasks[idx]
            else if (compare(currentRunTask, tasks[idx]) < 0) { // 기존 Task 의 우선순위가 더 크면, 변화 X
                tasks[idx].insertedTime = currentRunTime
                readyPool.add(tasks[idx])
            } else { // 새로 비교할 Task 의 우선순위가 더 크면
                info += Info(currentRunTask.pid, currentRunTime, 111)
                currentRunTask.insertedTime = currentRunTime
                readyPool.add(currentRunTask)
                currentRunTask = tasks[idx]
                ++contextSwitched
            }
            ++idx
        }
        ++currentRunTime
        if (currentRunTask === null) continue

        --currentRunTask.remainedTime
        if (currentRunTask.remainedTime == 0) {
            currentRunTask.completedTime = currentRunTime
            //currentRunTask!!.turnaround time
            if (readyPool.size == 0) {
                currentRunTask = null
            }
            else {
                val newRun = readyPool.remove()
                newRun.waitedTime += currentRunTime - newRun.insertedTime
                currentRunTask = newRun
            }
            ++contextSwitched
        }
    }
    if (currentRunTask != null) {
        currentRunTime += currentRunTask.remainedTime
        currentRunTask.remainedTime = 0
        currentRunTask.completedTime = currentRunTime
        ++contextSwitched
    }
    while (readyPool.isNotEmpty()) {
        val newRun = readyPool.remove()

        newRun.waitedTime += currentRunTime - newRun.insertedTime
        currentRunTime += newRun.remainedTime
        newRun.remainedTime = 0
        newRun.completedTime = currentRunTime
        ++contextSwitched
    }
    return ProgramData(tasks, info, currentRunTime, contextSwitched)
}

fun Lazarus(tasks : List<Task>) {
    tasks.forEach {
        it.remainedTime = it.executionTime
        it.insertedTime = -1
        it.waitedTime = 0
    }
}
