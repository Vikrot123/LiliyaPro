package pro.liliya.core

import kotlin.test.Test
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.composition.DefaultRuntimeKnowledgeLifecycleCompositionHolder

class DefaultRuntimeKnowledgeLifecycleCompositionHolderContractTest {

    @Test
    fun holder_returns_same_composition_instance() {

        val holder =
            DefaultRuntimeKnowledgeLifecycleCompositionHolder(DefaultRuntimeKnowledgeMemory(), DefaultRuntimeKnowledgeLifecycleStateStore())

        val first =
            holder.composition()

        val second =
            holder.composition()

        assertSame(
            first,
            second
        )
    }

    @Test
    fun holder_reset_creates_new_composition_instance() {

        val holder =
            DefaultRuntimeKnowledgeLifecycleCompositionHolder(DefaultRuntimeKnowledgeMemory(), DefaultRuntimeKnowledgeLifecycleStateStore())

        val first =
            holder.composition()

        holder.reset()

        val second =
            holder.composition()

        assertNotSame(
            first,
            second
        )
    }
}
