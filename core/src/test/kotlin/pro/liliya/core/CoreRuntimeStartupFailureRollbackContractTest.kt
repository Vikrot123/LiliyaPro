package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class CoreRuntimeStartupFailureRollbackContractTest {

    @Test
    fun failedStartupMustRollbackRuntimeState() {

        CoreRuntime.stop()

        CoreRuntime.setModuleProvider(
            FailingModuleProvider()
        )

        assertThrows(RuntimeException::class.java) {
            CoreRuntime.start()
        }

        CoreRuntime.resetModuleProvider()

        CoreRuntime.start()
        CoreRuntime.stop()
    }
}
