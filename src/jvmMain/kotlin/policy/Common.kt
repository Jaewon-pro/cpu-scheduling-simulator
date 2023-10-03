package policy

import model.ChartInfo
import model.Process
import model.ProgramData
import utils.ChartUtils
import java.util.*

internal fun calculateTasks(
    processes: List<Process>,
    compare: (Process, Process) -> Int,
    isPreemptive: Boolean
): ProgramData {
    val info: MutableList<ChartInfo> = ChartUtils.makeInitialList(processes.first().arrivalTime)

    val readyPool = PriorityQueue(compare)

    var currentRunProcess: Process? = null
    var currentRunTime = processes.first().arrivalTime
    var idx = 0

    while (idx < processes.size) {
        while (hasSameTimeProcess(idx, currentRunTime, processes)) {
            val process = processes[idx]

            if (currentRunProcess == null) {
                currentRunProcess = process
            } else if (isPreemptive && canTakeRunPlace(compare, currentRunProcess, process)) {
                addChart(currentRunProcess, currentRunTime, info)
                process.saveResponseTime(currentRunTime) // 새로 들어온 Task 의 첫 응답 시간 설정
                readyPool.add(currentRunProcess)
                currentRunProcess = process
            } else {
                readyPool.add(process)
            }
            ++idx
        }

        if (currentRunProcess == null) continue

        currentRunTime += tick(currentRunProcess, currentRunTime)

        if (currentRunProcess.isFinished()) {
            handleFinish(currentRunProcess, currentRunTime, info)
            currentRunProcess = readyPool.poll()
        }
    }

    if (currentRunProcess != null) {
        currentRunTime += runAll(currentRunProcess)
        handleFinish(currentRunProcess, currentRunTime, info)
    }

    finishAllProcess(readyPool, currentRunTime, info)

    return ProgramData(processes, info)
}


private fun hasSameTimeProcess(idx: Int, currentRunTime: Int, processes: List<Process>): Boolean {
    return idx < processes.size && currentRunTime == processes[idx].arrivalTime
}

private fun canTakeRunPlace(
    compare: (Process, Process) -> Int,
    currentRunProcess: Process,
    inputProcess: Process
): Boolean {
    return compare(currentRunProcess, inputProcess) > 0
}


private fun saveWaitedTime(process: Process, currentRunTime: Int) {
    process.waitedTime = currentRunTime - process.arrivalTime - process.executionTime
}

private fun tick(process: Process, currentRunTime: Int): Int {
    val runTime = 1
    process.saveResponseTime(currentRunTime)
    process.remainedTime -= runTime
    return runTime
}

private fun addChart(process: Process, currentRunTime: Int, info: MutableList<ChartInfo>) {
    val ranTime = currentRunTime - info.last().timestamp
    if (ranTime == 0) return
    val chart = ChartInfo(process.pid, currentRunTime, ranTime)
    info += chart
}

private fun runAll(process: Process): Int {
    val ranTime = process.remainedTime
    process.remainedTime = 0
    return ranTime
}


private fun handleFinish(process: Process, currentRunTime: Int, info: MutableList<ChartInfo>) {
    saveWaitedTime(process, currentRunTime)
    addChart(process, currentRunTime, info)
}

private fun finishAllProcess(readyPool: PriorityQueue<Process>, startTime: Int, info: MutableList<ChartInfo>) {
    var currentRunTime = startTime
    while (readyPool.isNotEmpty()) {
        val newRun = pollAndSaveResponseTime(readyPool, currentRunTime)
        currentRunTime += runAll(newRun)
        handleFinish(newRun, currentRunTime, info)
    }
}

private fun pollAndSaveResponseTime(readyPool: PriorityQueue<Process>, currentRunTime: Int): Process {
    val newRun = readyPool.remove()
    newRun.saveResponseTime(currentRunTime)
    return newRun
}
