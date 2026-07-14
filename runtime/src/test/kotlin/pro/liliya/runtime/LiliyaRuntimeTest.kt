package pro.liliya.runtime

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class LiliyaRuntimeTest {

    @Test
    fun `liliya runtime should process user input`() = runBlocking {

        val runtime = LiliyaRuntime()

        runtime.start()

        val responses = mutableListOf<String>()

        runtime.controller()
            .processInput("Привет, LiliyaPro")
            .collect { response ->
                responses.add(response)
            }

        assertTrue(
            responses.isNotEmpty()
        )

        println("Runtime response:")

        responses.forEach {
            println(it)
        }

        runtime.stop()
    }
}
