package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CoreRuntimeStateTransitionContractTest {

    @Test
    fun runtimeStartsOnlyThroughValidLifecycle() {

        CoreRuntime.stop()

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state()
        )

        CoreRuntime.start()

        assertEquals(
            CoreRuntimeState.RUNNING,
            CoreRuntime.state()
        )

        CoreRuntime.stop()

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state()
        )
    }

    @Test
    fun runtimeFailureCanRecoverOnlyThroughNewStart() {

        CoreRuntime.stop()

        CoreRuntime.setModuleProvider(
            FailingModuleProvider()
        )

        try {
            CoreRuntime.start()
        } catch (_: RuntimeException) {
            // expected
        }

        assertEquals(
            CoreRuntimeState.FAILED,
            CoreRuntime.state()
        )

        CoreRuntime.resetModuleProvider()

        CoreRuntime.start()

        assertEquals(
            CoreRuntimeState.RUNNING,
            CoreRuntime.state()
        )

        CoreRuntime.stop()
    }

    @Test
    fun secondStartDoesNotChangeRunningState() {

        CoreRuntime.stop()

        CoreRuntime.start()

        assertEquals(
            CoreRuntimeState.RUNNING,
            CoreRuntime.state()
        )

        CoreRuntime.start()

        assertEquals(
            CoreRuntimeState.RUNNING,
            CoreRuntime.state()
        )

        CoreRuntime.stop()
    }
}
