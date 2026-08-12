package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoreRuntimeFailureEventOrderingContractTest {

    @Test
    fun moduleFailureMustArriveBeforeRuntimeFailure() {

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

        val moduleFailedIndex =
            received.indexOfFirst {
                it is RuntimeEvent.ModuleFailed
            }

        val runtimeFailedIndex =
            received.indexOfFirst {
                it is RuntimeEvent.RuntimeFailed
            }

        assertTrue(
            moduleFailedIndex >= 0,
            "ModuleFailed event must exist"
        )

        assertTrue(
            runtimeFailedIndex >= 0,
            "RuntimeFailed event must exist"
        )

        assertTrue(
            moduleFailedIndex < runtimeFailedIndex,
            "ModuleFailed must happen before RuntimeFailed"
        )

        CoreRuntime.resetModuleProvider()
        RuntimeEventBus.clear()
    }
}
