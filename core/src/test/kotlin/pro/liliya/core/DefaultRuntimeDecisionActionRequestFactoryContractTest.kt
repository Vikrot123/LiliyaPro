package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.DefaultRuntimeDecisionActionRequestFactory
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision

class DefaultRuntimeDecisionActionRequestFactoryContractTest {

    private val factory =
        DefaultRuntimeDecisionActionRequestFactory()

    private val authority =
        RuntimeActionAuthorityContext(
            source = "test-authority",
            level = RuntimeAuthorityLevel.SYSTEM
        )

    @Test
    fun decision_without_command_produces_no_action_request() {

        val decision = RuntimeDecision(
            command = null,
            reason = "no action required",
            confidence = 0.9
        )

        val request = factory.create(
            decision = decision,
            source = "intelligence-decision",
            authority = authority
        )

        assertNull(request)
    }

    @Test
    fun decision_command_becomes_action_request_command() {

        val decision = RuntimeDecision(
            command = RuntimeCommand.HEALTH_CHECK,
            reason = "health check required",
            confidence = 0.8
        )

        val request = factory.create(
            decision = decision,
            source = "intelligence-decision",
            authority = authority
        )

        assertEquals(
            RuntimeCommand.HEALTH_CHECK,
            request?.command
        )
    }

    @Test
    fun decision_reason_is_preserved() {

        val decision = RuntimeDecision(
            command = RuntimeCommand.RECOVER,
            reason = "critical instability",
            confidence = 0.95
        )

        val request = factory.create(
            decision = decision,
            source = "intelligence-decision",
            authority = authority
        )

        assertEquals(
            "critical instability",
            request?.reason
        )
    }

    @Test
    fun decision_source_is_explicit() {

        val decision = RuntimeDecision(
            command = RuntimeCommand.HEALTH_CHECK,
            reason = "degradation detected",
            confidence = 0.7
        )

        val request = factory.create(
            decision = decision,
            source = "runtime-intelligence",
            authority = authority
        )

        assertEquals(
            "runtime-intelligence",
            request?.source
        )
    }

    @Test
    fun authority_is_passed_without_escalation() {

        val decision = RuntimeDecision(
            command = RuntimeCommand.RECOVER,
            reason = "critical instability",
            confidence = 1.0
        )

        val request = factory.create(
            decision = decision,
            source = "runtime-intelligence",
            authority = authority
        )

        assertEquals(
            authority,
            request?.authority
        )

        assertEquals(
            RuntimeAuthorityLevel.SYSTEM,
            request?.resolvedAuthority()?.level
        )
    }

    @Test
    fun factory_does_not_create_request_for_stable_decision() {

        val decision = RuntimeDecision(
            command = null,
            reason = "Runtime is stable; no action required",
            confidence = 1.0
        )

        val request = factory.create(
            decision = decision,
            source = "runtime-intelligence",
            authority = authority
        )

        assertNull(request)
    }
}
