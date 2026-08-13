package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CoreRuntimeRecoveryContractTest {

    @Test
    fun `runtime recovery becomes true after runtime ready`() {

        CoreRuntime.start()

        try {
            val snapshot =
                CoreRuntime.getRuntimeRecoverySnapshot()

            assertTrue(snapshot.recovered)
            assertTrue(snapshot.recoveredAt != null)

        } finally {
            CoreRuntime.stop()
        }
    }
}
