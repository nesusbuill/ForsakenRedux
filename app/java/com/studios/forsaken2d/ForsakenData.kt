package com.studios.forsaken2d

import android.content.Context

data class Ability(val name: String, val cooldown: Float, val type: Int)
data class Skin(val name: String, val color: Int)
data class Char(val id: String, val name: String, val isKiller: Boolean, val hp: Float, val speed: Float, val radius: Float, val skins: List<Skin>, val abilities: List<Ability>)
data class Obstacle(val x: Float, val y: Float, val w: Float, val h: Float)
data class Generator(var x: Float, var y: Float, var progress: Float = 0f, var isCompleted: Boolean = false, val radius: Float = 40f)

data class GameMap(
    val name: String, val bgColor: Int, val width: Float, val height: Float,
    val obstacles: List<Obstacle>, val generators: List<Generator>,
    val playerSpawn: Pair<Float, Float>, val killerSpawn: Pair<Float, Float>
)

object DB {
    val killers = listOf(
        Char("slasher", "The Slasher", true, 1250f, 7f, 45f, listOf(
            Skin("Default", 0xFFFFFFFF.toInt()), Skin("Hockey", 0xFFBDBDBD.toInt()), Skin("Vanity", 0xFFFFFFFF.toInt())
        ), listOf(Ability("Slash", 2f, 1), Ability("Rage", 30f, 2))),
        Char("c00lkidd", "c00lkidd", true, 800f, 6.5f, 40f, listOf(
            Skin("Default", 0xFFF44336.toInt()), Skin("Bluudud", 0xFF2196F3.toInt()), Skin("Diva", 0xFFE91E63.toInt())
        ), listOf(Ability("Punch", 2f, 1), Ability("Dash", 12f, 2))),
        Char("johndoe", "John Doe", true, 2300f, 7.5f, 50f, listOf(
            Skin("Default", 0xFFFFC107.toInt()), Skin("Annihilation", 0xFF000000.toInt())
        ), listOf(Ability("Slash", 1.75f, 1), Ability("Buff", 18f, 2)))
    )

    val survivors = listOf(
        Char("noob", "Noob", false, 100f, 11f, 40f, listOf(
            Skin("Classic", 0xFFFFEB3B.toInt()), Skin("Fancy", 0xFF9C27B0.toInt()), Skin("Genesis", 0xFFFFFFFF.toInt())
        ), listOf(Ability("Cola", 10f, 2), Ability("Slateskin", 65f, 3))),
        Char("shedletsky", "Shedletsky", false, 110f, 10f, 42f, listOf(
            Skin("Modern", 0xFF2196F3.toInt()), Skin("Golden", 0xFFFFD700.toInt())
        ), listOf(Ability("Sword", 40f, 1), Ability("Chicken", 70f, 3))),
        Char("007n7", "007n7", false, 100f, 11f, 40f, listOf(
            Skin("Default", 0xFF4CAF50.toInt()), Skin("118o8", 0xFF2196F3.toInt())
        ), listOf(Ability("Clone", 40f, 2))),
        Char("chance", "Chance", false, 80f, 11f, 40f, listOf(
            Skin("Golden", 0xFFFFD700.toInt()), Skin("Agent", 0xFF000000.toInt())
        ), listOf(Ability("Gun", 40f, 1), Ability("Reroll", 0f, 3))),
        Char("janedoe", "Jane Doe", false, 100f, 10f, 42f, listOf(
            Skin("Casual", 0xFFFFFFFF.toInt()), Skin("Masquerade", 0xFF9C27B0.toInt())
        ), listOf(Ability("Hatchet", 15f, 1)))
    )

    fun getRandomMap(): GameMap {
        val maps = listOf(
            GameMap("Glass Houses", 0xFF1A2A3A.toInt(), 2000f, 1200f, 
                listOf(Obstacle(400f, 300f, 500f, 40f), Obstacle(400f, 300f, 40f, 400f), Obstacle(1200f, 700f, 400f, 40f)), 
                listOf(Generator(500f, 200f), Generator(1500f, 800f), Generator(800f, 900f), Generator(300f, 800f), Generator(1600f, 300f)), 
                1000f to 600f, 1800f to 1100f
            ),
            GameMap("Underground War", 0xFF2A1A0A.toInt(), 2000f, 1200f, 
                listOf(Obstacle(200f, 200f, 400f, 40f), Obstacle(700f, 400f, 40f, 500f), Obstacle(1000f, 300f, 400f, 40f), Obstacle(500f, 800f, 40f, 300f)), 
                listOf(Generator(400f, 400f), Generator(1200f, 200f), Generator(800f, 700f), Generator(1600f, 900f), Generator(300f, 950f)), 
                1000f to 600f, 1800f to 1100f
            ),
            GameMap("Horror Hotel", 0xFF1A0A0A.toInt(), 2000f, 1200f, 
                listOf(Obstacle(100f, 100f, 1800f, 30f), Obstacle(100f, 1070f, 1800f, 30f), Obstacle(100f, 100f, 30f, 1000f), Obstacle(1870f, 100f, 30f, 1000f), Obstacle(600f, 200f, 30f, 500f), Obstacle(1200f, 500f, 30f, 600f)), 
                listOf(Generator(300f, 300f), Generator(1500f, 900f), Generator(900f, 400f), Generator(1700f, 300f), Generator(400f, 900f)), 
                1000f to 600f, 1800f to 1100f
            )
        )
        return maps.random()
    }
}

class LMSManager {
    fun getLMS(kId: String, sId: String, sSkin: String): Pair<Float, String> {
        if (setOf(kId, sId) == setOf("c00lkidd", "007n7")) return 90f to "lms_plead"
        if (setOf(kId, sId) == setOf("johndoe", "janedoe")) return 100f to "lms_you_and_i"
        if (sSkin == "Golden") return 70f to "lms_default"
        return 70f to "lms_default"
    }
}

class MatchManager {
    var timeLeft = 160f
    var isLMS = false
    var isOver = false
    var lmsTheme = ""
    var malice = 0

    fun start(survivorCount: Int, kId: String, sId: String, sSkin: String, context: Context) {
        isOver = false
        malice = 0
        if (survivorCount == 1) {
            isLMS = true
            val lms = LMSManager().getLMS(kId, sId, sSkin)
            timeLeft = lms.first
            lmsTheme = lms.second.uppercase()
            SoundManager.playLMSMusic(context, lms.second)
        } else {
            isLMS = false
            timeLeft = 160f 
            SoundManager.stopMusic()
        }
    }

    fun onGeneratorDone() {
        malice++
        timeLeft -= 2f 
    }

    fun update(dt: Float, currentSurvivors: Int, kId: String, sId: String, sSkin: String, context: Context): String {
        if (isOver) return ""
        
        if (!isLMS && currentSurvivors == 1) {
            isLMS = true
            val lms = LMSManager().getLMS(kId, sId, sSkin)
            timeLeft = lms.first
            lmsTheme = lms.second.uppercase()
            SoundManager.playLMSMusic(context, lms.second)
            return "LMS_TRIGGERED"
        }

        timeLeft -= dt
        if (timeLeft <= 0) {
            timeLeft = 0f
            isOver = true
            return "SURVIVOR_WIN"
        }
        return ""
    }

    fun formatTime(): String {
        return String.format("%d:%02d", (timeLeft / 60).toInt(), (timeLeft % 60).toInt())
    }
}
