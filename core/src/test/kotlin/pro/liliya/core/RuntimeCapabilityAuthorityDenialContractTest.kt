package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.policy.RuntimeActionPolicyDecision

class RuntimeCapabilityAuthorityDenialContractTest {

    @Test
    fun capability_denies_action_with_insufficient_authority() {
        val composition = DefaultRuntimeComposition()

        val request = RuntimeActionRequest(
            command = RuntimeCommand.START,
            authority = RuntimeActionAuthorityContext(
                source = "test",
                level = RuntimeAuthorityLevel.USER
            )
        )

        val result = composition
            .actionPolicyEvaluator()
            .evaluate(request)

        assertEquals(
            RuntimeActionPolicyDecision.DENY,
            result.decision
        )

        assertEquals(
            false,
            result.capabilityAllowed
        )

        assertEquals(
            RuntimeAuthorityLevel.USER,
            result.actualAuthority
        )

        assertEquals(
            RuntimeAuthorityLevel.SYSTEM,
            result.requiredAuthority
        )
    }
}
