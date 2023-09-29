package policy

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import utils.CsvReaderUtils
import java.io.File

class FCFSTest {

    private val testFilePath = "src/jvmTest/resources/sample.csv"

    @DisplayName("FCFS 테스트")
    @Test
    fun testFCFS() {
        val file = File(testFilePath)
        val processes = CsvReaderUtils.parseTasksFromFile(file)

        val result = executeFCFS(processes)

        assertThat(result.processes.size).isEqualTo(25)
        assertThat(result.info.size).isEqualTo(26)

        result.processes.zipWithNext { current, next ->
            assertThat(current.remainedTime).isZero()
            assertThat(current.arrivalTime).isLessThanOrEqualTo(next.arrivalTime)
        }

        result.info.zipWithNext { current, next ->
            assertThat(current.timestamp).isLessThanOrEqualTo(next.timestamp)
        }

        assertThat(result.info.first().timestamp).isEqualTo(0)
        assertThat(result.info[1].timestamp).isEqualTo(1)
        assertThat(result.info.last().timestamp).isEqualTo(75)
    }

}
