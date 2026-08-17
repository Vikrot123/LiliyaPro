package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeStartingStateOrderingContractTest {

    @Test
    fun runtimeStartingEventIsPublishedAfterStartingState() {
        RuntimeEventBus.clear()

        var stateAtStarting: CoreRuntimeState? = null

        RuntimeEventBus.subscribe { event ->
            if (event == RuntimeEvent.RuntimeStarting) {
                stateAtStarting = CoreRuntime.state()
            }
        }

        try {
            CoreRuntime.start()

            assertEquals(
                CoreRuntimeState.STARTING,
                stateAtStarting
            )
        } finally {
            CoreRuntime.stop()
            RuntimeEventBus.clear()
        }
    }
}
