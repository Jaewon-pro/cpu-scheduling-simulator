package policy

import utils.Info
import utils.ProgramData
import utils.Task
import java.util.*


//internal fun runTaskNonPreemptive(tasks: List<Task>, compare: (Task, Task) -> Int): ProgramData {
//    var currentRunTime = 0
//    val readyPool = PriorityQueue(compare)
//    var currentRunTask: Task? = null
//    var idx = 0
//    val info: MutableList<Info> = mutableListOf(Info(-1, tasks[0].arrivalTime, ranTime = 0))
//}

internal fun runTaskPreemptive(tasks: List<Task>, compare: (Task, Task) -> Int): ProgramData { // 선점형, priority, srtf 공용
    val info: MutableList<Info> = mutableListOf(Info(-1, tasks[0].arrivalTime, ranTime = 0))

    var currentRunTime = tasks[0].arrivalTime
    val readyPool = PriorityQueue(compare)
    var currentRunTask: Task? = null
    var idx = 0

    while (idx < tasks.size) {
        while (idx < tasks.size && currentRunTime == tasks[idx].arrivalTime) { // 동일한 시간에 들어오는 경우 처리를 위해 if 가 아닌 while

            if (currentRunTask === null) { currentRunTask = tasks[idx] }
            else if (compare(currentRunTask, tasks[idx]) <= 0) { // 기존 Task 의 우선순위가 더 크면, 변화 X, current 계속 실행
                tasks[idx].insertedTime = currentRunTime
                readyPool.add(tasks[idx])
            } else { // 새로 비교할 Task 의 우선순위가 더 크면
                val ranTime = currentRunTime - info.last().timestamp
                if (ranTime != 0)  info += Info(currentRunTask.pid, currentRunTime, ranTime) // ranTime 이 0인 경우 방지

                currentRunTask.insertedTime = currentRunTime
                tasks[idx].responseTime = currentRunTime // 새로 들어온 Task 의 첫 응답 시간 설정
                readyPool.add(currentRunTask)
                currentRunTask = tasks[idx]
            }
            ++idx
        }
        //println(currentRunTime)
        if (currentRunTask === null) { continue }
        if (currentRunTask.responseTime == -1) { currentRunTask.responseTime = currentRunTime }

        ++currentRunTime

        --currentRunTask.remainedTime
        if (currentRunTask.isFinished()) {
            print(currentRunTask.pid)
            println(": finished ## at $currentRunTime")
            info += Info(currentRunTask.pid, currentRunTime, currentRunTime - info.last().timestamp)

            if (readyPool.isEmpty()) { currentRunTask = null }
            else {
                val newRun = readyPool.remove()
                newRun.waitedTime += currentRunTime - newRun.insertedTime
                currentRunTask = newRun
                if (currentRunTask.responseTime == -1) { currentRunTask.responseTime = currentRunTime }
            }
        }
    }
    if (currentRunTask != null) {
        currentRunTime += currentRunTask.remainedTime
        info += Info(currentRunTask.pid, currentRunTime, currentRunTime - currentRunTask.responseTime)
        currentRunTask.remainedTime = 0
    }
    while (readyPool.isNotEmpty()) {
        val newRun = readyPool.remove()

        info += Info(newRun.pid, currentRunTime + newRun.remainedTime, newRun.remainedTime)
        newRun.waitedTime += currentRunTime - newRun.insertedTime
        if (newRun.responseTime == -1) { newRun.responseTime = currentRunTime }
        currentRunTime += newRun.remainedTime
        newRun.remainedTime = 0
        print(newRun.pid)
        println(": finished ! at $currentRunTime")
    }
    return ProgramData(tasks, info)
}



internal fun printResult(data: ProgramData): ProgramData {
    data.tasks.forEach{ println(it) }
    return data
}
