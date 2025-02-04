package com.example.findwithit.di

import com.example.findwithit.data.local.InquiryDao
import com.example.findwithit.data.repository.InquiryRepository
import com.example.findwithit.data.repository.InquiryRepositoryImpl
import com.example.findwithit.insurance.dao.InsuranceDao
import com.example.findwithit.insurance.repository.InsurancePerson
import com.example.findwithit.insurance.repository.InsurancePersonImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideInquiryRepository(dao: InquiryDao): InquiryRepository {
        return InquiryRepositoryImpl(dao)
    }
    @Provides
    @Singleton
    fun provideInsuranceRepository(dao: InsuranceDao): InsurancePerson {
        return InsurancePersonImpl(dao)
    }


}