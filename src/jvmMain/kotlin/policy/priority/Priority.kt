package policy.priority

import utils.ProgramData
import utils.Task
import utils.printResult
import utils.runTaskPreemptive

//    PID,Arrival Time,Burst Time,Priority
//    1,3,12,3
//    2,5,12,2
//    3,7,12,1
//    4,10,12,1

fun parse(string: String): Task {
    if (string.filter { it == ',' }.count() <= 3 ) {
        val (pid, arrival, execution) = string.split(',', ignoreCase = false).map { it.toInt() }
        return Task(pid, arrival, execution, 1, -1)
    }
    val (pid, arrival, execution, priority) = string.split(',', ignoreCase = false).map { it.toInt() }
    return Task(pid, arrival, execution, priority, -1)
}

internal fun compare(a: Task, b: Task): Int = compareValuesBy(a, b, { -it.priority }, { it.arrivalTime })

fun execute(tasks: List<Task>): ProgramData {
    return runTaskPreemptive(tasks, ::compare).let(::printResult)
}
