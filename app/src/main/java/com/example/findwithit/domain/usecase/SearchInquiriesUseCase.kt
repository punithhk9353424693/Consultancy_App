package com.example.findwithit.domain.usecase


import com.example.findwithit.data.local.CustomerInquiry
import com.example.findwithit.data.repository.InquiryRepository
import kotlinx.coroutines.flow.Flow

class SearchInquiriesUseCase(private val repository: InquiryRepository) {
    fun invoke(query: String): Flow<List<CustomerInquiry>> = repository.searchInquiries(query)
}