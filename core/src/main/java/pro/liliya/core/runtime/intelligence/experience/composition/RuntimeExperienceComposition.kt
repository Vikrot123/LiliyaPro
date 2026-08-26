package pro.liliya.core.runtime.intelligence.experience.composition

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceEngine
import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidator
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecisionEngine
import pro.liliya.core.runtime.intelligence.experience.store.RuntimeExperienceStore

interface RuntimeExperienceComposition {

    fun experienceEngine(): RuntimeExperienceEngine

    fun experienceDecisionEngine(): RuntimeExperienceDecisionEngine

    fun experienceStore(): RuntimeExperienceStore

    fun experienceConsolidator(): RuntimeExperienceConsolidator
}
