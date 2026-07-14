package pro.liliya.runtime

import kotlinx.coroutines.runBlocking
import pro.liliya.domain.models.RuntimeState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LiliyaRuntimeStateTest {

    @Test
    fun `runtime should start and change state`() = runBlocking {

        val runtime = LiliyaRuntime()

        assertEquals(
            RuntimeState.BOOTING,
            runtime.stateMachine().state()
        )

        runtime.start()

        assertEquals(
            RuntimeState.READY,
            runtime.stateMachine().state()
        )

        val responses = mutableListOf<String>()

        runtime.process("Привет, LiliyaPro")
            .collect {
                responses.add(it)
            }

        assertTrue(
            responses.isNotEmpty()
        )

        runtime.stop()

        assertEquals(
            RuntimeState.STOPPED,
            runtime.stateMachine().state()
        )
    }
}
