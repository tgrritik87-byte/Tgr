package com.example.ui.state

data class VoiceParticipant(
    val id: String,
    val name: String,
    val isHost: Boolean = false,
    val isSpeaker: Boolean = false,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val avatarHex: Long = 0xFFFF5E62,
    val isVerified: Boolean = true
)

data class LiveVoiceRoom(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val emoji: String,
    val speakerCount: Int,
    val listenerCount: Int,
    val tags: List<String>,
    val speakers: List<VoiceParticipant>,
    val listeners: List<VoiceParticipant>
)

object VoiceRoomsData {
    val sampleRooms = listOf(
        LiveVoiceRoom(
            id = "voice_speed",
            title = "3-Minute Spark Speed Dating",
            subtitle = "Quick prompt pairs with 180s on the clock. Press Raise Hand to jump on stage!",
            category = "Speed Dating",
            emoji = "⚡",
            speakerCount = 4,
            listenerCount = 48,
            tags = listOf("Fast Match", "1-on-1 Sparks", "Timed"),
            speakers = listOf(
                VoiceParticipant("host_1", "Mia (Host)", isHost = true, isSpeaker = true, isSpeaking = true, avatarHex = 0xFFFF3B6F),
                VoiceParticipant("spk_elena", "Elena Vance", isSpeaker = true, isSpeaking = true, avatarHex = 0xFFFF8359),
                VoiceParticipant("spk_lucas", "Lucas Silva", isSpeaker = true, isSpeaking = false, avatarHex = 0xFFF59E0B),
                VoiceParticipant("spk_user", "You (Jordan)", isSpeaker = true, isSpeaking = false, isMuted = true, avatarHex = 0xFF8B5CF6)
            ),
            listeners = listOf(
                VoiceParticipant("lis_1", "Chloe Dupont", avatarHex = 0xFF06B6D4),
                VoiceParticipant("lis_2", "Daniel Park", avatarHex = 0xFF6366F1),
                VoiceParticipant("lis_3", "Aria Thorne", avatarHex = 0xFFF43F5E),
                VoiceParticipant("lis_4", "Kev W.", avatarHex = 0xFF10B981),
                VoiceParticipant("lis_5", "Tara S.", avatarHex = 0xFFEC4899),
                VoiceParticipant("lis_6", "Liam C.", avatarHex = 0xFF3B82F6)
            )
        ),
        LiveVoiceRoom(
            id = "voice_chill",
            title = "Late Night Chill & Lo-Fi Talks",
            subtitle = "Dim the lights, sip tea, and talk about whatever is on your mind after midnight.",
            category = "Chill Lounge",
            emoji = "🌙",
            speakerCount = 3,
            listenerCount = 34,
            tags = listOf("Lo-Fi", "Relaxed", "Midnight"),
            speakers = listOf(
                VoiceParticipant("host_2", "Julian (Host)", isHost = true, isSpeaker = true, isSpeaking = false, avatarHex = 0xFF8B5CF6),
                VoiceParticipant("spk_maya", "Maya Lin", isSpeaker = true, isSpeaking = true, avatarHex = 0xFFEC4899),
                VoiceParticipant("spk_leo", "Leo Chen", isSpeaker = true, isSpeaking = false, avatarHex = 0xFF06B6D4)
            ),
            listeners = listOf(
                VoiceParticipant("lis_7", "Sophia R.", avatarHex = 0xFFFF7A59),
                VoiceParticipant("lis_8", "Nathan B.", avatarHex = 0xFF10B981)
            )
        ),
        LiveVoiceRoom(
            id = "voice_music",
            title = "Indie Record Club & Concert Recs",
            subtitle = "Playing favorite vinyl tracks and chatting about summer music festivals.",
            category = "Music & Vibe",
            emoji = "🎧",
            speakerCount = 3,
            listenerCount = 22,
            tags = listOf("Indie", "Vinyl", "Festivals"),
            speakers = listOf(
                VoiceParticipant("host_3", "Sam G. (Host)", isHost = true, isSpeaker = true, isSpeaking = true, avatarHex = 0xFF10B981),
                VoiceParticipant("spk_chloe", "Chloe Dupont", isSpeaker = true, isSpeaking = false, avatarHex = 0xFF06B6D4),
                VoiceParticipant("spk_tobi", "Tobi K.", isSpeaker = true, isSpeaking = false, avatarHex = 0xFFF59E0B)
            ),
            listeners = listOf(
                VoiceParticipant("lis_9", "Zack H.", avatarHex = 0xFF6366F1),
                VoiceParticipant("lis_10", "Rachel M.", avatarHex = 0xFFFF3B6F)
            )
        )
    )
}
