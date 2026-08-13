package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoreRuntimeFailureHealthContractTest {

    @Test
    fun `runtime failure health captures module failure`() {

        CoreRuntime.start()

        try {
            ModuleEventBus.publish(
                ModuleEvent.Failed(
                    moduleName = "TestModule",
                    phase = "INIT",
                    reason = "Initialization failed"
                )
            )

            val snapshot =
                CoreRuntime.getRuntimeFailureHealthSnapshot()

            assertTrue(snapshot.failed)

            assertEquals(
                "TestModule",
                snapshot.failedModule
            )

            assertTrue(
                snapshot.failureReason!!.contains("INIT")
            )

        } finally {
            CoreRuntime.stop()
        }
    }
}
