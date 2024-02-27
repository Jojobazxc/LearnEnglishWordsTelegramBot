import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(private val botToken: String) {

    fun getUpdate(updateId: Int): String {
        val urlGetUpdates = "$TELEGRAM_BOT_API_BASE_URL$botToken/getUpdates?offset=$updateId"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(chatId: String?, textMessage: String?): String {
        val encodedText = URLEncoder.encode(textMessage, Charsets.UTF_8)
        val urlSendMessage = "$TELEGRAM_BOT_API_BASE_URL$botToken/sendMessage?chat_id=$chatId&text=$encodedText"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMenu(chatId: String?): String {
        val urlSendMessage = "$TELEGRAM_BOT_API_BASE_URL$botToken/sendMessage"
        val sendMenuBody = """
            {
                "chat_id": $chatId,
                "text": "Основное меню",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "Изучить слова",
                                "callback_data": "$CALLBACK_DATA_LEARN_BUTTON"
                                
                            },
                            {
                                "text": "Статистика",
                                "callback_data": "$CALLBACK_DATA_STATISTICS_BUTTON"                                
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendQuestion(chatId: String?, question: Question): String? {
        val urlSendMessage = "$TELEGRAM_BOT_API_BASE_URL$botToken/sendMessage"
        val sendQuestion = """
            {
                "chat_id": $chatId,
                "text": "${question.wordForLearning.original}",
                "reply_markup": {
                    "inline_keyboard": [
                        [
                            {
                                "text": "${question.answers[0].translate}",
                                "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX + 0}"
                                
                            },
                            {
                                "text": "${question.answers[1].translate}",
                                "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX + 1}"                                
                            }                        
                        ],
                        [
                            {
                                "text": "${question.answers[2].translate}",
                                "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX + 2}" 
                            },
                            {
                                "text": "${question.answers[3].translate}",
                                "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX + 3}" 
                            }
                        ]
                    ]
                }
            }
        """.trimIndent()
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendQuestion))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, chatId: String?) {
        if (trainer.getNextQuestion() == null) sendMessage(chatId, "Вы выучили все слова!")
        else sendQuestion(chatId, trainer.getNextQuestion()!!)
    }
}


