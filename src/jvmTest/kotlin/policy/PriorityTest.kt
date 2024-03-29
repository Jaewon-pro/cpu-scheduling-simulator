package policy

import org.assertj.core.api.AssertionsForClassTypes
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import utils.CsvReaderUtils
import java.io.File

class PriorityTest {

    private val testFilePath = "src/jvmTest/resources/sample.csv"


    @DisplayName("Priority non-pre-emptive 테스트")
    @Test
    fun testPriority_non_preemptive() {
        val file = File(testFilePath)
        val processes = CsvReaderUtils.parseTasksFromFile(file)

        val result = executePriorityNonPreemptive(processes)

        val expectedTimestamp = arrayOf(0,1,2,3,4,6,9,11,12,15,16,19,21,25,32,35,39,42,47,48,52,57,60,62,67,75)

        AssertionsForClassTypes.assertThat(result.processes.size).isEqualTo(25)
        AssertionsForClassTypes.assertThat(result.info.size).isEqualTo(26)

        result.info.mapIndexed { i, info ->
            AssertionsForClassTypes.assertThat(info.timestamp).isEqualTo(expectedTimestamp[i])
        }
    }

    @DisplayName("Priority pre-emptive 테스트")
    @Test
    fun testPriority_preemptive() {
        val file = File(testFilePath)
        val processes = CsvReaderUtils.parseTasksFromFile(file)

        val result = executePriority(processes)

        val expectedTimestamp = arrayOf(0,1,2,3,5,6,7,9,10,13,14,16,19,21,28,31,35,38,40,45,46,50,55,58,60,62,67,75)

        AssertionsForClassTypes.assertThat(result.processes.size).isEqualTo(25)
        AssertionsForClassTypes.assertThat(result.info.size).isEqualTo(28)

        result.info.mapIndexed { i, info ->
            AssertionsForClassTypes.assertThat(info.timestamp).isEqualTo(expectedTimestamp[i])
        }
    }


}
