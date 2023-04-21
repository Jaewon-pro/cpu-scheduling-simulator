package policy

import utils.ProgramData
import utils.Task

fun executeSRTF(tasks: List<Task>): ProgramData {
    return runTaskPreemptive(tasks, ::compareSJF)//.let(::printResult)
}
