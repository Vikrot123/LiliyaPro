package pro.liliya.runtime

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class LiliyaRuntimeIntegrationTest {

    @Test
    fun `runtime should process user input through cognitive cycle`() =
        runBlocking {

            val runtime = LiliyaRuntime()

            runtime.start()

            val responses = mutableListOf<String>()

            runtime.process("Привет, LiliyaPro")
                .collect {
                    responses.add(it)
                }

            runtime.stop()

            assertTrue(
                responses.isNotEmpty()
            )

            assertTrue(
                responses.any {
                    it.contains("Reflection")
                }
            )
        }
}
