package com.example.findwithit.data.repository


import com.example.findwithit.data.local.CustomerInquiry
import kotlinx.coroutines.flow.Flow

interface InquiryRepository {
    suspend fun addInquiry(inquiry: CustomerInquiry)
    fun getInquiries(): Flow<List<CustomerInquiry>>
    fun searchInquiries(query: String): Flow<List<CustomerInquiry>>
    suspend fun deleteInquiry(id: Int)
    suspend fun updateInquery(inquery: CustomerInquiry)
    suspend fun addAllInqueries(customerInquiry: List<CustomerInquiry>)
    suspend fun getFavoriteCustomers(): List<CustomerInquiry>


}
