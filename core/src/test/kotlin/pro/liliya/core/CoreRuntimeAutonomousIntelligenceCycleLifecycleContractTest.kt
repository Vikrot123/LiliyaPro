package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

class CoreRuntimeAutonomousIntelligenceCycleLifecycleContractTest {

    @Test
    fun stopped_runtime_rejects_autonomous_intelligence_cycle_before_activation() {
        CoreRuntime.stop()

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state()
        )

        val auditBefore =
            CoreRuntime
                .getRuntimeActionAudit()
                .size

        val historyBefore =
            CoreRuntime
                .getRuntimeCommandHistory()
                .size

        val source =
            "v1.501-stopped"

        val authority =
            RuntimeActionAuthorityContext(
                source =
                    source,
                level =
                    RuntimeAuthorityLevel.SYSTEM
            )

        assertFailsWith<IllegalStateException> {
            CoreRuntime.processAutonomousIntelligenceCycle(
                source =
                    source,
                authority =
                    authority
            )
        }

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state(),
            "rejected autonomous activation must not change runtime lifecycle state"
        )

        assertEquals(
            auditBefore,
            CoreRuntime
                .getRuntimeActionAudit()
                .size,
            "rejected autonomous activation must not reach action dispatch"
        )

        assertEquals(
            historyBefore,
            CoreRuntime
                .getRuntimeCommandHistory()
                .size,
            "rejected autonomous activation must not create runtime command history"
        )
    }
}
