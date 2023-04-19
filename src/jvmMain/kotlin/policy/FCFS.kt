package policy

import utils.Info
import utils.ProgramData
import utils.Task
import utils.printResult

//    PID,Arrival Time,Burst Time
//    1,2,24
//    2,0,3
//    3,1,3

private fun runTaskFCFS(tasks: List<Task>): ProgramData {
    val info: MutableList<Info> = mutableListOf(Info(-1, tasks[0].arrivalTime, 0))
    var currentRunTime = tasks[0].executionTime
    tasks[0].responseTime = currentRunTime
    tasks[0].waitedTime = 0

    info += Info(tasks[0].pid, currentRunTime, tasks[0].executionTime)
    for (i in 1 until tasks.size) {
        val timeDiff = currentRunTime - tasks[i].arrivalTime
        tasks[i].waitedTime = if (timeDiff > 0) timeDiff else 0
        tasks[i].responseTime = currentRunTime - tasks[i].arrivalTime

        currentRunTime += tasks[i].executionTime
        info.add(Info(tasks[i].pid, currentRunTime, tasks[i].executionTime))
    }
    val sorted = tasks.sortedBy { it.idx } // CSV 의 파일 순서대로 정렬
    return ProgramData(sorted, info)
}


fun executeFCFS(tasks: List<Task>): ProgramData {
    val sortedTasks: List<Task> = tasks.sortedBy{ it.arrivalTime } // FCFS 라서 도착한 순서대로 정렬
    return runTaskFCFS(sortedTasks).let(::printResult)
}
