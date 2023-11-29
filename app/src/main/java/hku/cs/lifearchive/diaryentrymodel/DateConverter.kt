package hku.cs.lifearchive.diaryentrymodel

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {
    @TypeConverter
    fun dateToLong(date: Date): Long {
        return date.time
    }
    @TypeConverter
    fun longToDate(long: Long): Date {
        return Date(long)
    }
}