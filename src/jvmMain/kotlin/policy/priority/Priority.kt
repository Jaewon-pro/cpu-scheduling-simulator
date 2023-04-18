package policy.priority

import utils.ProgramData
import utils.Task
import utils.printResult
import utils.runTaskPreemptive

//PID,Arrival Time,Burst Time,Priority
//1,0,5,1
//2,5,6,2
//3,10,5,1
//4,12,6,3
//5,13,3,1

fun parse(string: String): Task {
    if (string.count { it == ',' } <= 2 ) {
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
