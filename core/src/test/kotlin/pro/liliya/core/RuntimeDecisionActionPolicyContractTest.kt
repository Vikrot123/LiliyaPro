package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityAuthorityEvaluator
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityRegistry
import pro.liliya.core.runtime.capability.RuntimeCapabilityResolver
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.DefaultRuntimeDecisionActionRequestFactory
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.policy.DefaultRuntimeActionPolicyEvaluator
import pro.liliya.core.runtime.policy.RuntimeActionPolicyDecision

class RuntimeDecisionActionPolicyContractTest {

    private val authorityEvaluator =
        DefaultRuntimeCapabilityAuthorityEvaluator()

    private val resolver =
        RuntimeCapabilityResolver(
            registry = DefaultRuntimeCapabilityRegistry(),
            authorityEvaluator = authorityEvaluator
        )

    private val policy =
        DefaultRuntimeActionPolicyEvaluator(
            capabilityResolver = resolver
        )

    private val factory =
        DefaultRuntimeDecisionActionRequestFactory()

    @Test
    fun system_recovery_decision_becomes_allowed_policy_request() {

        val decision = RuntimeDecision(
            command = RuntimeCommand.RECOVER,
            reason = "critical instability",
            confidence = 0.95
        )

        val request = factory.create(
            decision = decision,
            source = "runtime-intelligence",
            authority = RuntimeActionAuthorityContext(
                source = "system-controller",
                level = RuntimeAuthorityLevel.SYSTEM
            )
        )

        val result = policy.evaluate(
            checkNotNull(request)
        )

        assertEquals(
            RuntimeActionPolicyDecision.ALLOW,
            result.decision
        )

        assertTrue(result.capabilityAllowed == true)
        assertEquals(
            RuntimeAuthorityLevel.SYSTEM,
            result.authorityLevel
        )
    }

    @Test
    fun user_recovery_decision_is_denied_by_policy() {

        val decision = RuntimeDecision(
            command = RuntimeCommand.RECOVER,
            reason = "critical instability",
            confidence = 0.95
        )

        val request = factory.create(
            decision = decision,
            source = "runtime-intelligence",
            authority = RuntimeActionAuthorityContext(
                source = "user-controller",
                level = RuntimeAuthorityLevel.USER
            )
        )

        val result = policy.evaluate(
            checkNotNull(request)
        )

        assertEquals(
            RuntimeActionPolicyDecision.DENY,
            result.decision
        )

        assertFalse(result.capabilityAllowed == true)

        assertEquals(
            RuntimeAuthorityLevel.USER,
            result.authorityLevel
        )

        assertEquals(
            RuntimeAuthorityLevel.SYSTEM,
            result.requiredAuthority
        )
    }

    @Test
    fun user_health_check_is_allowed() {

        val decision = RuntimeDecision(
            command = RuntimeCommand.HEALTH_CHECK,
            reason = "health verification required",
            confidence = 0.8
        )

        val request = factory.create(
            decision = decision,
            source = "runtime-intelligence",
            authority = RuntimeActionAuthorityContext(
                source = "user-controller",
                level = RuntimeAuthorityLevel.USER
            )
        )

        val result = policy.evaluate(
            checkNotNull(request)
        )

        assertEquals(
            RuntimeActionPolicyDecision.ALLOW,
            result.decision
        )

        assertTrue(result.capabilityAllowed == true)

        assertEquals(
            RuntimeAuthorityLevel.USER,
            result.authorityLevel
        )
    }

    @Test
    fun policy_preserves_action_request_authority_source() {

        val decision = RuntimeDecision(
            command = RuntimeCommand.HEALTH_CHECK,
            reason = "verify runtime",
            confidence = 0.9
        )

        val request = factory.create(
            decision = decision,
            source = "runtime-intelligence",
            authority = RuntimeActionAuthorityContext(
                source = "explicit-system-authority",
                level = RuntimeAuthorityLevel.USER
            )
        )

        val result = policy.evaluate(
            checkNotNull(request)
        )

        assertEquals(
            "explicit-system-authority",
            result.authoritySource
        )

        assertEquals(
            RuntimeAuthorityLevel.USER,
            result.actualAuthority
        )
    }

    @Test
    fun decision_cannot_escalate_authority() {

        val decision = RuntimeDecision(
            command = RuntimeCommand.RECOVER,
            reason = "request recovery",
            confidence = 1.0
        )

        val request = factory.create(
            decision = decision,
            source = "runtime-intelligence",
            authority = RuntimeActionAuthorityContext(
                source = "user-controller",
                level = RuntimeAuthorityLevel.USER
            )
        )

        val result = policy.evaluate(
            checkNotNull(request)
        )

        assertEquals(
            RuntimeAuthorityLevel.USER,
            result.actualAuthority
        )

        assertEquals(
            RuntimeAuthorityLevel.SYSTEM,
            result.requiredAuthority
        )

        assertEquals(
            RuntimeActionPolicyDecision.DENY,
            result.decision
        )
    }
}
