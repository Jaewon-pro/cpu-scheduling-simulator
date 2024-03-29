package policy

import model.Process
import model.ProgramData

// The top priority, designated as '1', represents the utmost urgency.
private fun comparePriority(a: Process, b: Process): Int = compareValuesBy(a, b, { it.priority }, { it.arrivalTime })

fun executePriorityNonPreemptive(processes: List<Process>): ProgramData {
    return calculateTasks(processes, ::comparePriority, isPreemptive = false)
}

fun executePriority(processes: List<Process>): ProgramData {
    return calculateTasks(processes, ::comparePriority, isPreemptive = true)
}
