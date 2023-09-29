package model


data class Process(
    val pid: Int, // PID 를 기준으로 Task 단위 저장
    val arrivalTime: Int,
    val executionTime: Int,
    val priority: Int,
    val idx: Int, // csv 파일에서의 순서
) {
    var remainedTime: Int = executionTime
    var waitedTime: Int = 0 // 이건 1틱마다가 아닌 task 사이클 마다 계산
    var responseTime: Int = -1 // 처음으로 응답한 시간

    fun completedTime() = arrivalTime + executionTime + waitedTime
    fun turnaroundTime() = executionTime + waitedTime
    fun isFinished() = remainedTime == 0

    fun reset() {
        remainedTime = executionTime
        waitedTime = 0
        responseTime = -1
    }

    fun saveResponseTime(time: Int) {
        if (responseTime == -1) {
            responseTime = time
        }
    }

    override fun toString(): String {
        var str = "Task PID: $pid, Arrival time: $arrivalTime, Burst time: $executionTime"
        if (priority >= 0) str += ", Priority: $priority"
        if (waitedTime != -1) str += ", Waited time: $waitedTime"
        return str
    }
}
