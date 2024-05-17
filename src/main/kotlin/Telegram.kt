import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>
)

@Serializable
class InlineKeyboard(
    @SerialName("callback_data")
    val callbackData: String,
    @SerialName("text")
    val text: String,
)


fun main(args: Array<String>) {

    var lastUpdateId = 0L
    val telegramBot = TelegramBotService(args[0])
    val trainers = HashMap<Long, LearnWordsTrainer>()

    while (true) {
        Thread.sleep(2000)

        val response: Response = telegramBot.getUpdate(lastUpdateId)
        println(response)
        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, trainers, telegramBot) }
        lastUpdateId = sortedUpdates.last().updateId + 1
    }

}

fun handleUpdate(
    update: Update,
    trainers: HashMap<Long, LearnWordsTrainer>,
    telegramBot: TelegramBotService
) {

    val message = update.message?.text
    val chatId = update.message?.chat?.id ?: update.callbackQuery?.message?.chat?.id ?: return
    val data = update.callbackQuery?.data

    val trainer = trainers.getOrPut(chatId) { LearnWordsTrainer("$chatId.txt") }
    val statistics = trainer.getStatistics()

    if (message?.lowercase() == START_BOT) {
        telegramBot.sendMenu(chatId)
    }
    if (data?.lowercase() == CALLBACK_DATA_STATISTICS_BUTTON) {
        telegramBot.sendMessage(
            chatId,
            "Выучено ${statistics.quantityOfLearnedWords.size} из ${statistics.dictionarySize} слов | ${statistics.percentsOfCorrectAnswers}%"
        )
    }
    if (data == CALLBACK_DATA_LEARN_BUTTON) {
        telegramBot.checkNextQuestionAndSend(trainer, chatId)
    }
    if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
        val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
        if (trainer.checkUserAnswer(userAnswerIndex)) {
            telegramBot.sendMessage(chatId, "Правильно!")
        } else {
            telegramBot.sendMessage(
                chatId,
                "Неправильно! ${trainer.question?.wordForLearning?.original} - это ${trainer.question?.wordForLearning?.translate}"
            )
        }
        telegramBot.checkNextQuestionAndSend(trainer, chatId)
    }

    if (data == RESET_CLICKED) {
        trainer.resetProgress()
        telegramBot.sendMessage(chatId, "Прогресс сброшен")
    }

}