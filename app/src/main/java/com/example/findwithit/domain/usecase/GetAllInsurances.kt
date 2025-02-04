package com.example.findwithit.domain.usecase

import com.example.findwithit.data.local.CustomerInquiry
import com.example.findwithit.insurance.model.InsuranceCostumer
import com.example.findwithit.insurance.repository.InsurancePersonImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllInsurances(
    private val repo: InsurancePersonImpl
) {

    fun invoke(): Flow<List<InsuranceCostumer>> = repo.getAllInsurance()


}