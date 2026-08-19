package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.audit.RuntimeActionAuditRecord
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.history.RuntimeCommandRecord

class RuntimeCompositionRuntimeHistoryPrepareIsolationContractTest {

    @Test
    fun runtime_history_state_does_not_leak_after_prepare_runtime() {
        val composition = DefaultRuntimeComposition()

        composition.commandHistoryProvider()
            .record(
                RuntimeCommandRecord(
                    command = RuntimeCommand.START,
                    success = true,
                    previousState = CoreRuntimeState.STOPPED,
                    currentState = CoreRuntimeState.RUNNING,
                    message = "test-command"
                )
            )

        composition.actionAuditProvider()
            .record(
                RuntimeActionAuditRecord(
                    request = RuntimeActionRequest(
                        command = RuntimeCommand.START,
                        source = "test"
                    ),
                    success = true,
                    message = "test-action"
                )
            )

        assertEquals(
            1,
            composition.commandHistoryProvider()
                .snapshot()
                .size
        )

        assertEquals(
            1,
            composition.actionAuditProvider()
                .snapshot()
                .size
        )

        composition.prepareRuntime()

        assertEquals(
            0,
            composition.commandHistoryProvider()
                .snapshot()
                .size
        )

        assertEquals(
            0,
            composition.actionAuditProvider()
                .snapshot()
                .size
        )
    }
}
