package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeStopStateOrderingContractTest {

    @Test
    fun systemStopEventIsPublishedAfterStoppedState() {
        RuntimeEventBus.clear()

        var stateAtStop: CoreRuntimeState? = null

        RuntimeEventBus.subscribe { event ->
            if (event == RuntimeEvent.SystemStop) {
                stateAtStop = CoreRuntime.state()
            }
        }

        try {
            CoreRuntime.start()
            CoreRuntime.stop()

            assertEquals(
                CoreRuntimeState.STOPPED,
                stateAtStop
            )
        } finally {
            CoreRuntime.stop()
            RuntimeEventBus.clear()
        }
    }
}
