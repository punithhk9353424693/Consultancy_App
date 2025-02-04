package com.example.findwithit.insurance.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.findwithit.insurance.model.InsuranceCostumer
import kotlinx.coroutines.flow.Flow

@Dao
interface InsuranceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsurance(insurance: InsuranceCostumer)

    @Query("SELECT * FROM  InsuranceCostumer")
    fun getAllInsurance(): Flow<List<InsuranceCostumer>>

    @Query("SELECT *FROM InsuranceCostumer WHERE ownerName LIKE:query OR mobile Like:query OR regDate LIKE:query")
    fun searchInsurance(query: String): Flow<List<InsuranceCostumer>>

    @Query("DELETE FROM InsuranceCostumer WHERE id=:id")
    suspend fun deleteInsurance(id:Int)

    @Update
    suspend fun updateInsurance(insurance: InsuranceCostumer):Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllInsurance(insurances: List<InsuranceCostumer>)

}