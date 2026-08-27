package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

class CoreRuntimeAutonomousIntelligenceCyclePostExecutionRestartIsolationContractTest {

    @Test
    fun post_execution_provenance_is_isolated_across_runtime_restart() {
        CoreRuntime.stop()
        CoreRuntime.start()

        val first =
            try {
                assertEquals(
                    CoreRuntimeState.RUNNING,
                    CoreRuntime.state()
                )

                val source =
                    "v1.508-before-restart"

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

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state()
        )

        CoreRuntime.start()

        try {
            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state()
            )

            val secondSource =
                "v1.508-after-restart"

            val secondAuthority =
                RuntimeActionAuthorityContext(
                    source =
                        secondSource,
                    level =
                        RuntimeAuthorityLevel.SYSTEM
                )

            val second =
                CoreRuntime.processAutonomousIntelligenceCycle(
                    source =
                        secondSource,
                    authority =
                        secondAuthority
                )

            val firstExecution =
                first.executionCycle.execution

            val secondExecution =
                second.executionCycle.execution

            val firstEvaluation =
                first.executionCycle.evaluation

            val secondEvaluation =
                second.executionCycle.evaluation

            val firstPostExecution =
                first.executionCycle.postExecution

            val secondPostExecution =
                second.executionCycle.postExecution

            assertNotSame(
                firstExecution,
                secondExecution,
                "restart must produce a new autonomous execution snapshot"
            )

            assertNotSame(
                firstEvaluation,
                secondEvaluation,
                "restart must produce a new autonomous evaluation snapshot"
            )

            assertNotSame(
                firstPostExecution,
                secondPostExecution,
                "restart must produce a new post-execution result"
            )

            assertSame(
                firstExecution,
                firstEvaluation.executionResult,
                "pre-restart evaluation must retain pre-restart execution identity"
            )

            assertSame(
                secondExecution,
                secondEvaluation.executionResult,
                "post-restart evaluation must retain post-restart execution identity"
            )

            assertSame(
                firstEvaluation,
                firstPostExecution.evaluation,
                "pre-restart post-execution result must retain its evaluation"
            )

            assertSame(
                secondEvaluation,
                secondPostExecution.evaluation,
                "post-restart post-execution result must retain its evaluation"
            )

            assertSame(
                firstEvaluation,
                firstPostExecution
                    .analysis
                    .evidence
                    .evaluation,
                "pre-restart reflection provenance must remain on pre-restart evaluation"
            )

            assertSame(
                secondEvaluation,
                secondPostExecution
                    .analysis
                    .evidence
                    .evaluation,
                "post-restart reflection provenance must remain on post-restart evaluation"
            )

            val firstMaterialization =
                firstPostExecution.materialization

            val secondMaterialization =
                secondPostExecution.materialization

            if (firstMaterialization != null) {
                assertSame(
                    firstPostExecution.analysis,
                    firstMaterialization.analysis,
                    "pre-restart materialization must retain pre-restart analysis"
                )

                assertSame(
                    firstExecution,
                    firstMaterialization
                        .analysis
                        .evidence
                        .evaluation
                        .executionResult,
                    "pre-restart materialization must resolve to pre-restart execution"
                )
            }

            if (secondMaterialization != null) {
                assertSame(
                    secondPostExecution.analysis,
                    secondMaterialization.analysis,
                    "post-restart materialization must retain post-restart analysis"
                )

                assertSame(
                    secondExecution,
                    secondMaterialization
                        .analysis
                        .evidence
                        .evaluation
                        .executionResult,
                    "post-restart materialization must resolve to post-restart execution"
                )
            }

            if (
                firstMaterialization != null &&
                secondMaterialization != null
            ) {
                assertNotSame(
                    firstMaterialization,
                    secondMaterialization,
                    "restart must not reuse materialization identity"
                )
            }

            val firstRepresentation =
                firstPostExecution.representation

            val secondRepresentation =
                secondPostExecution.representation

            if (firstRepresentation != null) {
                assertSame(
                    firstMaterialization,
                    firstRepresentation.materialization,
                    "pre-restart representation must retain its materialization"
                )
            }

            if (secondRepresentation != null) {
                assertSame(
                    secondMaterialization,
                    secondRepresentation.materialization,
                    "post-restart representation must retain its materialization"
                )
            }

            if (
                firstRepresentation != null &&
                secondRepresentation != null
            ) {
                assertNotSame(
                    firstRepresentation,
                    secondRepresentation,
                    "restart must not reuse representation identity"
                )
            }

            val firstCommitDecision =
                firstPostExecution.commitDecision

            val secondCommitDecision =
                secondPostExecution.commitDecision

            if (firstCommitDecision != null) {
                assertSame(
                    firstRepresentation,
                    firstCommitDecision.representation,
                    "pre-restart commit decision must retain its representation"
                )
            }

            if (secondCommitDecision != null) {
                assertSame(
                    secondRepresentation,
                    secondCommitDecision.representation,
                    "post-restart commit decision must retain its representation"
                )
            }

            if (
                firstCommitDecision != null &&
                secondCommitDecision != null
            ) {
                assertNotSame(
                    firstCommitDecision,
                    secondCommitDecision,
                    "restart must not reuse commit decision identity"
                )
            }

            val firstCommit =
                firstPostExecution.commitResult

            val secondCommit =
                secondPostExecution.commitResult

            if (firstCommit != null) {
                assertSame(
                    firstCommitDecision,
                    firstCommit.decision,
                    "pre-restart commit result must retain its decision"
                )
            }

            if (secondCommit != null) {
                assertSame(
                    secondCommitDecision,
                    secondCommit.decision,
                    "post-restart commit result must retain its decision"
                )
            }

            val secondRequest =
                secondExecution.execution.request

            if (secondRequest != null) {
                assertEquals(
                    secondSource,
                    secondRequest.source
                )

                assertSame(
                    secondAuthority,
                    secondRequest.authority
                )
            }

            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state(),
                "restart provenance verification must leave runtime running"
            )
        } finally {
            CoreRuntime.stop()
        }

        assertEquals(
            CoreRuntimeState.STOPPED,
            CoreRuntime.state()
        )
    }
}
