package policy

import utils.Task

internal fun compareSJF(a: Task, b: Task): Int = compareValuesBy(a, b, { it.remainedTime }, { it.arrivalTime })



//fun executeSJF(tasks: List<Task>): ProgramData {
//    return runTaskNonPreemptive(tasks, ::compareSJF).let(::printResult)
//}
