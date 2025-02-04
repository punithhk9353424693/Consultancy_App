package com.example.findwithit.di

import android.content.Context
import androidx.room.Room
import com.example.findwithit.data.local.AppDatabase
import com.example.findwithit.data.local.InquiryDao
import com.example.findwithit.insurance.dao.InsuranceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)

    }


    @Provides
    fun provideInquiryDao(db: AppDatabase): InquiryDao = db.inquiryDao()

    @Provides
    fun provideInsuranceDao(db: AppDatabase): InsuranceDao = db.insuranceDao()
}