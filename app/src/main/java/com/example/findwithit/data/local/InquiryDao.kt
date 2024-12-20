package com.example.findwithit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface InquiryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInquiry(inquiry: CustomerInquiry)

    @Query("SELECT * FROM CustomerInquiry ORDER BY createdDateTime DESC")
    fun getAllInquiries(): Flow<List<CustomerInquiry>>

    @Query("SELECT * FROM CustomerInquiry WHERE name LIKE :query OR contactNumber LIKE :query")
    fun searchInquiries(query: String): Flow<List<CustomerInquiry>>

    @Query("DELETE FROM CustomerInquiry WHERE id = :id")
    suspend fun deleteInquiry(id: Int)
}