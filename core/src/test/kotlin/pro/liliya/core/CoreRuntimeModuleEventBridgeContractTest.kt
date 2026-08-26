package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.AfterEach

class CoreRuntimeModuleEventBridgeContractTest {

    @AfterEach
    fun cleanupCoreRuntimeAfterTest() {
        CoreRuntime.resetModuleProvider()
        CoreRuntime.stop()
        RuntimeEventBus.clear()
    }

    @Test
    fun moduleFailedEventMustBeBridgedOnlyOnce() {

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

        val beforeManualPublish =
            received.count {
                it is RuntimeEvent.ModuleFailed
            }

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "TEST_MODULE",
                phase = "TEST_PHASE",
                reason = "TEST_REASON"
            )
        )

        val afterManualPublish =
            received.count {
                it is RuntimeEvent.ModuleFailed
            }

        assertEquals(
            beforeManualPublish + 1,
            afterManualPublish
        )

        CoreRuntime.stop()

        ModuleEventBus.publish(
            ModuleEvent.Failed(
                moduleName = "SECOND_TEST_MODULE",
                phase = "TEST_PHASE",
                reason = "SECOND_REASON"
            )
        )

        val finalCount =
            received.count {
                it is RuntimeEvent.ModuleFailed
            }

        assertEquals(
            afterManualPublish,
            finalCount
        )

        assertTrue(
            received.any {
                it is RuntimeEvent.ModuleFailed &&
                it.moduleName == "TEST_MODULE"
            }
        )

        CoreRuntime.resetModuleProvider()
        RuntimeEventBus.clear()
    }
}
