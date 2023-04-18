package policy.fcfs

import utils.Info
import utils.ProgramData
import utils.Task
import utils.printResult

//    PID,Arrival Time,Burst Time
//    1,2,24
//    2,0,3
//    3,1,3

fun parse(string: String, index: Int): Task {
    val (pid, arrival, execution) = string.split(',', ignoreCase = false).map { it.toInt() }
    return Task(pid, arrival, execution, index)
}

internal fun runTask(tasks: List<Task>): ProgramData {
    val info: MutableList<Info> = mutableListOf()
    var contextCount = 0
    var currentRunTime = tasks[0].executionTime
    tasks[0].waitedTime = 0

    for (i in 1 until tasks.size) {
        val timeDiff = currentRunTime - tasks[i].arrivalTime
        info.add(Info(tasks[i].pid, currentRunTime, timeDiff))

        tasks[i].waitedTime = if (timeDiff > 0) timeDiff else 0
        currentRunTime += tasks[i].executionTime
        ++contextCount
    }
    val sorted = tasks.sortedBy { it.idx } // CSV 의 파일 순서대로 정렬
    return ProgramData(sorted, info, currentRunTime, contextCount)
}


//fun execute(fileInputStream: FileInputStream): Iterable<Task> {
//    return parseFromCSV(fileInputStream, ::parse).sortedBy{ it.arrivalTime } // FCFS 라서 도착한 순서대로 정렬
//        .let(::runTask).let(::printResult)
//}

fun execute(tasks: List<Task>): ProgramData {

    val sortedTasks: List<Task> = tasks.sortedBy{ it.arrivalTime } // FCFS 라서 도착한 순서대로 정렬

    return runTask(sortedTasks).let(::printResult)
}
