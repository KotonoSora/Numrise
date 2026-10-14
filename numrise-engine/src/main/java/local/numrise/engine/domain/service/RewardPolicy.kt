package local.numrise.engine.domain.service

/**
 * Domain service: scoring policy for finished games. Pure rules, no ports.
 */
object RewardPolicy {
    fun starsFor(timerSeconds: Int, timeLimit: Int): Int = when {
        timerSeconds > timeLimit * 0.6 -> 3
        timerSeconds > timeLimit * 0.3 -> 2
        else -> 1
    }

    fun rewardFor(stars: Int, isLevelMode: Boolean): Int =
        if (isLevelMode) 20 + (stars * 10) else 10 + (stars * 5)

    fun finalScore(currentScore: Int, timerSeconds: Int): Int =
        currentScore + (timerSeconds * 10)

    fun timeElapsed(timeLimit: Int, timerSeconds: Int): Int =
        timeLimit - timerSeconds
}
