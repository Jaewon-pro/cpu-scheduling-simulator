package policy

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import utils.CsvReaderUtils
import java.io.File

class RoundRobinTest {

    private val testFilePath = "src/jvmTest/resources/sample.csv"

    @DisplayName("타임 퀀텀 4일 때, RR 테스트")
    @Test
    fun testRoundRobin_4() {
        val file = File(testFilePath)
        val processes = CsvReaderUtils.parseTasksFromFile(file)
        val quantum = 4

        val result = executeRoundRobin(processes, quantum)

        val expectedTimestamp = arrayOf(0,1,2,3,4,6,9,12,14,15,16,18,21,25,27,31,35,38,42,43,46,50,54,58,62,63,66,69,70,71,75)

        assertThat(result.processes.size).isEqualTo(25)
        assertThat(result.info.size).isEqualTo(31)

        result.info.mapIndexed { i, info ->
            assertThat(info.timestamp).isEqualTo(expectedTimestamp[i])
        }

    }

}
