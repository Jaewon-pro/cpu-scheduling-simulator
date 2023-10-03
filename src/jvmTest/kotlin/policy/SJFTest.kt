package policy

import org.assertj.core.api.AssertionsForClassTypes
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import utils.CsvReaderUtils
import java.io.File

class SJFTest {

    private val testFilePath = "src/jvmTest/resources/sample.csv"


    @DisplayName("SJF 테스트")
    @Test
    fun test_SJF() {
        val file = File(testFilePath)
        val processes = CsvReaderUtils.parseTasksFromFile(file)

        val result = executeSJF(processes)

        val expectedTimestamp = arrayOf(0,1,2,3,4,6,9,10,12,15,16,18,20,23,27,28,31,34,37,41,45,50,55,60,67,75)

        AssertionsForClassTypes.assertThat(result.processes.size).isEqualTo(25)
        AssertionsForClassTypes.assertThat(result.info.size).isEqualTo(26)

        result.info.mapIndexed { i, info ->
            AssertionsForClassTypes.assertThat(info.timestamp).isEqualTo(expectedTimestamp[i])
        }
    }

    @DisplayName("SRTF 테스트")
    @Test
    fun test_SRTF() {
        val file = File(testFilePath)
        val processes = CsvReaderUtils.parseTasksFromFile(file)

        val result = executeSRTF(processes)

        val expectedTimestamp = arrayOf(0,1,2,3,4,6,7,8,10,12,13,14,16,18,20,23,24,25,27,30,33,37,41,45,50,55,60,67,75)

        AssertionsForClassTypes.assertThat(result.processes.size).isEqualTo(25)
        AssertionsForClassTypes.assertThat(result.info.size).isEqualTo(29)

        result.info.mapIndexed { i, info ->
            AssertionsForClassTypes.assertThat(info.timestamp).isEqualTo(expectedTimestamp[i])
        }
    }

}