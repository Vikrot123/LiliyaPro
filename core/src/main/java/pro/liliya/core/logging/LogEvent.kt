package pro.liliya.core.logging


data class LogEvent(

    val time: Long = System.currentTimeMillis(),

    val level: LogLevel,

    val tag: String,

    val marker: String,

    val message: String

)
