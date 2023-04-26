package policy

import utils.ProgramData
import utils.Task

// The top priority, designated as '1', represents the utmost urgency.
private fun comparePriority(a: Task, b: Task): Int = compareValuesBy(a, b, { it.priority }, { it.arrivalTime })

fun executePriorityNonPreemptive(tasks: List<Task>): ProgramData {
    return calculateTasks(tasks, ::comparePriority, isPreemptive = false)//.let(::printResult)
}

fun executePriority(tasks: List<Task>): ProgramData {
    return calculateTasks(tasks, ::comparePriority, isPreemptive = true)//.let(::printResult)
}
