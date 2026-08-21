package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleEvent

class RuntimeCompositionShutdownOrderingContractTest {

    @Test
    fun runtime_shutdown_records_stopped_before_external_events_are_ignored() {
        RuntimeEventBus.clear()

        val composition = DefaultRuntimeComposition()

        composition.prepareRuntime()
        composition.startRuntime()

        composition.stopRuntime()

        val records = composition.lifecycleRecorder()
            .records()

        assertEquals(
            RuntimeLifecycleEvent.STOPPED,
            records.last().event
        )

        val before = records.size

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )

        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeServiceFailed(
                serviceName = "after-stop",
                reason = "shutdown-barrier"
            )
        )

        val after = composition.lifecycleRecorder()
            .records()

        assertEquals(
            before,
            after.size
        )

        RuntimeEventBus.clear()
    }
}
