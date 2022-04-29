package com.example.passportroom.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.passportroom.dao.PassportDao
import com.example.passportroom.entity.Passport

@Database(entities = [Passport::class],version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun passportDao(): PassportDao

    companion object {
        private var instance: MyDatabase? = null
        @Synchronized
        fun getInstance(context: Context): MyDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java, "my_db"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()

            }
            return instance!!
        }
    }
}