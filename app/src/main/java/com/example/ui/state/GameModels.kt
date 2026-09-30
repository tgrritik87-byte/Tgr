package com.example.ui.state

data class WouldYouRatherCard(
    val id: Int,
    val optionA: String,
    val optionB: String,
    val votesA: Int = 68,
    val votesB: Int = 32,
    val category: String = "Lifestyle"
)

data class TruthOrDareItem(
    val id: Int,
    val type: String, // "TRUTH" or "DARE"
    val level: String, // "Icebreaker", "Romantic", "Deep"
    val prompt: String
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>
)

object DatingGamesData {
    val wouldYouRatherList = listOf(
        WouldYouRatherCard(
            id = 1,
            optionA = "First date at a hidden rooftop speakeasy",
            optionB = "First date at a cozy indie bookstore & cafe",
            votesA = 58,
            votesB = 42,
            category = "First Date"
        ),
        WouldYouRatherCard(
            id = 2,
            optionA = "Spontaneous weekend road trip with zero hotel bookings",
            optionB = "Meticulously planned all-inclusive beach resort",
            votesA = 64,
            votesB = 36,
            category = "Travel Vibe"
        ),
        WouldYouRatherCard(
            id = 3,
            optionA = "Cook an elaborate 4-course dinner together from scratch",
            optionB = "Order takeout from 3 different spots and binge a series",
            votesA = 71,
            votesB = 29,
            category = "Date Night"
        ),
        WouldYouRatherCard(
            id = 4,
            optionA = "Share an apartment with 3 golden retriever puppies",
            optionB = "Share an apartment with 3 lazy cuddly ragdoll cats",
            votesA = 53,
            votesB = 47,
            category = "Pets"
        ),
        WouldYouRatherCard(
            id = 5,
            optionA = "Have honest conversations late at night on a balcony",
            optionB = "Have deep conversations while driving on an open highway",
            votesA = 62,
            votesB = 38,
            category = "Intimacy"
        )
    )

    val truthOrDareList = listOf(
        TruthOrDareItem(
            id = 1,
            type = "TRUTH",
            level = "Icebreaker",
            prompt = "What was the most awkward or hilarious dating moment you've ever had?"
        ),
        TruthOrDareItem(
            id = 2,
            type = "TRUTH",
            level = "Romantic",
            prompt = "What's an immediate green flag that makes you instantly interested in someone?"
        ),
        TruthOrDareItem(
            id = 3,
            type = "DARE",
            level = "Icebreaker",
            prompt = "Send a 10-second voice note singing the chorus of your favorite shower song."
        ),
        TruthOrDareItem(
            id = 4,
            type = "TRUTH",
            level = "Deep",
            prompt = "What is a passion project or secret dream you rarely share with strangers?"
        ),
        TruthOrDareItem(
            id = 5,
            type = "DARE",
            level = "Romantic",
            prompt = "Give your most charming 1-sentence compliment to your match right now."
        ),
        TruthOrDareItem(
            id = 6,
            type = "TRUTH",
            level = "Romantic",
            prompt = "Do you believe in love at first sight, or love after deep conversation?"
        )
    )

    val compatibilityQuestions = listOf(
        QuizQuestion(
            id = 1,
            question = "What does your ideal Saturday look like?",
            options = listOf(
                "Sunrise hike & specialty iced coffee",
                "Sleeping in, brunch & wandering art galleries",
                "Thrifting, farmers markets & cooking",
                "Gym, gaming session & hanging with friends"
            )
        ),
        QuizQuestion(
            id = 2,
            question = "What is your primary Love Language?",
            options = listOf(
                "Quality Time & uninterrupted presence",
                "Words of Affirmation & sweet messages",
                "Physical Touch & gentle affection",
                "Acts of Service & thoughtful gestures"
            )
        ),
        QuizQuestion(
            id = 3,
            question = "How do you prefer to handle disagreements?",
            options = listOf(
                "Talk it through calmly right away",
                "Take a 15-minute breather, then discuss gently",
                "Write thoughts down to articulate clearly",
                "Use gentle humor to defuse before talking"
            )
        ),
        QuizQuestion(
            id = 4,
            question = "What's your dream vacation style?",
            options = listOf(
                "Spontaneous road trip with a tent and camera",
                "Relaxing seaside villa with books and wine",
                "Dense metropolitan city hopping food stalls",
                "Winter cabin with a fireplace and hot tub"
            )
        ),
        QuizQuestion(
            id = 5,
            question = "In a relationship, what matters most to you?",
            options = listOf(
                "Deep emotional safety & mutual trust",
                "Shared humor & never taking life too seriously",
                "Inspiring each other's goals and growth",
                "Spontaneous adventures and daily excitement"
            )
        )
    )
}
