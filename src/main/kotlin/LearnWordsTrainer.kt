import java.io.File

data class Statistics(
    val quantityOfLearnedWords: List<Word>,
    val dictionarySize: Int,
    val percentsOfCorrectAnswers: Int,
)

data class Question(
    val listOfUnlearnedWords: List<Word>,
    val answers: List<Word>,
    val wordForLearning: Word
)

class LearnWordsTrainer(private val boundaryForLearnedWords: Int, val countOfAnswers: Int, private val nameOfTextFile: String) {

    private val dictionary = loadDictionary()
    private var question: Question? = null

    fun getStatistics(): Statistics {
        val quantityOfLearnedWords = dictionary.filter { it.countOfCorrectAnswer >= boundaryForLearnedWords }
        val dictionarySize = dictionary.size
        val percentsOfCorrectAnswers =
            ((quantityOfLearnedWords.size.toDouble() / dictionarySize) * 100).toInt()
        return Statistics(
            quantityOfLearnedWords,
            dictionarySize,
            percentsOfCorrectAnswers
        )
    }

    fun getNextQuestion(): Question? {
        val listOfUnlearnedWords = dictionary.filter { it.countOfCorrectAnswer < boundaryForLearnedWords }
        var answers = listOfUnlearnedWords.shuffled().take(countOfAnswers)
        if (listOfUnlearnedWords.isEmpty()) return null
        if ((answers.size <= countOfAnswers)) {
            answers = answers + dictionary
                .filter { it.countOfCorrectAnswer > boundaryForLearnedWords }
                .shuffled()
                .take(countOfAnswers - answers.size)
        }
        val wordForLearning = answers.random()
        question = Question(
            listOfUnlearnedWords,
            answers,
            wordForLearning
        )
        return question
    }

    fun checkUserAnswer(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.answers.indexOf(it.wordForLearning)
            return if (correctAnswerId == (userAnswerIndex)) {
                it.wordForLearning.countOfCorrectAnswer++
                saveDictionary(dictionary)
                true
            } else false
        } ?: false
    }

    private fun loadDictionary(): List<Word> {
        val wordsFile = File(nameOfTextFile)

        val lines = wordsFile.readLines()
        val dictionary: MutableList<Word> = mutableListOf()
        for (line in lines) {
            if ((line.count { it == '|' } == 2)){
                val line = line.split("|")
                val word = Word(line[0], line[1], line.get(2).toIntOrNull()?:0)
                dictionary.add(word)
            }
            else {
                val copyLine = "$line|"
                val parsingList = copyLine.split('|')
                val word = Word(parsingList[0], parsingList[1], parsingList.get(2).toIntOrNull()?:0)
                dictionary.add(word)
            }
        }
        return dictionary
    }

    private fun saveDictionary(dictionary: List<Word>) {
        val dictionaryFile = File(nameOfTextFile)

        dictionaryFile.writeText("")
        for (word in dictionary) {
            val stringForWrite = "${word.original}|${word.translate}|${word.countOfCorrectAnswer}\n"
            dictionaryFile.appendText(stringForWrite)
        }

    }
}