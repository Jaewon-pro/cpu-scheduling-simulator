package policy

import utils.ChartInfo
import utils.ProgramData
import utils.Task
import java.util.*

internal fun calculateTasks(tasks: List<Task>, compare: (Task, Task) -> Int, isNonPreemptive: Boolean): ProgramData {
    val info: MutableList<ChartInfo> = mutableListOf(ChartInfo(-1, tasks[0].arrivalTime, ranTime = 0))

    var currentRunTime = tasks[0].arrivalTime
    val readyPool = PriorityQueue(compare)
    var currentRunTask: Task? = null
    var idx = 0

    while (idx < tasks.size) {
        while (idx < tasks.size && currentRunTime == tasks[idx].arrivalTime) { // 동일한 시간에 들어오는 경우 처리를 위해 if 가 아닌 while

            if (currentRunTask === null) { currentRunTask = tasks[idx] }
            else if (compare(currentRunTask, tasks[idx]) <= 0 || isNonPreemptive) { // 기존 Task 의 우선순위가 더 크면, 변화 X, current 계속 실행
                readyPool.add(tasks[idx])
            } else { // If the priority of the new task is more urgent. 새로 비교할 Task 의 우선순위가 더 크면
                val ranTime = currentRunTime - info.last().timestamp
                if (ranTime != 0)  info += ChartInfo(currentRunTask.pid, currentRunTime, ranTime) // To ignore if ranTime is zero

                tasks[idx].responseTime = currentRunTime // 새로 들어온 Task 의 첫 응답 시간 설정
                readyPool.add(currentRunTask)
                currentRunTask = tasks[idx]
            }
            ++idx
        }
        if (currentRunTask === null) { continue }
        if (currentRunTask.responseTime == -1) { currentRunTask.responseTime = currentRunTime }

        ++currentRunTime
        --currentRunTask.remainedTime
        if (currentRunTask.isFinished()) {
            //println("${currentRunTask.pid}: finished ## at $currentRunTime")
            currentRunTask.waitedTime = currentRunTime - currentRunTask.arrivalTime - currentRunTask.executionTime
            info += ChartInfo(currentRunTask.pid, currentRunTime, currentRunTime - info.last().timestamp)

            if (readyPool.isEmpty()) { currentRunTask = null }
            else {
                val newRun = readyPool.remove()
                currentRunTask = newRun
                if (currentRunTask.responseTime == -1) { currentRunTask.responseTime = currentRunTime }
            }
        }
    }
    if (currentRunTask != null) {
        currentRunTime += currentRunTask.remainedTime
        info += ChartInfo(currentRunTask.pid, currentRunTime, currentRunTime - currentRunTask.responseTime)
        currentRunTask.remainedTime = 0
    }
    while (readyPool.isNotEmpty()) {
        val newRun = readyPool.remove()

        info += ChartInfo(newRun.pid, currentRunTime + newRun.remainedTime, newRun.remainedTime)
        if (newRun.responseTime == -1) { newRun.responseTime = currentRunTime }
        currentRunTime += newRun.remainedTime
        newRun.remainedTime = 0
        newRun.waitedTime = currentRunTime - newRun.arrivalTime - newRun.executionTime
        //println("${newRun.pid}: finished ! at $currentRunTime")
    }
    return ProgramData(tasks, info)
}

//internal fun printResult(data: ProgramData): ProgramData {
//    data.tasks.forEach{ println(it) }
//    return data
//}
