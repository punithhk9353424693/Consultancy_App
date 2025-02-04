package com.example.findwithit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface InquiryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInquiry(inquiry: CustomerInquiry)

    @Query("SELECT * FROM CustomerInquiry ORDER BY createdDateTime DESC")
    fun getAllInquiries(): Flow<List<CustomerInquiry>>

    @Query("SELECT * FROM CustomerInquiry WHERE name LIKE :query OR CAST(contactNumber AS TEXT) LIKE :query OR inquiryCategory LIKE :query OR lookingFor Like:query")
    fun searchInquiries(query: String): Flow<List<CustomerInquiry>>

    @Query("DELETE FROM CustomerInquiry WHERE id = :id")
    suspend fun deleteInquiry(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInquiries(inquiries: List<CustomerInquiry>)

    @Update
    suspend fun updateInquiry(inquiry: CustomerInquiry): Int

    @Query("SELECT * FROM CustomerInquiry WHERE id = :id")
    suspend fun getCustomerById(id: Int): CustomerInquiry?

    @Query("SELECT * FROM CustomerInquiry WHERE isFavorite = 1")
    suspend fun getFavoriteCustomers(): List<CustomerInquiry>
}