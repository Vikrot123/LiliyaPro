package pro.liliya.core.runtime.intelligence.decision

import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext

class DefaultRuntimeDecisionActionRequestFactory :
    RuntimeDecisionActionRequestFactory {

    override fun create(
        decision: RuntimeDecision,
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeActionRequest? {

        val command = decision.command ?: return null

        return RuntimeActionRequest(
            command = command,
            source = source,
            reason = decision.reason,
            authority = authority
        )
    }
}
