package com.example.mengyueli.placebook.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.mengyueli.placebook.model.Bookmark

// 1
@Database(entities = arrayOf(Bookmark::class), version = 2)
abstract class PlaceBookDatabase : RoomDatabase() {
    // 2
    abstract fun bookmarkDao(): BookmarkDao
    // 3
    companion object {
        // 4
        private var instance: PlaceBookDatabase? = null
        // 5
        fun getInstance(context: Context): PlaceBookDatabase {
            if (instance == null) {
                // 6
                instance = Room.databaseBuilder(
                        context.applicationContext,
                        PlaceBookDatabase::class.java,
                        "PlaceBook")
                        .fallbackToDestructiveMigration()
                        .build()
            }
// 7
            return instance as PlaceBookDatabase
        }
    }
}