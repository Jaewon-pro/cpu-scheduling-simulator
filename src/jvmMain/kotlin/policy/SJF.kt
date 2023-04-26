package policy

import utils.ProgramData
import utils.Task

private fun compareSJF(a: Task, b: Task): Int = compareValuesBy(a, b, { it.remainedTime }, { it.arrivalTime })

fun executeSJF(tasks: List<Task>): ProgramData {
    return calculateTasks(tasks, ::compareSJF, isNonPreemptive = true)
}

fun executeSRTF(tasks: List<Task>): ProgramData {
    return calculateTasks(tasks, ::compareSJF, isNonPreemptive = false)//.let(::printResult)
}
