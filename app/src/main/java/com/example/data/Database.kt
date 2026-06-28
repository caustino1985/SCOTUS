package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "exhibit_notes")
data class ExhibitNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val documentId: String,
    val noteText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false,
    val userId: String? = null
)

@Dao
interface ExhibitNoteDao {
    @Query("SELECT * FROM exhibit_notes WHERE documentId = :documentId ORDER BY timestamp DESC")
    fun getNotesForDocument(documentId: String): Flow<List<ExhibitNote>>

    @Query("SELECT * FROM exhibit_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<ExhibitNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: ExhibitNote)

    @Update
    suspend fun updateNote(note: ExhibitNote)

    @Query("DELETE FROM exhibit_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)
}

@Database(entities = [ExhibitNote::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exhibitNoteDao(): ExhibitNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "scotus_exhibition_db"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
