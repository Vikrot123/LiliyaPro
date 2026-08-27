package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

class CoreRuntimeAutonomousIntelligenceCyclePostExecutionProvenanceContractTest {

    @Test
    fun repeated_autonomous_cycles_preserve_independent_post_execution_provenance() {
        CoreRuntime.stop()
        CoreRuntime.start()

        try {
            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state()
            )

            val firstSource =
                "v1.507-first"

            val firstAuthority =
                RuntimeActionAuthorityContext(
                    source =
                        firstSource,
                    level =
                        RuntimeAuthorityLevel.SYSTEM
                )

            val first =
                CoreRuntime.processAutonomousIntelligenceCycle(
                    source =
                        firstSource,
                    authority =
                        firstAuthority
                )

            val secondSource =
                "v1.507-second"

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
                "separate autonomous cycles must own distinct execution snapshots"
            )

            assertNotSame(
                firstEvaluation,
                secondEvaluation,
                "separate autonomous cycles must own distinct evaluation snapshots"
            )

            assertNotSame(
                firstPostExecution,
                secondPostExecution,
                "separate autonomous cycles must own distinct post-execution results"
            )

            assertSame(
                firstExecution,
                firstEvaluation.executionResult,
                "first evaluation must preserve first execution identity"
            )

            assertSame(
                secondExecution,
                secondEvaluation.executionResult,
                "second evaluation must preserve second execution identity"
            )

            assertSame(
                firstEvaluation,
                firstPostExecution.evaluation,
                "first post-execution result must preserve first evaluation identity"
            )

            assertSame(
                secondEvaluation,
                secondPostExecution.evaluation,
                "second post-execution result must preserve second evaluation identity"
            )

            assertSame(
                firstEvaluation,
                firstPostExecution.analysis.evidence.evaluation,
                "first reflection provenance must point to first evaluation"
            )

            assertSame(
                secondEvaluation,
                secondPostExecution.analysis.evidence.evaluation,
                "second reflection provenance must point to second evaluation"
            )

            val firstMaterialization =
                firstPostExecution.materialization

            if (firstMaterialization != null) {
                assertSame(
                    firstPostExecution.analysis,
                    firstMaterialization.analysis,
                    "first materialization must preserve first analysis identity"
                )

                assertSame(
                    firstEvaluation,
                    firstMaterialization
                        .analysis
                        .evidence
                        .evaluation,
                    "first materialization provenance must remain on first evaluation"
                )
            }

            val secondMaterialization =
                secondPostExecution.materialization

            if (secondMaterialization != null) {
                assertSame(
                    secondPostExecution.analysis,
                    secondMaterialization.analysis,
                    "second materialization must preserve second analysis identity"
                )

                assertSame(
                    secondEvaluation,
                    secondMaterialization
                        .analysis
                        .evidence
                        .evaluation,
                    "second materialization provenance must remain on second evaluation"
                )
            }

            val firstRepresentation =
                firstPostExecution.representation

            if (firstRepresentation != null) {
                assertSame(
                    firstMaterialization,
                    firstRepresentation.materialization,
                    "first representation must preserve first materialization identity"
                )
            }

            val secondRepresentation =
                secondPostExecution.representation

            if (secondRepresentation != null) {
                assertSame(
                    secondMaterialization,
                    secondRepresentation.materialization,
                    "second representation must preserve second materialization identity"
                )
            }

            val firstCommitDecision =
                firstPostExecution.commitDecision

            if (firstCommitDecision != null) {
                assertSame(
                    firstRepresentation,
                    firstCommitDecision.representation,
                    "first commit decision must preserve first representation identity"
                )
            }

            val secondCommitDecision =
                secondPostExecution.commitDecision

            if (secondCommitDecision != null) {
                assertSame(
                    secondRepresentation,
                    secondCommitDecision.representation,
                    "second commit decision must preserve second representation identity"
                )
            }

            val firstCommit =
                firstPostExecution.commitResult

            if (firstCommit != null) {
                assertSame(
                    firstCommitDecision,
                    firstCommit.decision,
                    "first commit result must preserve first decision identity"
                )

                assertSame(
                    firstRepresentation?.experience,
                    firstCommit.experience,
                    "first commit must preserve first experience identity"
                )
            }

            val secondCommit =
                secondPostExecution.commitResult

            if (secondCommit != null) {
                assertSame(
                    secondCommitDecision,
                    secondCommit.decision,
                    "second commit result must preserve second decision identity"
                )

                assertSame(
                    secondRepresentation?.experience,
                    secondCommit.experience,
                    "second commit must preserve second experience identity"
                )
            }

            if (
                firstMaterialization != null &&
                secondMaterialization != null
            ) {
                assertNotSame(
                    firstMaterialization,
                    secondMaterialization,
                    "separate cycles must not share materialization snapshots"
                )
            }

            if (
                firstRepresentation != null &&
                secondRepresentation != null
            ) {
                assertNotSame(
                    firstRepresentation,
                    secondRepresentation,
                    "separate cycles must not share experience representations"
                )
            }

            if (
                firstCommitDecision != null &&
                secondCommitDecision != null
            ) {
                assertNotSame(
                    firstCommitDecision,
                    secondCommitDecision,
                    "separate cycles must not share commit decisions"
                )
            }

            assertEquals(
                CoreRuntimeState.RUNNING,
                CoreRuntime.state(),
                "provenance verification must preserve RUNNING state"
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
