package pro.liliya.app


import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


import pro.liliya.app.application.AppContainer
import pro.liliya.runtime.RuntimeStatus
import pro.liliya.runtime.RuntimeStatusService


class MainActivity : Activity() {



    private lateinit var chatBox: TextView

    private lateinit var statusText: TextView

    private lateinit var input: EditText

    private lateinit var sendButton: Button

    private lateinit var scrollView: ScrollView



    private val controller =
        AppContainer.chatController



    private val scope =
        CoroutineScope(
            SupervisorJob()
                    + Dispatchers.Main
        )





    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        createUI()


        startRuntime()

    }






    private fun createUI() {


        val root =
            LinearLayout(this).apply {


                orientation =
                    LinearLayout.VERTICAL


                setPadding(
                    32,
                    32,
                    32,
                    32
                )


                setBackgroundColor(
                    Color.WHITE
                )

            }





        val title =
            TextView(this).apply {


                text =
                    "LiliyaPro"


                textSize =
                    32f


                gravity =
                    Gravity.CENTER


                setTypeface(
                    null,
                    Typeface.BOLD
                )


                setTextColor(
                    Color.BLACK
                )

            }






        statusText =
            TextView(this).apply {


                text =
                    "Runtime: BOOTING"


                textSize =
                    16f


                gravity =
                    Gravity.CENTER


                setTextColor(
                    Color.DKGRAY
                )


                setPadding(
                    0,
                    20,
                    0,
                    20
                )

            }







        chatBox =
            TextView(this).apply {


                text =
"""
LiliyaPro

Когнитивное ядро запускается...

""".trimIndent()



                textSize =
                    18f



                setTextColor(
                    Color.BLACK
                )



                setPadding(
                    16,
                    16,
                    16,
                    16
                )

            }







        scrollView =
            ScrollView(this).apply {


                addView(
                    chatBox,
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )

            }







        input =
            EditText(this).apply {


                hint =
                    "Введите сообщение..."


                textSize =
                    18f

            }







        sendButton =
            Button(this).apply {


                text =
                    "Отправить"

            }






        sendButton.setOnClickListener {


            sendMessage()

        }







        root.addView(
            title,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )



        root.addView(
            statusText,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )



        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                -1,
                0,
                1f
            )
        )



        root.addView(
            input,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )



        root.addView(
            sendButton,
            LinearLayout.LayoutParams(
                -1,
                -2
            )
        )




        setContentView(
            root
        )

    }








    private fun startRuntime() {
scope.launch {

    RuntimeStatusService.status.collect { status ->

        statusText.text = "Runtime: ${status.name}"

    }

}

        scope.launch {


            try {


                controller.start()



                statusText.text =
                    "Runtime: READY"



                chatBox.append(
                    "\n\n✓ Runtime готов"
                )



            }
            catch(
                error: Exception
            ) {


                statusText.text =
                    "Runtime: ERROR"


                chatBox.append(
                    "\n\nОшибка запуска: ${error.message}"
                )

            }


        }


    }








    private fun sendMessage() {


        val message =
            input.text
                .toString()
                .trim()



        if(message.isEmpty()) {

            return

        }




        input.text.clear()



        sendButton.isEnabled =
            false




        scope.launch {


            try {



                statusText.text =
                    "Runtime: THINKING"





                chatBox.append(
                    "\n\n\nТы:\n$message\n\nLiliya:\n"
                )






                controller
                    .sendMessage(message)
                    .collect { response ->



                        chatBox.append(
                            response
                        )


                        scrollDown()

                    }





                statusText.text =
                    "Runtime: READY"



            }
            catch(
                error: Exception
            ) {



                chatBox.append(
                    "\n\nОшибка: ${error.message}"
                )



                statusText.text =
                    "Runtime: ERROR"


            }
            finally {


                sendButton.isEnabled =
                    true

            }


        }


    }









    private fun scrollDown() {


        scrollView.post {


            scrollView.fullScroll(
                ScrollView.FOCUS_DOWN
            )

        }


    }








    override fun onDestroy() {


        scope.launch {


            controller.stop()

        }



        scope.cancel()



        super.onDestroy()

    }


}
