package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull

class CoreRuntimeLifecycleMonitorContractTest {

    @Test
    fun core_runtime_monitor_exposes_lifecycle_snapshot() {

        val result = CoreRuntime.monitor()

        assertNotNull(result)
        assertNotNull(result.lifecycleHistory)
    }
}
