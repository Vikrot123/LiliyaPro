package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

class CoreRuntimeAutonomousIntelligenceCycleActivationContractTest {

    @Test
    fun core_runtime_exposes_autonomous_intelligence_cycle_activation() {
        val source =
            "v1.500-core-runtime"

        val authority =
            RuntimeActionAuthorityContext(
                source =
                    source,
                level =
                    RuntimeAuthorityLevel.SYSTEM
            )

        val result =
            CoreRuntime.processAutonomousIntelligenceCycle(
                source =
                    source,
                authority =
                    authority
            )

        assertNotNull(
            result
        )

        assertSame(
            result.intelligence,
            result.executionCycle.execution.intelligence
        )

        val request =
            result
                .executionCycle
                .execution
                .execution
                .request

        if (request != null) {
            assertEquals(
                source,
                request.source
            )

            assertSame(
                authority,
                request.authority
            )
        }
    }
}
