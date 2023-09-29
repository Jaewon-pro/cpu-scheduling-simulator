package policy

import model.ChartInfo
import model.Process
import model.ProgramData
import utils.ChartUtils

//    PID,Arrival Time,Burst Time
//    1,1,24
//    2,2,3
//    3,3,3

fun executeFCFS(processes: List<Process>): ProgramData {
    val sortedProcesses: List<Process> = processes.sortedBy { it.arrivalTime } // FCFS 라서 도착한 순서대로 정렬
    return runTaskFCFS(sortedProcesses)
}


private fun runTaskFCFS(processes: List<Process>): ProgramData {
    val first = processes.first()

    val chartInfoList: MutableList<ChartInfo> = ChartUtils.makeInitialList(first.arrivalTime)

    var currentRunTime = finishProcess(first, 0)

    chartInfoList += ChartUtils.makeChartInfo(first, currentRunTime)

    processes.drop(1).map { process ->
        currentRunTime = finishProcess(process, currentRunTime)
        chartInfoList += ChartUtils.makeChartInfo(process, currentRunTime)
    }

    return ProgramData(processes, chartInfoList)
}


private fun finishProcess(process: Process, currentRunTime: Int): Int {
    process.waitedTime = getTimeDifference(currentRunTime, process.arrivalTime)
    process.responseTime = currentRunTime
    process.remainedTime = 0
    return currentRunTime + process.executionTime
}



private fun getTimeDifference(now: Int, before: Int): Int {
    return (now - before).coerceAtLeast(0)
}
