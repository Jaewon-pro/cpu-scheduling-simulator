package policy

import utils.Process
import utils.ProgramData

private fun compareSJF(a: Process, b: Process): Int = compareValuesBy(a, b, { it.remainedTime }, { it.arrivalTime })

fun executeSJF(processes: List<Process>): ProgramData {
    return calculateTasks(processes, ::compareSJF, isPreemptive = false)
}

fun executeSRTF(processes: List<Process>): ProgramData {
    return calculateTasks(processes, ::compareSJF, isPreemptive = true)//.let(::printResult)
}
