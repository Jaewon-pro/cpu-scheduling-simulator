package policy

import model.ChartInfo
import model.Process
import model.ProgramData
import java.util.*

internal fun calculateTasks(
    processes: List<Process>,
    compare: (Process, Process) -> Int,
    isPreemptive: Boolean
): ProgramData {
    val info: MutableList<ChartInfo> = mutableListOf(ChartInfo(-1, processes[0].arrivalTime, ranTime = 0))

    var currentRunTime = processes[0].arrivalTime
    val readyPool = PriorityQueue(compare)
    var currentRunProcess: Process? = null
    var idx = 0

    while (idx < processes.size) {
        while (idx < processes.size && currentRunTime == processes[idx].arrivalTime) { // 동일한 시간에 들어오는 경우 처리를 위해 if 가 아닌 while

            if (currentRunProcess === null) {
                currentRunProcess = processes[idx]
            } else if (compare(currentRunProcess, processes[idx]) <= 0 || !isPreemptive) {
                // If It's not preemptive, simply add it to the ready pool without comparing it to other tasks.
                // Or if It's preemptive, then compare the priority of 2 tasks.
                // 기존 Task 의 우선순위가 더 크면, 변화 X, current 계속 실행
                readyPool.add(processes[idx])
            } else { // If the priority of the new task is more urgent. 새로 비교할 Task 의 우선순위가 더 크면
                val ranTime = currentRunTime - info.last().timestamp
                if (ranTime != 0) {
                    info += ChartInfo(currentRunProcess.pid, currentRunTime, ranTime)
                } // To ignore if ranTime is zero

                processes[idx].responseTime = currentRunTime // 새로 들어온 Task 의 첫 응답 시간 설정
                readyPool.add(currentRunProcess)
                currentRunProcess = processes[idx]
            }
            ++idx
        }
        if (currentRunProcess === null) {
            continue
        }
        if (currentRunProcess.responseTime == -1) {
            currentRunProcess.responseTime = currentRunTime
        }

        ++currentRunTime
        --currentRunProcess.remainedTime
        if (currentRunProcess.isFinished()) {
//            println("${currentRunProcess.pid}: finished ## at $currentRunTime")
            currentRunProcess.waitedTime =
                currentRunTime - currentRunProcess.arrivalTime - currentRunProcess.executionTime
            info += ChartInfo(currentRunProcess.pid, currentRunTime, currentRunTime - info.last().timestamp)

            if (readyPool.isEmpty()) {
                currentRunProcess = null
            } else {
                val newRun = readyPool.remove()
                currentRunProcess = newRun
                if (currentRunProcess.responseTime == -1) {
                    currentRunProcess.responseTime = currentRunTime
                }
            }
        }
    }
    if (currentRunProcess != null) {
//        println("${currentRunProcess.pid}: current time: ${currentRunTime}, remained: ${currentRunProcess.remainedTime}")
        currentRunTime += currentRunProcess.remainedTime
        info += ChartInfo(currentRunProcess.pid, currentRunTime, currentRunTime - info.last().timestamp)
        currentRunProcess.waitedTime = currentRunTime - currentRunProcess.arrivalTime - currentRunProcess.executionTime
        currentRunProcess.remainedTime = 0
    }
    while (readyPool.isNotEmpty()) {
        val newRun = readyPool.remove()

        info += ChartInfo(newRun.pid, currentRunTime + newRun.remainedTime, newRun.remainedTime)
        if (newRun.responseTime == -1) {
            newRun.responseTime = currentRunTime
        }
        currentRunTime += newRun.remainedTime
        newRun.remainedTime = 0
        newRun.waitedTime = currentRunTime - newRun.arrivalTime - newRun.executionTime
//        println("${newRun.pid}: finished ! at $currentRunTime")
    }
    return ProgramData(processes, info)
}
