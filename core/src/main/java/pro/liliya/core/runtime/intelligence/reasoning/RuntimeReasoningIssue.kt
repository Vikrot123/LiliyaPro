package pro.liliya.core.runtime.intelligence.reasoning

enum class RuntimeReasoningIssue {
    GOAL_MISMATCH,
    PLAN_STATE_MISMATCH,
    EMPTY_PLAN,
    STEP_ORDER_INVALID,
    REQUIRED_STEP_MISSING,
    UNEXPECTED_STEP,
    ACTIONABILITY_CONTRADICTION
}
