package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.DatingDao
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.CommunityMessageEntity
import com.example.data.local.entities.CommunityRoomEntity
import com.example.data.local.entities.DatingProfileEntity
import com.example.data.local.entities.MatchEntity
import com.example.data.local.entities.SafetyReportEntity
import com.example.data.local.entities.UserProfileEntity

@Database(
    entities = [
        DatingProfileEntity::class,
        MatchEntity::class,
        ChatMessageEntity::class,
        CommunityRoomEntity::class,
        CommunityMessageEntity::class,
        SafetyReportEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DatingDatabase : RoomDatabase() {
    abstract fun datingDao(): DatingDao

    companion object {
        @Volatile
        private var INSTANCE: DatingDatabase? = null

        fun getInstance(context: Context): DatingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DatingDatabase::class.java,
                    "spark_dating_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
