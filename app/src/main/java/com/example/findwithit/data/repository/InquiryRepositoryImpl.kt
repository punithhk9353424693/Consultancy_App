package com.example.findwithit.data.repository

import android.util.Log
import com.example.findwithit.data.local.CustomerInquiry
import com.example.findwithit.data.local.InquiryDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InquiryRepositoryImpl @Inject constructor(
    private val dao: InquiryDao
) : InquiryRepository {

    override suspend fun addInquiry(inquiry: CustomerInquiry) {
        dao.insertInquiry(inquiry)
    }

    override fun getInquiries(): Flow<List<CustomerInquiry>> {
        return dao.getAllInquiries()
    }

    override fun searchInquiries(query: String): Flow<List<CustomerInquiry>> {
        return dao.searchInquiries(query)
    }

    override suspend fun deleteInquiry(id: Int) {
        dao.deleteInquiry(id)
    }

    override suspend fun updateInquery(inquery: CustomerInquiry) {
        dao.updateInquiry(inquery)
    }

    override suspend fun addAllInqueries(customerInquiry: List<CustomerInquiry>) {
        dao.insertInquiries(customerInquiry)
        Log.d("InquiryRepository", "Inserted data: $customerInquiry")
    }

    override suspend fun getFavoriteCustomers(): List<CustomerInquiry> {
        return dao.getFavoriteCustomers()

    }


}