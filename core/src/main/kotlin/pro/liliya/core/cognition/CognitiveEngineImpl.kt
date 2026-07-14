package pro.liliya.core.cognition

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import pro.liliya.core.event.EventProcessor
import pro.liliya.core.memory.MemoryCoordinator
import pro.liliya.domain.api.CognitiveEngine
import pro.liliya.domain.api.ExecutiveController
import pro.liliya.domain.api.PlanningEngine
import pro.liliya.domain.api.ReasoningEngine
import pro.liliya.domain.api.ReflectionEngine
import pro.liliya.domain.models.Episode
import pro.liliya.domain.models.SystemEvent
import java.util.UUID

class CognitiveEngineImpl(
    private val controller: ExecutiveController,
    private val eventProcessor: EventProcessor,
    private val reasoningEngine: ReasoningEngine,
    private val planningEngine: PlanningEngine,
    private val reflectionEngine: ReflectionEngine,
    private val memoryCoordinator: MemoryCoordinator
) : CognitiveEngine {

    override suspend fun process(
        input: String
    ): Flow<String> = flow {

        val event = SystemEvent.UserMessageReceived(
            message = input
        )

        emit("Observing input...")

        eventProcessor.process(event)


        val memories = memoryCoordinator.recall(input)

        if (memories.isNotEmpty()) {
            emit("Memory found: ${memories.size}")
        }


        val reasoning = reasoningEngine.reason(input)

        emit(reasoning.summary)


        val plan = planningEngine.createPlan(reasoning)

        emit("Plan created")

        plan.steps.forEach { step ->
            emit("Plan step: $step")
        }


        controller.processInput(input)
            .collect { response ->
                emit(response)

                memoryCoordinator.rememberInteraction(
                    input = input,
                    response = response
                )
            }


        val episode = Episode(
            id = UUID.randomUUID().toString(),
            events = listOf(event)
        )


        val reflection = reflectionEngine.reflect(
            episode
        )


        memoryCoordinator.rememberInteraction(
            input = input,
            response = reflection
        )


        emit("Reflection: $reflection")
    }
}
