//package policy
//
//import utils.ProgramData
//import utils.Task
//
//private fun score(task: Task) = 1.0f / task.executionTime + task.priority - (0.5 * task.)
//internal fun compareIO(a: Task, b: Task): Int = compareValuesBy(a, b,
//    { it.remainedTime
//        // score = (1 / burst_time) + priority - (0.5 * io_operations)
//    }, { it.arrivalTime })
//
//fun executeIO(tasks: List<Task>): ProgramData {
//    return runTaskPreemptive(tasks, ::compareIO)//.let(::printResult)
//}
