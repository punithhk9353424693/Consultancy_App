package com.example.findwithit.data.repository


import com.example.findwithit.data.local.CustomerInquiry
import kotlinx.coroutines.flow.Flow

interface InquiryRepository {
    suspend fun addInquiry(inquiry: CustomerInquiry)
    fun getInquiries(): Flow<List<CustomerInquiry>>
    fun searchInquiries(query: String): Flow<List<CustomerInquiry>>
    suspend fun deleteInquiry(id: Int)
}
