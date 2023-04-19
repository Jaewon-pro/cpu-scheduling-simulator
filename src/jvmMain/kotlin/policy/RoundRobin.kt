package policy

import utils.*

private fun runTask(tasks : List<Task>, quantum: Int): ProgramData { // Round Robin
    val info: MutableList<Info> = mutableListOf(Info(-1, tasks[0].arrivalTime, 0)) // 처음에 0 표시

    var currentRunTime = tasks[0].arrivalTime
    val readyPool = CircularQueue<Task>(tasks.size + 1)
    readyPool.add(tasks[0])
    var nextIdx = 1

    while (readyPool.isNotEmpty()) {
        val performed = readyPool.poll()
        val performedTime = if (performed.remainedTime >= quantum) quantum else performed.remainedTime % quantum
        currentRunTime += performedTime
        performed.remainedTime -= performedTime

        info += Info(performed.pid, currentRunTime, performedTime)

        println("${performed.pid}: ran, at $currentRunTime")

        while (nextIdx < tasks.size && currentRunTime >= tasks[nextIdx].arrivalTime) {
            readyPool.add(tasks[nextIdx])
            ++nextIdx
        }

        if (performed.remainedTime == 0) { // Job finished
            performed.waitedTime = currentRunTime - performed.arrivalTime - performed.executionTime
            println("${performed.pid}: finished, at $currentRunTime")
        } else {
            readyPool.add(performed)
        }
    }

    println("Total Executed time : $currentRunTime")
    println("Context Switched : ${info.size - 2}")

    return ProgramData(tasks, info)
}

fun executeRoundRobin(tasks: List<Task>, quantum: Int): ProgramData {
    return runTask(tasks, quantum).let(::printResult)
}
