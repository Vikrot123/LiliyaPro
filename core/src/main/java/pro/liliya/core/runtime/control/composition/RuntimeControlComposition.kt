package pro.liliya.core.runtime.control.composition

import pro.liliya.core.runtime.audit.RuntimeActionAuditProvider
import pro.liliya.core.runtime.control.DefaultRuntimeControl
import pro.liliya.core.runtime.control.RuntimeControlRegistry
import pro.liliya.core.runtime.dispatcher.HealthRuntimeActionHandler
import pro.liliya.core.runtime.dispatcher.RuntimeActionDispatcher
import pro.liliya.core.runtime.dispatcher.RuntimeActionHandlerRegistry
import pro.liliya.core.runtime.history.RuntimeCommandHistoryProvider

interface RuntimeControlComposition {

    fun controlRegistry(): RuntimeControlRegistry

    fun commandHistoryProvider(): RuntimeCommandHistoryProvider

    fun actionAuditProvider(): RuntimeActionAuditProvider

    fun actionHandlerRegistry(): RuntimeActionHandlerRegistry

    fun actionDispatcher(): RuntimeActionDispatcher

    fun defaultRuntimeControl(): DefaultRuntimeControl

    fun healthRuntimeActionHandler(): HealthRuntimeActionHandler
}
