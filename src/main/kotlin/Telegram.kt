fun main(args: Array<String>) {

    var updateId = 0

    val telegramBot = TelegramBotService(args[0])

    val updateIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    val trainer = LearnWordsTrainer(3, 6, "words.txt")
    val statistics = trainer.getStatistics()


    while (true) {
        Thread.sleep(2000)
        val updates = telegramBot.getUpdate(updateId)
        println(updates)

        val matchResultUpdateId = updateIdRegex.find(updates)
        val groupsUpdateId = matchResultUpdateId?.groups
        val updateId2 = groupsUpdateId?.get(1)?.value
        updateId = updateId2?.toInt()?.plus(1) ?: 0
        if (updateId == 0) continue

        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value

        val matchResultId: MatchResult? = chatIdRegex.find(updates)
        val groupsId = matchResultId?.groups
        val id = groupsId?.get(1)?.value

        val data = dataRegex.find(updates)?.groups?.get(1)?.value

        if (text?.lowercase() == "/start" && id != null) {
            telegramBot.sendMenu(id)
        }
        if (data?.lowercase() == CALLBACK_DATA_STATISTICS_BUTTON && id != null) {
            telegramBot.sendMessage(
                id,
                "Выучено ${statistics.quantityOfLearnedWords.size} из ${statistics.dictionarySize} слов | ${statistics.percentsOfCorrectAnswers}%"
            )
        }
        if (data?.lowercase() == CALLBACK_DATA_LEARN_BUTTON && id != null) {
            telegramBot.checkNextQuestionAndSend(trainer, id)
        }
        if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
            val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
            if (trainer.checkUserAnswer(userAnswerIndex)) telegramBot.sendMessage(id, "Правильно!")
            else telegramBot.sendMessage(
                id,
                "Неправильно! ${trainer.question?.wordForLearning?.original} - это ${trainer.question?.wordForLearning?.translate}"
            )
        }

    }

}
