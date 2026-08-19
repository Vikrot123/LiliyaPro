package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.RuntimeService
import pro.liliya.core.runtime.RuntimeServiceState
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeRecoveryActionTargetContractTest {

    @Test
    fun `recover action targets specific runtime service`() {

        val service = object : RuntimeService {

            override val name: String = "test-service"

            override var state: RuntimeServiceState =
                RuntimeServiceState.STOPPED

            override fun start() {
                state = RuntimeServiceState.RUNNING
            }

            override fun stop() {
                state = RuntimeServiceState.STOPPED
            }
        }

        CoreRuntime.registerRuntimeService(service)

        CoreRuntime.start()

        try {

            val result = CoreRuntime.dispatchRuntimeAction(
                RuntimeActionRequest(
                    command = RuntimeCommand.RECOVER,
                    target = "test-service",
                    source = "recovery-target-contract-test",
                    authority = RuntimeActionAuthorityContext(
                        source = "recovery-target-contract-test",
                        level = RuntimeAuthorityLevel.SYSTEM
                    )
                )
            )

            if (!result.success) {
                error("RECOVER FAILED: $result")
            }

            val snapshot = CoreRuntime.getRuntimeRecoverySnapshot()

            assertTrue(snapshot.recovered)
            assertTrue(snapshot.recoveredAt != null)

        } finally {
            CoreRuntime.stop()
        }
    }
}
