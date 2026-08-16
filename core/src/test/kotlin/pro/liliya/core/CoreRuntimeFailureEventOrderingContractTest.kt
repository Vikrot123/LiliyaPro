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

        val runtimeStartingIndex =
            received.indexOfFirst {
                it is RuntimeEvent.RuntimeStarting
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
            runtimeStartingIndex >= 0,
            "RuntimeStarting event must exist"
        )

        assertTrue(
            runtimeStartingIndex < moduleFailedIndex,
            "RuntimeStarting must happen before ModuleFailed"
        )

        assertTrue(
            moduleFailedIndex < runtimeFailedIndex,
            "ModuleFailed must happen before RuntimeFailed"
        )

        assertTrue(
            received.none {
                it is RuntimeEvent.RuntimeReady
            },
            "Failed startup must never publish RuntimeReady"
        )

        CoreRuntime.resetModuleProvider()
        RuntimeEventBus.clear()
    }
}
