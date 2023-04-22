//package policy
//
//import utils.ProgramData
//import utils.Task
//
//
//private fun score(task: Task) = task.remainedTime +
//internal fun comparePRR(a: Task, b: Task): Int = compareValuesBy(a, b,
//    { it.remainedTime
//    }, { it.arrivalTime })
//
//fun executeIO(tasks: List<Task>): ProgramData {
//    return runTaskPreemptive(tasks, ::comparePRR)//.let(::printResult)
//}
