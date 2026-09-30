package com.example.data

import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.CommunityMessageEntity
import com.example.data.local.entities.CommunityRoomEntity
import com.example.data.local.entities.DatingProfileEntity
import com.example.data.local.entities.MatchEntity
import com.example.data.local.entities.UserProfileEntity

object InitialData {
    val defaultUser = UserProfileEntity(
        userId = "current_user",
        name = "Jordan Rivera",
        age = 25,
        bio = "Spatial designer & coffee enthusiast. Seeking someone who enjoys spontaneous weekend road trips, indie concerts, and deep late-night conversations.",
        occupation = "Spatial & Product Designer",
        location = "San Francisco, CA",
        isVerified = true,
        verificationPose = "Selfie Tilt & Smile Verified",
        maxDistanceKm = 35,
        minAge = 21,
        maxAge = 32,
        verifiedOnly = false,
        incognitoMode = false,
        interests = "Design, Specialty Coffee, Film Photography, Bouldering, Indie Pop, Hiking"
    )

    val profiles = listOf(
        DatingProfileEntity(
            id = "profile_elena",
            name = "Elena Vance",
            age = 24,
            gender = "Woman",
            distanceKm = 3,
            locationName = "Mission District, SF",
            occupation = "Product Designer",
            companyOrSchool = "Airbnb",
            isVerified = true,
            verificationMethod = "Selfie Pose Verified",
            bio = "Always on the hunt for the city's crispest pour-over. When not sketching layouts, you'll find me on coastal trails with a 35mm camera.",
            prompts = "The hallmark of a great date is...||Finding a rooftop with warm tea and talking until the streetlights turn off.///Two truths and a lie...||I've solo hiked Mt. Rainier, I speak 3 languages, I hate avocados.///My favorite weekend tradition...||Morning farmer's market followed by making fresh sourdough.",
            interests = "Design, Specialty Coffee, Film Cameras, Hiking, Bossa Nova",
            relationshipIntent = "Long-term relationship",
            avatarDrawable = "img_avatar_elena",
            gradientStartHex = 0xFFFF4B72,
            gradientEndHex = 0xFFFF8359,
            likedCurrentUser = true,
            heightCm = 168,
            zodiac = "Libra",
            pets = "Cat owner (Mochi)"
        ),
        DatingProfileEntity(
            id = "profile_maya",
            name = "Maya Lin",
            age = 25,
            gender = "Woman",
            distanceKm = 5,
            locationName = "Hayes Valley, SF",
            occupation = "Sound Designer & Composer",
            companyOrSchool = "Warner Music Group",
            isVerified = true,
            verificationMethod = "Selfie Biometric Check",
            bio = "I turn everyday city noises into ambient beats. Huge vinyl collector and amateur mixologist. Tell me your top 3 desert-island albums.",
            prompts = "We will get along if...||You appreciate lo-fi vinyl records and spontaneous 1 AM noodle runs.///My most irrational fear...||Leaving my house without headphones.///Together we could...||Explore that underground jazz bar downtown.",
            interests = "Indie Rock, Vinyl Records, Tonkotsu Ramen, Synthesizers, Night Walks",
            relationshipIntent = "Long-term relationship",
            avatarDrawable = "gradient_violet",
            gradientStartHex = 0xFF8B5CF6,
            gradientEndHex = 0xFFEC4899,
            likedCurrentUser = true,
            heightCm = 165,
            zodiac = "Pisces",
            pets = "Dog lover"
        ),
        DatingProfileEntity(
            id = "profile_lucas",
            name = "Lucas Silva",
            age = 27,
            gender = "Man",
            distanceKm = 7,
            locationName = "Marina, SF",
            occupation = "Architectural Photographer",
            companyOrSchool = "Studio Silva",
            isVerified = true,
            verificationMethod = "Government ID Verified",
            bio = "Capturing geometric shadows and brutalist concrete. Weekend cyclist, espresso snob, and always ready for a road trip up Highway 1.",
            prompts = "Together we could...||Hunt down the best hidden speakeasies and photograph neon reflections.///I'm surprisingly good at...||Baking authentic rosemary focaccia from scratch.",
            interests = "Photography, Architecture, Road Trips, Cycling, Italian Food",
            relationshipIntent = "Open to explore",
            avatarDrawable = "gradient_amber",
            gradientStartHex = 0xFFF59E0B,
            gradientEndHex = 0xFFEF4444,
            likedCurrentUser = false,
            heightCm = 183,
            zodiac = "Sagittarius",
            pets = "Golden Retriever dad"
        ),
        DatingProfileEntity(
            id = "profile_chloe",
            name = "Chloe Dupont",
            age = 23,
            gender = "Woman",
            distanceKm = 4,
            locationName = "Ocean Beach, SF",
            occupation = "Marine Biologist & Diver",
            companyOrSchool = "Oceanic Research Lab",
            isVerified = true,
            verificationMethod = "Selfie Pose Verified",
            bio = "Happiest under 20 meters of ocean water. Passionate about kelp forest restoration, cold plunges, and sunset bonfires with marshmallows.",
            prompts = "A shower thought I had recently...||Octopuses literally have three hearts, so they have 3x the capacity to love.///Teach me something about...||Your absolute favorite secret neighborhood gem.",
            interests = "Scuba Diving, Ocean Conservation, Surfing, Bonfires, Podcasts",
            relationshipIntent = "Long-term relationship",
            avatarDrawable = "gradient_teal",
            gradientStartHex = 0xFF06B6D4,
            gradientEndHex = 0xFF3B82F6,
            likedCurrentUser = true,
            heightCm = 170,
            zodiac = "Cancer",
            pets = "Plants only"
        ),
        DatingProfileEntity(
            id = "profile_daniel",
            name = "Daniel Park",
            age = 28,
            gender = "Man",
            distanceKm = 9,
            locationName = "South Park, SF",
            occupation = "Senior AI Engineer",
            companyOrSchool = "Stripe",
            isVerified = true,
            verificationMethod = "Selfie Pose Verified",
            bio = "Solving complex math during the week, bouldering V6s and perfecting matcha lattes on the weekend. Let's play chess or grab boba.",
            prompts = "My green flags are...||Always having a phone charger and texting back within 5 minutes.///My ideal Sunday...||Early morning bouldering, iced matcha, reading sci-fi in the park.",
            interests = "Bouldering, Matcha, Chess, Sci-Fi Novels, Tech, Cats",
            relationshipIntent = "Long-term relationship",
            avatarDrawable = "gradient_indigo",
            gradientStartHex = 0xFF6366F1,
            gradientEndHex = 0xFFA855F7,
            likedCurrentUser = true,
            heightCm = 178,
            zodiac = "Taurus",
            pets = "Tuxedo cat named Pixel"
        ),
        DatingProfileEntity(
            id = "profile_aria",
            name = "Aria Thorne",
            age = 26,
            gender = "Woman",
            distanceKm = 12,
            locationName = "North Beach, SF",
            occupation = "Ceramics Artist & Curator",
            companyOrSchool = "Atelier Thorne",
            isVerified = false,
            verificationMethod = "Unverified",
            bio = "Handmade cups, earthy glazes, and museum afternoons. Looking for someone who doesn't mind a little clay dust on their clothes.",
            prompts = "One thing you should know about me...||I will probably give you a handcrafted mug on our second date.///Best travel story...||Getting lost in Kyoto looking for vintage pottery tools.",
            interests = "Ceramics, Art Museums, Thrift Shopping, Tea Ceremonies, Film",
            relationshipIntent = "Casual dating",
            avatarDrawable = "gradient_rose",
            gradientStartHex = 0xFFF43F5E,
            gradientEndHex = 0xFFBE185D,
            likedCurrentUser = false,
            heightCm = 167,
            zodiac = "Virgo",
            pets = "No pets"
        )
    )

    val communityRooms = listOf(
        CommunityRoomEntity(
            roomId = "room_coffee",
            title = "Coffee & Late Night Talks",
            category = "Lifestyle & Chill",
            description = "Cozy audio and chat hub for caffeine lovers, bookworms, and midnight musings.",
            emoji = "☕",
            activeUsersCount = 42,
            tags = "Coffee, Books, Relaxed, Late-Night"
        ),
        CommunityRoomEntity(
            roomId = "room_speed_dating",
            title = "3-Minute Spark Dating",
            category = "Fast Match",
            description = "Fast-paced, low-pressure 3-minute prompts to break the ice and see who sparks instant chemistry!",
            emoji = "⚡",
            activeUsersCount = 68,
            tags = "Speed Dating, Icebreakers, Audio, Live"
        ),
        CommunityRoomEntity(
            roomId = "room_music",
            title = "Indie Jams & Vinyl Vibes",
            category = "Music & Arts",
            description = "Drop your currently listening track, share live concert plans, and connect over favorite chords.",
            emoji = "🎵",
            activeUsersCount = 29,
            tags = "Indie, Concerts, Vinyl, Soundtracks"
        ),
        CommunityRoomEntity(
            roomId = "room_foodies",
            title = "Secret Food Spots & Dates",
            category = "Food & Drinks",
            description = "Discover the most romantic speakeasies, hole-in-the-wall noodle bars, and dessert spots.",
            emoji = "🍜",
            activeUsersCount = 54,
            tags = "Foodies, Speakeasies, Dates, Cocktails"
        ),
        CommunityRoomEntity(
            roomId = "room_outdoors",
            title = "Hiking & Weekend Adventures",
            category = "Active & Outdoors",
            description = "Find buddies for sunrise coastal hikes, bouldering gyms, and weekend campouts.",
            emoji = "🌲",
            activeUsersCount = 37,
            tags = "Hiking, Camping, Bouldering, Fitness"
        )
    )

    val initialCommunityMessages = listOf(
        CommunityMessageEntity(
            roomId = "room_coffee",
            senderName = "Elena Vance",
            senderAvatarHex = 0xFFFF4B72,
            senderIsVerified = true,
            content = "Anyone checked out the new pour-over spot on 18th? Their Ethiopian beans are incredible.",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 18,
            likesCount = 5
        ),
        CommunityMessageEntity(
            roomId = "room_coffee",
            senderName = "Daniel Park",
            senderAvatarHex = 0xFF6366F1,
            senderIsVerified = true,
            content = "Yes! Tried their natural process anaerobic roast yesterday. 10/10 recommendation.",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 10,
            likesCount = 3
        ),
        CommunityMessageEntity(
            roomId = "room_coffee",
            senderName = "Maya Lin",
            senderAvatarHex = 0xFF8B5CF6,
            senderIsVerified = true,
            content = "Added to my Sunday morning to-do list! Who else loves working from indie cafes with ambient jazz?",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 4,
            likesCount = 7
        ),
        CommunityMessageEntity(
            roomId = "room_speed_dating",
            senderName = "Chloe Dupont",
            senderAvatarHex = 0xFF06B6D4,
            senderIsVerified = true,
            content = "Quick prompt: If you could teleport to any beach in the world right now, where are we heading? 🌊",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 12,
            likesCount = 9
        ),
        CommunityMessageEntity(
            roomId = "room_music",
            senderName = "Lucas Silva",
            senderAvatarHex = 0xFFF59E0B,
            senderIsVerified = true,
            content = "Anyone caught the Khruangbin show at the Greek Theatre? Sound was unbelievable.",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 25,
            likesCount = 4
        )
    )

    // Pre-seeded match with Elena to demonstrate mutual-match-only chat instantly
    val initialMatch = MatchEntity(
        matchId = "match_elena",
        profileId = "profile_elena",
        matchedAt = System.currentTimeMillis() - 1000 * 60 * 60 * 3, // 3 hours ago
        lastMessage = "I love that photo of your bouldering trip! Which gym do you go to?",
        lastMessageTimestamp = System.currentTimeMillis() - 1000 * 60 * 15,
        isNewMatch = false,
        unreadCount = 1
    )

    val initialMessages = listOf(
        ChatMessageEntity(
            messageId = "msg_1",
            matchId = "match_elena",
            senderId = "profile_elena",
            text = "Hey Jordan! ✨ Loved your prompt about coastal road trips. I just drove down to Big Sur last weekend!",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 90,
            isRead = true
        ),
        ChatMessageEntity(
            messageId = "msg_2",
            matchId = "match_elena",
            senderId = "me",
            text = "Hey Elena! That's awesome, Big Sur is magical. Did you take any 35mm shots out there?",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 45,
            isRead = true
        ),
        ChatMessageEntity(
            messageId = "msg_3",
            matchId = "match_elena",
            senderId = "profile_elena",
            text = "I love that photo of your bouldering trip! Which gym do you go to?",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 15,
            isRead = false
        )
    )
}
