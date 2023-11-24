package hku.cs.lifearchive.diaryentrymodel

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

const val DATABASE_NAME = "diary"

// Use example:
// val diaryEntryDao = DiaryEntryDatabase.getDatabase(this@MainActivity).dao()
// val entries = diaryEntryDao.getAll()
// For supported functions of the dao, see interface DiaryEntryDao {...}
inline fun <reified T> Gson.fromJson(json: String) =
    fromJson<T>(json, object : TypeToken<T>() {}.type)

//converters for complex data
class Converters {

    // date converter
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun ListtoJson(value: List<String>?)= Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String): ArrayList<String> = Gson().fromJson<ArrayList<String>>(value)


}


@Entity
data class DiaryEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String? = null,

    @ColumnInfo(name = "picture_paths") val picturePaths: ArrayList<String>,
    @ColumnInfo(name = "voice_recording_path") val voiceRecording: String? = null,
    @ColumnInfo(name = "ar_video_path") val arVideoPath: String? = null,
    @ColumnInfo(name = "longitude") val longitude: Long? = null,
    @ColumnInfo(name = "latitude") val latitude: Long? = null,
    @ColumnInfo(name = "date") val date: Date? = Date()
)

@Dao
interface DiaryEntryDao {
    @Query("SELECT * FROM DiaryEntry ORDER BY date DESC")
    fun getAll(): List<DiaryEntry>

    @Query("SELECT * FROM DiaryEntry WHERE id IN (:ids) ORDER BY date DESC")
    fun getByIds(vararg ids: Int): List<DiaryEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEntry(vararg DiaryEntryDatabase: DiaryEntry)

    @Delete
    fun deleteByIds(vararg DiaryEntryDatabase: DiaryEntry)
}

@Database(entities = [DiaryEntry::class], version = 1)
@TypeConverters(Converters::class)
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