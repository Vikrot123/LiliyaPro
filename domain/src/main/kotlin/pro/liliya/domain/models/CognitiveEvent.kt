package pro.liliya.domain.models

/**
 * События когнитивного цикла LiliyaPro.
 *
 * Используются для передачи информации
 * между Cognitive Engine и Runtime Layer.
 */
sealed class CognitiveEvent {

    /**
     * Начало анализа пользовательского ввода.
     */
    data class ThinkingStarted(
        val input: String
    ) : CognitiveEvent()


    /**
     * Начат поиск информации в памяти.
     */
    data class MemorySearching(
        val query: String
    ) : CognitiveEvent()


    /**
     * Найдены данные в памяти.
     */
    data class MemoryFound(
        val count: Int
    ) : CognitiveEvent()


    /**
     * Завершено рассуждение.
     */
    data class ReasoningCompleted(
        val summary: String
    ) : CognitiveEvent()


    /**
     * План действий создан.
     */
    data class PlanningCompleted(
        val steps: List<String>
    ) : CognitiveEvent()


    /**
     * Начата генерация ответа.
     */
    data object RespondingStarted : CognitiveEvent()


    /**
     * Часть ответа модели.
     */
    data class ResponseChunk(
        val text: String
    ) : CognitiveEvent()


    /**
     * Цикл обработки завершён.
     */
    data object Completed : CognitiveEvent()


    /**
     * Ошибка обработки.
     */
    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : CognitiveEvent()
}
