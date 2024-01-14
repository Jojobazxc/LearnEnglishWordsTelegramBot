import java.io.File

fun main() {

    val wordsFile = File("words.txt")

    wordsFile.createNewFile()

    val lines = wordsFile.readLines()

    val dictionary: MutableList<Word> = mutableListOf()
    for (line in lines) {
        val line = line.split("|")
        val word = Word(line[0], line[1], line[2].toInt())
        dictionary.add(word)
    }
    println(dictionary)

    val quantityOfCorrectAnswers = dictionary.filter { it.countOfCorrectAnswer >= 3 }
    while (true) {
        println("Меню: 1 – Учить слова, 2 – Статистика, 0 – Выход")
        val choice = readln().toIntOrNull() ?: continue
        when (choice) {
            1 -> {
                do {
                    val listOfUnlearnedWords = dictionary.filter { it.countOfCorrectAnswer <= 3 }
                    if (listOfUnlearnedWords.isEmpty()) println("Вы выучили все слова")
                    else {
                        val wordForLearning = listOfUnlearnedWords.random()
                        val answers = listOfUnlearnedWords.shuffled().take(4)
                        println(wordForLearning.translate)
                        answers.forEach{ println(it.original)}
                        val a = readln()
                    }
                } while (listOfUnlearnedWords.isNotEmpty())

            }

            2 -> {
                val percentsOfCorrectAnswers =
                    ((quantityOfCorrectAnswers.size.toDouble() / dictionary.size) * 100).toInt()
                println("Выучено ${quantityOfCorrectAnswers.size} из ${dictionary.size} слов | ${percentsOfCorrectAnswers}%")
            }

            0 -> return
            else -> println("Введите существующий вариант ответа")
        }
    }

}