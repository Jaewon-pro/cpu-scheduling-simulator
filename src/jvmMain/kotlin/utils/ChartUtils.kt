package utils

import model.ChartInfo
import model.Process

class ChartUtils {
    companion object {

        fun makeChartInfo(process: Process, now: Int): ChartInfo {
            return ChartInfo(process.pid, now, process.executionTime)
        }

        fun makeInitialList(startTime: Int): MutableList<ChartInfo> {
            val start = makeFirstChart(startTime)
            return mutableListOf(start)
        }

        private fun makeFirstChart(startTime: Int): ChartInfo = ChartInfo(-1, startTime, 0)


    }
}
