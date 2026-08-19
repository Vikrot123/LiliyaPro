package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.lifecycle.RuntimeLifecycleEvent

class RuntimeLifecycleRecorderPrepareIsolationContractTest {

    @Test
    fun lifecycle_records_are_cleared_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        composition.lifecycleRecorder()
            .record(
                RuntimeLifecycleEvent.STARTED,
                "test"
            )

        assertEquals(
            1,
            composition.lifecycleRecorder()
                .records()
                .size
        )

        composition.prepareRuntime()

        assertEquals(
            0,
            composition.lifecycleRecorder()
                .records()
                .size
        )

        assertNull(
            composition.lifecycleRecorder()
                .last()
        )
    }

    @Test
    fun lifecycle_recorder_instance_survives_but_state_is_reset() {
        val composition = DefaultRuntimeComposition()

        val recorderBefore =
            composition.lifecycleRecorder()

        composition.lifecycleRecorder()
            .record(
                RuntimeLifecycleEvent.STOPPED,
                "before reset"
            )

        composition.prepareRuntime()

        val recorderAfter =
            composition.lifecycleRecorder()

        assertEquals(
            recorderBefore,
            recorderAfter
        )

        assertEquals(
            0,
            recorderAfter.records().size
        )
    }
}
