package com.example.findwithit.presentation.home.viewmodel


import com.example.findwithit.data.local.CustomerInquiry
import com.example.findwithit.data.repository.InquiryRepository
import com.example.findwithit.domain.usecase.AddInquiryUseCase
import com.example.findwithit.domain.usecase.GetInquiriesUseCase
import com.example.findwithit.domain.usecase.SearchInquiriesUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class InquiryViewModel @Inject constructor(
    private val addInquiryUseCase:  AddInquiryUseCase,
    private val getInquiriesUseCase: GetInquiriesUseCase,
    private val searchInquiriesUseCase:  SearchInquiriesUseCase,
    private val repository:  InquiryRepository
) : ViewModel() {

    private val _inquiries =
        MutableStateFlow<List<CustomerInquiry>>(emptyList())
    val inquiries: StateFlow<List<CustomerInquiry>> get() = _inquiries

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> get() = _query

    init {
        viewModelScope.launch {
            Flow.collect { inquiries ->
                MutableStateFlow.value = inquiries
            }
        }
    }

    fun addInquiry(inquiry: CustomerInquiry) {
        viewModelScope.launch {
            addInquiryUseCase(inquiry)
        }
    }

    fun searchInquiries(query: String) {
        MutableStateFlow.value = query
        viewModelScope.launch {
            Flow.collect { inquiries ->
                MutableStateFlow.value = inquiries
            }
        }
    }

    fun deleteInquiry(id: Int) {
        viewModelScope.launch {
            repository.deleteInquiry(id)
        }
    }
}