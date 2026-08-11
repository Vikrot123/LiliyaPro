package pro.liliya.core.logging


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object LogFormatter {


    fun format(

        event: LogEvent

    ): String {


        val time =

            SimpleDateFormat(

                "yyyy-MM-dd HH:mm:ss.SSS",

                Locale.US

            ).format(

                Date(event.time)

            )


        return buildString {


            append(time)

            append(" [LILIYA]")


            append(" [")

            append(event.level.name)

            append("]")


            append(" [")

            append(event.tag)

            append("]")


            append(" [")

            append(event.marker)

            append("]")


            append(" ")


            append(event.message)


            append("\n")

        }

    }

}
