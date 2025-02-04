package com.example.findwithit.data.local

import androidx.room.TypeConverter
import java.util.Date

class DataConverter {
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    // Convert Long to Date
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}