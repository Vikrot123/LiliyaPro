package pro.liliya.core.runtime.control.composition

import pro.liliya.core.runtime.action.RuntimeActionExecutor
import pro.liliya.core.runtime.audit.RuntimeActionAuditProvider
import pro.liliya.core.runtime.composition.RuntimeComposition
import pro.liliya.core.runtime.control.DefaultRuntimeControl
import pro.liliya.core.runtime.control.RuntimeControlRegistry
import pro.liliya.core.runtime.dispatcher.HealthRuntimeActionHandler
import pro.liliya.core.runtime.dispatcher.RuntimeActionDispatcher
import pro.liliya.core.runtime.dispatcher.RuntimeActionHandlerRegistry
import pro.liliya.core.runtime.history.RuntimeCommandHistoryProvider
import pro.liliya.core.runtime.policy.RuntimeActionPolicyEvaluator

class DefaultRuntimeControlComposition(
    private val runtimeComposition: RuntimeComposition,
    private val actionPolicyEvaluator: RuntimeActionPolicyEvaluator
) : RuntimeControlComposition {

    private val controlRegistry = RuntimeControlRegistry()

    private val commandHistory = RuntimeCommandHistoryProvider()

    private val actionAuditProvider = RuntimeActionAuditProvider()

    private val actionHandlerRegistry = RuntimeActionHandlerRegistry()

    private val defaultRuntimeControl =
        DefaultRuntimeControl(
            runtimeComposition
        )

    private val actionDispatcher =
        RuntimeActionDispatcher(
            actionHandlerRegistry,
            actionAuditProvider,
            actionPolicyEvaluator,
            runtimeComposition
        )

    private val healthRuntimeActionHandler =
        HealthRuntimeActionHandler(
            RuntimeActionExecutor(
                defaultRuntimeControl
            )
        )

    override fun controlRegistry(): RuntimeControlRegistry {
        return controlRegistry
    }

    override fun commandHistoryProvider(): RuntimeCommandHistoryProvider {
        return commandHistory
    }

    override fun actionAuditProvider(): RuntimeActionAuditProvider {
        return actionAuditProvider
    }

    override fun actionHandlerRegistry(): RuntimeActionHandlerRegistry {
        return actionHandlerRegistry
    }

    override fun actionDispatcher(): RuntimeActionDispatcher {
        return actionDispatcher
    }

    override fun defaultRuntimeControl(): DefaultRuntimeControl {
        return defaultRuntimeControl
    }

    override fun healthRuntimeActionHandler(): HealthRuntimeActionHandler {
        return healthRuntimeActionHandler
    }
}
