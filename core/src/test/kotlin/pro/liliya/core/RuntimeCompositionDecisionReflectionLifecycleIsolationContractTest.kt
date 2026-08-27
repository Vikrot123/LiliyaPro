package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionDecisionReflectionLifecycleIsolationContractTest {

    @Test
    fun separate_compositions_own_independent_analyzers() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.decisionReflectionAnalyzer(),
            second.decisionReflectionAnalyzer()
        )
    }

    @Test
    fun prepare_runtime_preserves_stateless_analyzer_owner() {
        val composition =
            DefaultRuntimeComposition()

        val before =
            composition
                .decisionReflectionAnalyzer()

        composition.prepareRuntime()

        assertSame(
            before,
            composition
                .decisionReflectionAnalyzer()
        )
    }
}
