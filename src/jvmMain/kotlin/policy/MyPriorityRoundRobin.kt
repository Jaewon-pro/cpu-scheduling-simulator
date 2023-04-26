package policy

//
////private fun score(task: Task) = task.remainedTime +
//private fun runTaskRoundRobin(tasks : List<Task>, quantum: Int): ProgramData {
//    val info: MutableList<ChartInfo> = mutableListOf(ChartInfo(-1, tasks[0].arrivalTime, 0)) // 처음에 0 표시
//
//    var currentRunTime = tasks[0].arrivalTime
//    val readyPool = CircularQueue<Task>(tasks.size + 1)
//    readyPool.add(tasks[0])
//    tasks[0].responseTime = 0
//    var nextIdx = 1
//
//    while (readyPool.isNotEmpty()) {
//        val performed: Task = readyPool.poll()
//        val performedTime = if (performed.remainedTime >= quantum) quantum else performed.remainedTime % quantum
//        if (performed.responseTime == -1) performed.responseTime = currentRunTime
//
//        currentRunTime += performedTime
//        performed.remainedTime -= performedTime
//
//        info += ChartInfo(performed.pid, currentRunTime, performedTime) // ran at $currentRunTime
//
//        while (nextIdx < tasks.size && currentRunTime >= tasks[nextIdx].arrivalTime) {
//            readyPool.add(tasks[nextIdx])
//            ++nextIdx
//        }
//
//        if (performed.remainedTime == 0) { // Job finished
//            performed.waitedTime = currentRunTime - performed.arrivalTime - performed.executionTime
//            //println("${performed.pid}: finished,, at $currentRunTime")
//        } else {
//            readyPool.add(performed)
//        }
//    }
////    println("Total Executed time : $currentRunTime")
////    println("Context Switched : ${info.size - 2}")
//    return ProgramData(tasks, info)
//}
//fun executePRR(tasks: List<Task>): ProgramData {
//    return calculateTasks(tasks, ::comparePRR, isPreemptive = false)//.let(::printResult)
//}
