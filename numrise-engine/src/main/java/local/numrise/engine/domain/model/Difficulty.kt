package local.numrise.engine.domain.model

enum class Difficulty(val label: String, val maxNumber: Int, val timeLimit: Int) {
    EASY("EASY", 9, 30),
    MEDIUM("MEDIUM", 20, 45),
    HARD("HARD", 50, 55),
    VERY_HARD("VERY HARD", 60, 60)
}
