package pro.liliya.runtime

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MemoryCycleTest {

    @Test
    fun `liliya should remember experience`() = runBlocking {

        val runtime = LiliyaRuntime()

        runtime.start()

        runtime.process(
            "Привет, меня зовут Виктор"
        ).collect { response ->
            println(response)
        }


        val episode = runtime.createEpisode()


        assertTrue(
            episode.events.isNotEmpty()
        )


        val memory =
            runtime.memoryHistory()


        assertEquals(
            1,
            memory.size
        )


        println(
            "Memory episode created: ${episode.id}"
        )
    }
}
