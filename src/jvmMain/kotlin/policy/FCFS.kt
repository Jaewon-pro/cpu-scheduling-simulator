package policy

import utils.ChartInfo
import utils.Process
import utils.ProgramData

//    PID,Arrival Time,Burst Time
//    1,1,24
//    2,2,3
//    3,3,3

private fun runTaskFCFS(processes: List<Process>): ProgramData {
    val info: MutableList<ChartInfo> = mutableListOf(ChartInfo(-1, processes[0].arrivalTime, 0))
    var currentRunTime = processes[0].executionTime
    processes[0].responseTime = 0
    processes[0].remainedTime = 0 // tasks 실행했다는 걸 저장
    processes[0].waitedTime = 0

    info += ChartInfo(processes[0].pid, currentRunTime, processes[0].executionTime)
    for (i in 1 until processes.size) {
        val timeDiff = currentRunTime - processes[i].arrivalTime
        processes[i].waitedTime = if (timeDiff > 0) timeDiff else 0
        processes[i].responseTime = currentRunTime

        currentRunTime += processes[i].executionTime
        processes[i].remainedTime = 0 // fcfs 에서는 안쓰는 변수
        info.add(ChartInfo(processes[i].pid, currentRunTime, processes[i].executionTime))
    }
    val sorted = processes.sortedBy { it.idx } // CSV 의 파일 순서대로 정렬
    return ProgramData(sorted, info)
}


fun executeFCFS(processes: List<Process>): ProgramData {
    val sortedProcesses: List<Process> = processes.sortedBy{ it.arrivalTime } // FCFS 라서 도착한 순서대로 정렬
    return runTaskFCFS(sortedProcesses)
}
