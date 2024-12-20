package com.example.findwithit.domain.usecase

import com.example.findwithit.data.local.CustomerInquiry
import com.example.findwithit.data.repository.InquiryRepository
import kotlinx.coroutines.flow.Flow


class GetInquiriesUseCase(private val repository: InquiryRepository) {
    fun invoke(): Flow<List<CustomerInquiry>> = repository.getInquiries()
}