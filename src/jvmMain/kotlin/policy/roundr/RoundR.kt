package policy.roundr

import utils.*

//PID,Arrival Time,Burst Time
//1,0,53
//2,1,17
//3,2,68
//4,3,24

//1,0,10
//2,3,20
//3,8,8
//4,16,16
//5,20,1

fun parse(string: String): Task {
    val (pid, arrival, execution) = string.split(',', ignoreCase = false).map { it.toInt() }
    return Task(pid, arrival, execution)
}

internal fun runTask(tasks : List<Task>, quantum: Int): ProgramData { // Round Robin
    val info: MutableList<Info> = mutableListOf(Info(-1, tasks[0].arrivalTime, 0)) // 처음에 0 표시

    var contextSwitched = 0 // 문맥교환 횟수
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

        print(performed.pid)
        println(": ran, at $currentRunTime")

        while (nextIdx < tasks.size && currentRunTime >= tasks[nextIdx].arrivalTime) {
            readyPool.add(tasks[nextIdx])
            ++nextIdx
        }

        if (performed.remainedTime == 0) { // Job finished
            performed.waitedTime = currentRunTime - performed.arrivalTime - performed.executionTime
            print(performed.pid)
            println(": finished, at $currentRunTime")
        } else {
            readyPool.add(performed)
        }
        ++contextSwitched
    }
    contextSwitched -= 2 // 처음이랑 마지막 교체 횟수 빼줌

    println("Total Executed time : $currentRunTime")
    println("Context Switched : $contextSwitched")

    return ProgramData(tasks, info, currentRunTime, contextSwitched)
}

fun execute(tasks: List<Task>, quantum: Int): ProgramData {
    return runTask(tasks, quantum).let(::printResult)
}

