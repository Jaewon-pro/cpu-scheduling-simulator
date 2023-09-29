package policy

import model.ChartInfo
import model.Process
import model.ProgramData
import utils.ChartUtils
import utils.CircularQueue
import kotlin.math.min


fun executeRoundRobin(processes: List<Process>, quantum: Int): ProgramData {
    return calculateRoundRobin(processes, quantum)
}


private fun calculateRoundRobin(processes: List<Process>, quantum: Int): ProgramData {

    val chartInfoList: MutableList<ChartInfo> = ChartUtils.makeInitialList(processes.first().arrivalTime)

    val readyPool = makeReadyPool(processes)
    var currentRunTime = getStartTime(processes.first())
    var nextIdx = 1

    while (readyPool.isNotEmpty()) {
        val process: Process = readyPool.poll()

        val performedTime = runProcessAsMuchItCan(process, quantum, currentRunTime)

        currentRunTime += performedTime
        chartInfoList += ChartInfo(process.pid, currentRunTime, performedTime)

        while (isNextProcessArrived(nextIdx, currentRunTime, processes)) {
            readyPool.add(processes[nextIdx])
            ++nextIdx
        }

        if (process.isFinished()) {
            saveWaitedTime(currentRunTime, process)
            continue
        }
        readyPool.add(process)
    }

    return ProgramData(processes, chartInfoList)
}


private fun makeReadyPool(processes: List<Process>): CircularQueue<Process> {
    val readyPool = CircularQueue<Process>(processes.size + 1)
    readyPool.add(processes.first())
    return readyPool
}

private fun getStartTime(first: Process): Int {
    first.saveResponseTime(0)
    return first.arrivalTime
}

private fun getPerformedTime(process: Process, quantum: Int): Int {
    return min(process.remainedTime, quantum)
}

private fun runProcessAsMuchItCan(process: Process, quantum: Int, currentRunTime: Int): Int {
    val performedTime = getPerformedTime(process, quantum)
    process.remainedTime -= performedTime
    process.saveResponseTime(currentRunTime)

    return performedTime
}

private fun saveWaitedTime(currentRunTime: Int, process: Process) {
    process.waitedTime = currentRunTime - process.arrivalTime - process.executionTime
}

private fun isNextProcessArrived(nextIdx: Int, currentRunTime: Int, processes: List<Process>): Boolean {
    return nextIdx < processes.size && currentRunTime >= processes[nextIdx].arrivalTime
}
