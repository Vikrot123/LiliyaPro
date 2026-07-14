package pro.liliya.runtime

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class CognitivePipelineTest {

    @Test
    fun cognitivePipeline_shouldProcessInput() = runBlocking {

        val runtime = LiliyaRuntime()

        runtime.start()

        val results = mutableListOf<String>()

        runtime.process(
            "Hello Liliya"
        ).collect { output ->
            results.add(output)
        }

        runtime.stop()

        assertTrue(
            results.isNotEmpty()
        )

        assertTrue(
            results.any {
                it.contains("Reflection")
            }
        )
    }
}
