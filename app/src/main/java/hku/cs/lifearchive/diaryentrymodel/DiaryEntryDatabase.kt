package hku.cs.lifearchive.diaryentrymodel

import android.content.Context
import android.location.Location
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.Date

const val DATABASE_NAME = "diary"

// Use example:
// val diaryEntryDao = DiaryEntryDatabase.getDatabase(this@MainActivity).dao()
// val entries = diaryEntryDao.getAll()
// For supported functions of the dao, see interface DiaryEntryDao {...}

@Entity
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String? = null,
    @ColumnInfo(name = "picture_paths") val picturePaths: List<String> = emptyList(),
    @ColumnInfo(name = "voice_recording_path") val voiceRecording: String? = null,
    @ColumnInfo(name = "ar_video_path") val arVideoPath: String? = null,
    @ColumnInfo(name = "location") val location: Location? = null,
    @ColumnInfo(name = "date") val date: Date = Date()
)

@Dao
interface DiaryEntryDao {
    @Query("SELECT * FROM DiaryEntry ORDER BY date DESC")
    fun getAll(): List<DiaryEntry>

    @Query("SELECT * FROM DiaryEntry WHERE id IN (:ids) ORDER BY date DESC")
    fun getByIds(vararg ids: Int): List<DiaryEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(entry: DiaryEntry): DiaryEntry

    @Query("DELETE FROM DiaryEntry WHERE id IN (:ids)")
    fun deleteByIds(vararg ids: Int): List<DiaryEntry>
}

@Database(entities = [DiaryEntry::class], version = 1)
abstract class DiaryEntryDatabase : RoomDatabase() {
    abstract fun dao(): DiaryEntryDao

    companion object {
        private var dbInstance: DiaryEntryDatabase? = null

        fun getDatabase(context: Context): DiaryEntryDatabase {
            if (dbInstance == null) {
                dbInstance = Room.databaseBuilder(
                    context.applicationContext,
                    DiaryEntryDatabase::class.java,
                    DATABASE_NAME
                ).build()
            }
            return dbInstance!!
        }
    }
}