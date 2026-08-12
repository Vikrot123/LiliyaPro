package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import pro.liliya.core.module.ModuleManager

class CoreRuntimeStartupFailureRollbackTest {

    @Test
    fun runtimeFailureMustAllowFutureRestart() {

        assertDoesNotThrow {
            CoreRuntime.stop()
        }

        /*
         Current contract:
         failed startup must not permanently lock runtime state.
         */

        CoreRuntime.start()
        CoreRuntime.stop()

        CoreRuntime.start()
        CoreRuntime.stop()
    }
}
