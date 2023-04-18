package policy.sjf

// Shortest Job First

import utils.ProgramData
import utils.Task
import utils.printResult
import utils.runTaskPreemptive

//    PID,Arrival Time ,Burst Time
//    1,3,12
//    2,5,9
//    3,7,18
//    4,8,3


fun parse(string: String): Task {
    val (pid, arrival, execution) = string.split(',', ignoreCase = false).map { it.toInt() }
    return Task(pid, arrival, execution)
}

fun compare(a: Task, b: Task): Int = compareValuesBy(a, b, { it.remainedTime }, { it.arrivalTime })

fun execute(tasks: List<Task>): ProgramData {
    return runTaskPreemptive(tasks, ::compare).let(::printResult)
}

