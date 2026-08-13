package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull

class CoreRuntimeMonitorContractTest {

    @Test
    fun core_runtime_exposes_monitor_snapshot() {

        val result = CoreRuntime.monitor()

        assertNotNull(result)
        assertNotNull(result.diagnostics)
    }
}
