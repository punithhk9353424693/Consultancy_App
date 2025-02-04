package com.example.findwithit.presentation.home.viewmodel


import android.util.Log
import com.example.findwithit.data.local.CustomerInquiry
import com.example.findwithit.data.repository.InquiryRepository
import com.example.findwithit.domain.usecase.AddInquiryUseCase
import com.example.findwithit.domain.usecase.GetInquiriesUseCase
import com.example.findwithit.domain.usecase.SearchInquiriesUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findwithit.domain.usecase.UpdateInquiryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class InquiryViewModel @Inject constructor(
    private val addInquiryUseCase: AddInquiryUseCase,
    private val getInquiriesUseCase: GetInquiriesUseCase,
    private val searchInquiriesUseCase: SearchInquiriesUseCase,
    private val repository: InquiryRepository,
    private val updateInquiryUseCase: UpdateInquiryUseCase,
) : ViewModel() {

    private val _inquiries =
        MutableStateFlow<List<CustomerInquiry>>(emptyList())
    val inquiries: StateFlow<List<CustomerInquiry>> get() = _inquiries

    private val _favoriteCustomers = MutableStateFlow<List<CustomerInquiry>>(emptyList())
    val favoriteCustomers: StateFlow<List<CustomerInquiry>> = _favoriteCustomers

//    private val _query = MutableStateFlow("")
//    val query: StateFlow<String> get() = _query

    //------------------------------------------------------------


    init {
        // This should be used to fetch the list of inquiries when the ViewModel is initialized
        viewModelScope.launch {
            // Collect data from getInquiriesUseCase and update _inquiries
            getInquiriesUseCase.invoke().collect { inquiriesList ->
                _inquiries.value = inquiriesList // Update the state flow with the fetched data
            }
        }
    }

    fun toggleFavorites(customer: CustomerInquiry) {
        viewModelScope.launch {
            val updatedCustomer = customer.copy(isFavorite = !customer.isFavorite)
            repository.updateInquery(updatedCustomer)
        }
        fetchFavoriteCustomers()
    }


    fun fetchFavoriteCustomers() {
        viewModelScope.launch {
            _favoriteCustomers.value = repository.getFavoriteCustomers()
        }
    }


    // Check if a customer is a favorite



    // Function to add a new inquiry
    fun addInquiry(inquiry: CustomerInquiry) {
        viewModelScope.launch {
            addInquiryUseCase(inquiry)
            Log.d(
                "InquiryRepository",
                "Inserting customer: $inquiry"
            )// Call the use case to add an inquiry
        }
    }

    fun addAllEnquiries(inquiry: List<CustomerInquiry>) {
        viewModelScope.launch() {
            repository.addAllInqueries(inquiry)
            Log.d("Added list of Costumer", "${inquiry}")
        }
    }

    // Function to search inquiries based on the query string
    fun searchInquiries(query: String) {

        val queryWithWildcards = "%$query%"

        // Call repository method to fetch filtered results
        viewModelScope.launch {
            repository.searchInquiries(queryWithWildcards).collect { inquiries ->
                _inquiries.value = inquiries
            }
        }
    }

    fun updateInquery(customer: CustomerInquiry) {
        viewModelScope.launch {
            try {
                updateInquiryUseCase.invoke(customer)
                getAllInqueries()
            } catch (e: Exception) {
                Log.d("ViewModelError", "Not Updating: ${e.message}")
            }
        }
    }


    // Function to delete an inquiry by its ID
    fun deleteInquiry(id: Int) {
        viewModelScope.launch {
            repository.deleteInquiry(id) // Call repository to delete the inquiry
        }
    }

    fun getAllInqueries() {
        viewModelScope.launch {
            getInquiriesUseCase.invoke().collect { inquiriesList ->
                _inquiries.value = inquiriesList // Update _inquiries state
            }
        }
    }
        fun getInquiriesList(): List<CustomerInquiry>{
            return _inquiries.value

        }
}


