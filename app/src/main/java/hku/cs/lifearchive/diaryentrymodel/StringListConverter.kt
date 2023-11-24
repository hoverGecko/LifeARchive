package hku.cs.lifearchive.diaryentrymodel

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class StringListConverter {
    @TypeConverter
    fun listOfStringToJsonString(list: List<String>): String {
        return Json.encodeToString(list)
    }
    @TypeConverter
    fun jsonStringToListOfString(str: String): List<String> {
        return Json.decodeFromString<List<String>>(str)
    }
}