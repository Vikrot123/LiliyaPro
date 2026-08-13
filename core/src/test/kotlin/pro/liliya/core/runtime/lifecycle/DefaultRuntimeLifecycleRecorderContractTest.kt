package pro.liliya.core.runtime.lifecycle

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DefaultRuntimeLifecycleRecorderContractTest {

    @Test
    fun recorder_stores_runtime_events() {

        val recorder = DefaultRuntimeLifecycleRecorder()

        recorder.record(
            RuntimeLifecycleEvent.STARTED
        )

        recorder.record(
            RuntimeLifecycleEvent.FAILED,
            "service failed"
        )

        val records = recorder.records()

        assertEquals(
            2,
            records.size
        )

        assertEquals(
            RuntimeLifecycleEvent.FAILED,
            recorder.last()?.event
        )

        assertEquals(
            "service failed",
            recorder.last()?.reason
        )

        assertNotNull(
            recorder.last()
        )
    }
}
