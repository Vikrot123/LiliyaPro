package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeReadyStateOrderingContractTest {

    @Test
    fun runtimeReadyEventIsPublishedAfterRunningState() {

        RuntimeEventBus.clear()

        var stateAtReady: CoreRuntimeState? = null

        RuntimeEventBus.subscribe { event ->
            if (event == RuntimeEvent.RuntimeReady) {
                stateAtReady = CoreRuntime.state()
            }
        }

        try {
            CoreRuntime.start()

            assertEquals(
                CoreRuntimeState.RUNNING,
                stateAtReady
            )
        } finally {
            CoreRuntime.stop()
            RuntimeEventBus.clear()
        }
    }
}
