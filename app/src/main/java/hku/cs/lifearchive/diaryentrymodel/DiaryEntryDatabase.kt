package hku.cs.lifearchive.diaryentrymodel

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import java.util.Date

const val DATABASE_NAME = "diary"

// Use example:
// val diaryEntryDao = DiaryEntryDatabase.getDatabase(this@MainActivity).dao()
// val entries = diaryEntryDao.getAll()
// For supported functions of the dao, see interface DiaryEntryDao {...}
class Location {
    var latitude: Double = 0.0
    var longitude: Double = 0.0
}

@Entity
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String? = null,
    @ColumnInfo(name = "picture_paths") val picturePaths: List<String> = emptyList(),
    @ColumnInfo(name = "voice_recording_path") val voiceRecording: String? = null,
    @ColumnInfo(name = "ar_video_path") val arVideoPath: String? = null,
    @Embedded(prefix = "location") val location: Location? = null,
    @ColumnInfo(name = "date") val date: Date = Date()
)

@Dao
interface DiaryEntryDao {
    @Query("SELECT * FROM DiaryEntry ORDER BY date DESC")
    fun getAll(): List<DiaryEntry>

    @Query("SELECT * FROM DiaryEntry WHERE id IN (:ids) ORDER BY date DESC")
    fun getByIds(vararg ids: Int): List<DiaryEntry>

    @Query("SELECT * FROM DiaryEntry WHERE id = (:id)")
    fun getById(id: Int): DiaryEntry

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(entry: DiaryEntry)

    @Query("DELETE FROM DiaryEntry WHERE id IN (:ids)")
    fun deleteById(vararg ids: Int): Int

    @Query("DELETE FROM DiaryEntry WHERE id = (:id)")
    fun deleteById(id: Int): Int
}

@Database(entities = [DiaryEntry::class], version = 1)
@TypeConverters(StringListConverter::class, DateConverter::class)
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
                ).allowMainThreadQueries().build()
            }
            return dbInstance!!
        }
    }
}