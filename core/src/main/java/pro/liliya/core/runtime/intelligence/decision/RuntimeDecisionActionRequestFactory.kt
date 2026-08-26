package pro.liliya.core.runtime.intelligence.decision

import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext

interface RuntimeDecisionActionRequestFactory {

    fun create(
        decision: RuntimeDecision,
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeActionRequest?
}
