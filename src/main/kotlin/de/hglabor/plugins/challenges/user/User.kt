package de.hglabor.plugins.challenges.user

import de.hglabor.plugins.challenges.Challenge
import java.util.*

class User constructor(uuid: UUID, name: String) {
    var inChallenge: Boolean = false
    var soupsEaten: Int = 0
    var soupsDropped: Int = 0
    var hasChallengeCompleted: Boolean = false
    lateinit var currentChallenge: Challenge
}
