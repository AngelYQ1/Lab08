package com.example.lab08
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Task::class], version = 2)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var instance: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                ).fallbackToDestructiveMigration().build()
                instance = newInstance
                newInstance
            }
        }
    }
}