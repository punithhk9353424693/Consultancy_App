package com.example.findwithit.domain.usecase

import com.example.findwithit.data.local.CustomerInquiry
import com.example.findwithit.data.repository.InquiryRepository


class AddInquiryUseCase(private val repository: InquiryRepository) {
    suspend operator fun invoke(inquiry: CustomerInquiry) {
        repository.addInquiry(inquiry)
    }
}