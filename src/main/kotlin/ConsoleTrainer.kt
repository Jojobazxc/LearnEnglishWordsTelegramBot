import java.lang.Exception

fun Question.asConsoleString(): String {
    val answers = this.answers
        .mapIndexed { index, word: Word -> " ${index + 1} - ${word.translate}" }
        .joinToString("\n")
    return this.wordForLearning.original + "\n" + answers + "\n 0 - выйти в меню"
}

fun main() {

    val trainer = try {
        LearnWordsTrainer(countOfAnswers = 4, boundaryForLearnedWords = 3)
    } catch (e: Exception) {
        println("Невозможно загрузить словарь")
        return
    }

    while (true) {
        println("Меню: 1 – Учить слова, 2 – Статистика, 0 – Выход")
        when (readln().toIntOrNull()) {
            1 -> {
                while (true) {
                    val question = trainer.getNextQuestion()

                    if (question == null) {
                        println("Вы выучили все слова")
                        break
                    }

                    println(question.asConsoleString())
                    when (val answerOfUser = readln().toIntOrNull()) {
                        0 -> break
                        in 1..trainer.countOfAnswers -> {
                            if (trainer.checkUserAnswer(answerOfUser?.minus(1))) {
                                println("Правильно!")
                            } else println("Неверно! ${question.wordForLearning.original} - это ${question.wordForLearning.translate}")
                        }

                        else -> println("Введите существующий вариант ответа")
                    }
                }
            }

            2 -> {
                val statistics = trainer.getStatistics()
                println("Выучено ${statistics.quantityOfLearnedWords.size} из ${statistics.dictionarySize} слов | ${statistics.percentsOfCorrectAnswers}%")
            }

            0 -> return
            else -> println("Введите существующий вариант ответа")
        }
    }

}
