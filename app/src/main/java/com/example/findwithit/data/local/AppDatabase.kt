package com.example.findwithit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.findwithit.insurance.dao.InsuranceDao
import com.example.findwithit.insurance.model.InsuranceCostumer

@Database(entities = [CustomerInquiry::class, InsuranceCostumer::class], version = 4, exportSchema = false)
    abstract class AppDatabase : RoomDatabase() {

        abstract fun inquiryDao(): InquiryDao
        abstract fun insuranceDao(): InsuranceDao

    companion object {
        @Volatile  var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dream_homes_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }



}
