package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

class CoreRuntimeAutonomousIntelligenceCyclePostExecutionFailureRecoveryContractTest {

    @Test
    fun post_execution_provenance_is_isolated_across_failed_runtime_recovery() {
        CoreRuntime.resetModuleProvider()
        CoreRuntime.stop()
        CoreRuntime.start()

        val beforeFailure =
            try {
                assertEquals(
                    CoreRuntimeState.RUNNING,
                    CoreRuntime.state()
                )

                val source =
                    "v1.509-before-failure"

                val authority =
                    RuntimeActionAuthorityContext(
                        source =
                            source,
                        level =
                            RuntimeAuthorityLevel.SYSTEM
                    )

                CoreRuntime.processAutonomousIntelligenceCycle(
                    source =
                        source,
                    authority =
                        authority
                )
            } finally {
                CoreRuntime.stop()
            }

        try {
            CoreRuntime.setModuleProvider(
                FailingModuleProvider()
            )

            runCatching {
                CoreRuntime.start()
            }

            assertEquals(
                CoreRuntimeState.FAILED,
                CoreRuntime.state(),
                "startup failure must place runtime in FAILED state"
            )

            val failedAuditBefore =
                CoreRuntime
                    .getRuntimeActionAudit()
                    .size

            val failedSource =
                "v1.509-failed"

            val failedAuthority =
                RuntimeActionAuthorityContext(
                    source =
                        failedSource,
                    level =
                        RuntimeAuthorityLevel.SYSTEM
                )

            assertFailsWith<IllegalStateException> {
                CoreRuntime.processAutonomousIntelligenceCycle(
                    source =
                        failedSource,
                    authority =
                        failedAuthority
                )
            }

            assertEquals(
                CoreRuntimeState.FAILED,
                CoreRuntime.state(),
                "rejected activation must preserve FAILED state"
            )

            assertEquals(
                failedAuditBefore,
                CoreRuntime
                    .getRuntimeActionAudit()
                    .size,
                "FAILED activation must not reach action dispatch"
            )

            CoreRuntime.resetModuleProvider()
            CoreRuntime.start()

            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state(),
                "runtime must recover into RUNNING state"
            )

            val recoveredSource =
                "v1.509-recovered"

            val recoveredAuthority =
                RuntimeActionAuthorityContext(
                    source =
                        recoveredSource,
                    level =
                        RuntimeAuthorityLevel.SYSTEM
                )

            val recovered =
                CoreRuntime.processAutonomousIntelligenceCycle(
                    source =
                        recoveredSource,
                    authority =
                        recoveredAuthority
                )

            val beforeExecution =
                beforeFailure.executionCycle.execution

            val recoveredExecution =
                recovered.executionCycle.execution

            val beforeEvaluation =
                beforeFailure.executionCycle.evaluation

            val recoveredEvaluation =
                recovered.executionCycle.evaluation

            val beforePostExecution =
                beforeFailure.executionCycle.postExecution

            val recoveredPostExecution =
                recovered.executionCycle.postExecution

            assertNotSame(
                beforeExecution,
                recoveredExecution,
                "recovery must produce a new autonomous execution snapshot"
            )

            assertNotSame(
                beforeEvaluation,
                recoveredEvaluation,
                "recovery must produce a new evaluation snapshot"
            )

            assertNotSame(
                beforePostExecution,
                recoveredPostExecution,
                "recovery must produce a new post-execution result"
            )

            assertSame(
                beforeExecution,
                beforeEvaluation.executionResult,
                "pre-failure evaluation must retain pre-failure execution identity"
            )

            assertSame(
                recoveredExecution,
                recoveredEvaluation.executionResult,
                "recovered evaluation must retain recovered execution identity"
            )

            assertSame(
                beforeEvaluation,
                beforePostExecution.evaluation,
                "pre-failure post-execution result must retain its evaluation"
            )

            assertSame(
                recoveredEvaluation,
                recoveredPostExecution.evaluation,
                "recovered post-execution result must retain recovered evaluation"
            )

            assertSame(
                beforeEvaluation,
                beforePostExecution
                    .analysis
                    .evidence
                    .evaluation,
                "pre-failure reflection provenance must remain on pre-failure evaluation"
            )

            assertSame(
                recoveredEvaluation,
                recoveredPostExecution
                    .analysis
                    .evidence
                    .evaluation,
                "recovered reflection provenance must remain on recovered evaluation"
            )

            val beforeMaterialization =
                beforePostExecution.materialization

            val recoveredMaterialization =
                recoveredPostExecution.materialization

            if (beforeMaterialization != null) {
                assertSame(
                    beforeExecution,
                    beforeMaterialization
                        .analysis
                        .evidence
                        .evaluation
                        .executionResult,
                    "pre-failure materialization must resolve to pre-failure execution"
                )
            }

            if (recoveredMaterialization != null) {
                assertSame(
                    recoveredPostExecution.analysis,
                    recoveredMaterialization.analysis,
                    "recovered materialization must preserve recovered analysis identity"
                )

                assertSame(
                    recoveredExecution,
                    recoveredMaterialization
                        .analysis
                        .evidence
                        .evaluation
                        .executionResult,
                    "recovered materialization must resolve to recovered execution"
                )
            }

            if (
                beforeMaterialization != null &&
                recoveredMaterialization != null
            ) {
                assertNotSame(
                    beforeMaterialization,
                    recoveredMaterialization,
                    "recovery must not reuse pre-failure materialization identity"
                )
            }

            val beforeRepresentation =
                beforePostExecution.representation

            val recoveredRepresentation =
                recoveredPostExecution.representation

            if (recoveredRepresentation != null) {
                assertSame(
                    recoveredMaterialization,
                    recoveredRepresentation.materialization,
                    "recovered representation must preserve recovered materialization identity"
                )
            }

            if (
                beforeRepresentation != null &&
                recoveredRepresentation != null
            ) {
                assertNotSame(
                    beforeRepresentation,
                    recoveredRepresentation,
                    "recovery must not reuse pre-failure representation identity"
                )
            }

            val beforeCommitDecision =
                beforePostExecution.commitDecision

            val recoveredCommitDecision =
                recoveredPostExecution.commitDecision

            if (recoveredCommitDecision != null) {
                assertSame(
                    recoveredRepresentation,
                    recoveredCommitDecision.representation,
                    "recovered commit decision must preserve recovered representation identity"
                )
            }

            if (
                beforeCommitDecision != null &&
                recoveredCommitDecision != null
            ) {
                assertNotSame(
                    beforeCommitDecision,
                    recoveredCommitDecision,
                    "recovery must not reuse pre-failure commit decision identity"
                )
            }

            val recoveredCommit =
                recoveredPostExecution.commitResult

            if (recoveredCommit != null) {
                assertSame(
                    recoveredCommitDecision,
                    recoveredCommit.decision,
                    "recovered commit result must preserve recovered decision identity"
                )

                assertSame(
                    recoveredRepresentation?.experience,
                    recoveredCommit.experience,
                    "recovered commit result must preserve recovered experience identity"
                )
            }

            val recoveredRequest =
                recoveredExecution
                    .execution
                    .request

            if (recoveredRequest != null) {
                assertEquals(
                    recoveredSource,
                    recoveredRequest.source,
                    "recovered request must preserve recovered source"
                )

                assertSame(
                    recoveredAuthority,
                    recoveredRequest.authority,
                    "recovered request must preserve recovered authority identity"
                )
            }

            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state(),
                "provenance verification must preserve recovered RUNNING state"
            )
        } finally {
            CoreRuntime.resetModuleProvider()
            CoreRuntime.stop()
        }

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state(),
            "test cleanup must leave runtime STOPPED"
        )
    }
}
