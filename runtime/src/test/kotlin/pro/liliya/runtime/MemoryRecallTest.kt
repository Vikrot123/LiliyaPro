package pro.liliya.runtime

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue


class MemoryRecallTest {

    @Test
    fun `liliya should recall previous interaction`() = runBlocking {

        val runtime = LiliyaRuntime()

        runtime.start()


        runtime.process(
            "Привет, меня зовут Виктор"
        ).collect {
            println(it)
        }


        val memories =
            runtime.searchMemory(
                "Виктор"
            )


        assertTrue(
            memories.isNotEmpty()
        )


        println(
            "Memory recalled: ${memories.size}"
        )
    }
}
