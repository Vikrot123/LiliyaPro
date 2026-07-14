package pro.liliya.domain.models

/**
 * План выполнения,
 * созданный Planning Engine.
 */
data class Plan(

    /**
     * Последовательность шагов.
     */
    val steps: List<String>,

    /**
     * Приоритет выполнения.
     */
    val priority: Int = 0,

    /**
     * Идентификатор цели.
     */
    val goalId: String? = null
)
