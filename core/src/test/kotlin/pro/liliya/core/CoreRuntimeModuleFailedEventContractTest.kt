package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoreRuntimeModuleFailedEventContractTest {

    @Test
    fun failedModuleMustPublishRuntimeModuleFailedEvent() {

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

        val failure =
            received.filterIsInstance<RuntimeEvent.ModuleFailed>()
                .first()

        assertTrue(
            failure.moduleName ==
                "CRITICAL_STARTUP_FAILING_MODULE"
        )

        assertTrue(
            failure.reason.contains("CRITICAL_STARTUP_FAILING_MODULE")
        )

        CoreRuntime.resetModuleProvider()
        RuntimeEventBus.clear()
    }
}
