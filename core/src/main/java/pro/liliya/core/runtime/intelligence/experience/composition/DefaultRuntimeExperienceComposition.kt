package pro.liliya.core.runtime.intelligence.experience.composition

import pro.liliya.core.runtime.intelligence.experience.DefaultRuntimeExperienceEngine
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceEngine
import pro.liliya.core.runtime.intelligence.experience.consolidation.DefaultRuntimeExperienceConsolidator
import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidator
import pro.liliya.core.runtime.intelligence.experience.decision.DefaultRuntimeExperienceDecisionEngine
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecisionEngine
import pro.liliya.core.runtime.intelligence.experience.store.DefaultRuntimeExperienceStore
import pro.liliya.core.runtime.intelligence.experience.store.RuntimeExperienceStore

class DefaultRuntimeExperienceComposition : RuntimeExperienceComposition {

    private val experienceEngine: RuntimeExperienceEngine =
        DefaultRuntimeExperienceEngine()

    private val experienceDecisionEngine: RuntimeExperienceDecisionEngine =
        DefaultRuntimeExperienceDecisionEngine()

    private val experienceStore: RuntimeExperienceStore =
        DefaultRuntimeExperienceStore()

    private val experienceConsolidator: RuntimeExperienceConsolidator =
        DefaultRuntimeExperienceConsolidator()

    override fun experienceEngine(): RuntimeExperienceEngine {
        return experienceEngine
    }

    override fun experienceDecisionEngine(): RuntimeExperienceDecisionEngine {
        return experienceDecisionEngine
    }

    override fun experienceStore(): RuntimeExperienceStore {
        return experienceStore
    }

    override fun experienceConsolidator(): RuntimeExperienceConsolidator {
        return experienceConsolidator
    }
}
