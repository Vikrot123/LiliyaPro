package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach

class CoreRuntimeModuleFailurePropagationContractTest {

    @AfterEach
    fun cleanupCoreRuntimeAfterTest() {
        CoreRuntime.resetModuleProvider()
        CoreRuntime.stop()
        RuntimeEventBus.clear()
    }

    @Test
    fun moduleFailureMustBePropagatedToRuntimeEventBus() {

        val received = mutableListOf<RuntimeEvent>()

        RuntimeEventBus.clear()

        RuntimeEventBus.subscribe {
            received.add(it)
        }

        CoreRuntime.stop()

        CoreRuntime.setModuleProvider(
            FailingModuleProvider()
        )

        try {
            CoreRuntime.start()
        } catch (_: RuntimeException) {
        }

        assertTrue(
            received.any {
                it is RuntimeEvent.RuntimeFailed
            }
        )

        val failure =
            received.filterIsInstance<RuntimeEvent.RuntimeFailed>()
                .first()

        assertEquals(
            "Critical module failed: CRITICAL_STARTUP_FAILING_MODULE",
            failure.reason
        )

        CoreRuntime.resetModuleProvider()
        RuntimeEventBus.clear()
    }
}
