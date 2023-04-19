package policy

import utils.ProgramData
import utils.Task
import utils.printResult
import utils.runTaskPreemptive

private fun compareSRTF(a: Task, b: Task): Int = compareValuesBy(a, b, { it.remainedTime }, { it.arrivalTime })

fun executeSRTF(tasks: List<Task>): ProgramData {
    return runTaskPreemptive(tasks, ::compareSRTF).let(::printResult)
}
