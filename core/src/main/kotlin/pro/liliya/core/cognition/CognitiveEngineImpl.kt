package pro.liliya.core.cognition

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import pro.liliya.core.event.EventProcessor
import pro.liliya.core.memory.MemoryCoordinator
import pro.liliya.domain.api.CognitiveEngine
import pro.liliya.domain.api.ExecutiveController
import pro.liliya.domain.api.PlanningEngine
import pro.liliya.domain.api.ReasoningEngine
import pro.liliya.domain.api.ReflectionEngine
import pro.liliya.domain.models.CognitiveEvent
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
    ): Flow<CognitiveEvent> = flow {

        emit(
            CognitiveEvent.ThinkingStarted(
                input = input
            )
        )

        val event = SystemEvent.UserMessageReceived(
            message = input
        )

        eventProcessor.process(event)


        emit(
            CognitiveEvent.MemorySearching(
                query = input
            )
        )

        val memories = memoryCoordinator.recall(input)

        if (memories.isNotEmpty()) {
            emit(
                CognitiveEvent.MemoryFound(
                    count = memories.size
                )
            )
        }


        val reasoning = reasoningEngine.reason(input)

        emit(
            CognitiveEvent.ReasoningCompleted(
                summary = reasoning.summary
            )
        )


        val plan = planningEngine.createPlan(reasoning)

        emit(
            CognitiveEvent.PlanningCompleted(
                steps = plan.steps
            )
        )


        emit(
            CognitiveEvent.RespondingStarted
        )


        controller.processInput(input)
            .collect { response ->

                emit(
                    CognitiveEvent.ResponseChunk(
                        text = response
                    )
                )

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


        emit(
            CognitiveEvent.ResponseChunk(
                text = reflection
            )
        )


        emit(
            CognitiveEvent.Completed
        )
    }
}
