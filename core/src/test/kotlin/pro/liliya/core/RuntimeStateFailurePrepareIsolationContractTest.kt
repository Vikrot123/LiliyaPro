package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import pro.liliya.core.module.ModuleState
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeStateFailurePrepareIsolationContractTest {

    @Test
    fun runtime_failure_and_module_state_do_not_leak_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        composition.runtimeStateHolder()
            .setFailureReason("failure-before-reset")

        composition.runtimeStateHolder()
            .setModuleStates(
                mapOf(
                    "test-module" to ModuleState.CREATED
                )
            )

        composition.prepareRuntime()

        assertNull(
            composition.runtimeStateHolder()
                .failureReason()
        )

        assertEquals(
            emptyMap(),
            composition.runtimeStateHolder()
                .moduleStates()
        )
    }
}
