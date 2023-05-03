package policy

import utils.ChartInfo
import utils.CircularQueue
import utils.Process
import utils.ProgramData

private fun calculateRoundRobin(processes : List<Process>, quantum: Int): ProgramData {
    val info: MutableList<ChartInfo> = mutableListOf(ChartInfo(-1, processes[0].arrivalTime, 0)) // 처음에 0 표시

    var currentRunTime = processes[0].arrivalTime
    val readyPool = CircularQueue<Process>(processes.size + 1)
    readyPool.add(processes[0])
    processes[0].responseTime = 0
    var nextIdx = 1

    while (readyPool.isNotEmpty()) {
        val newRunProcess: Process = readyPool.poll()
        val performedTime = if (newRunProcess.remainedTime >= quantum) quantum else newRunProcess.remainedTime % quantum
        if (newRunProcess.responseTime == -1) newRunProcess.responseTime = currentRunTime

        currentRunTime += performedTime
        newRunProcess.remainedTime -= performedTime

        info += ChartInfo(newRunProcess.pid, currentRunTime, performedTime) // ran at $currentRunTime

        while (nextIdx < processes.size && currentRunTime >= processes[nextIdx].arrivalTime) {
            readyPool.add(processes[nextIdx])
            ++nextIdx
        }

        if (newRunProcess.remainedTime == 0) { // Job finished
            newRunProcess.waitedTime = currentRunTime - newRunProcess.arrivalTime - newRunProcess.executionTime
            //println("${performed.pid}: finished,, at $currentRunTime")
        } else {
            readyPool.add(newRunProcess)
        }
    }
//    println("Total Executed time : $currentRunTime")
//    println("Context Switched : ${info.size - 2}")
    return ProgramData(processes, info)
}

fun executeRoundRobin(processes: List<Process>, quantum: Int): ProgramData {
    return calculateRoundRobin(processes, quantum)
}
