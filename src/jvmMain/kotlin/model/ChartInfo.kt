package model

data class ChartInfo ( // 시간순으로 실행한 Task 들 단위 시간 저장, 나중에 간트차트 출력을 위해서
    val pid: Int,
    val timestamp: Int, // Since when
    val ranTime: Int, // How long executed
)
