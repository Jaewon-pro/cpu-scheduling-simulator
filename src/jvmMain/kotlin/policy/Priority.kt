package policy

import utils.ProgramData
import utils.Task

// The top priority, designated as '0', represents the utmost urgency.
private fun comparePriority(a: Task, b: Task): Int = compareValuesBy(a, b, { it.priority }, { it.arrivalTime })

fun executePriority(tasks: List<Task>): ProgramData {
    return runTaskPreemptive(tasks, ::comparePriority)//.let(::printResult)
}
