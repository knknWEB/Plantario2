package com.example.plantario2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.plantario2.DateConverter
import com.example.plantario2.dao.PlantDAO
import com.example.plantario2.dao.WateredDAO
import com.example.plantario2.model.Plant
import com.example.plantario2.model.Watered

@Database(entities = [Plant::class, Watered::class], version = 2)
@TypeConverters(DateConverter::class) // dodaj konwerter

abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDAO
    abstract fun wateredDao(): WateredDAO

    companion object {
        @Volatile
        private var INSTANCE: PlantDatabase? = null

        fun getDatabase(context: Context): PlantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlantDatabase::class.java,
                    "plant_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
        fun getInstance(context: Context): PlantDatabase {
            if (INSTANCE == null) {
                synchronized(PlantDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        PlantDatabase::class.java, "plant_database")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}