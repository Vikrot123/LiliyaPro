package pro.liliya.runtime

enum class RuntimeStatus {
    STOPPED,
    STARTING,
    LOADING_MODEL,
    READY,
    LISTENING,
    PROCESSING,
    PAUSED,
    ERROR
}
