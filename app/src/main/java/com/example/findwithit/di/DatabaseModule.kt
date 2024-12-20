package com.example.findwithit.di

import android.content.Context
import androidx.room.Room
import com.example.findwithit.data.local.AppDatabase
import com.example.findwithit.data.local.InquiryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "dream_homes_db"
        ).build()
    }

    @Provides
    fun provideInquiryDao(db: AppDatabase): InquiryDao = db.inquiryDao()
}