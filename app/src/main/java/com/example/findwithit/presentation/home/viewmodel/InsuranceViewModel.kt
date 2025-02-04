package com.example.findwithit.presentation.home.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.findwithit.domain.usecase.GetAllInsurances
import com.example.findwithit.insurance.model.InsuranceCostumer
import com.example.findwithit.insurance.repository.InsurancePerson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InsuranceViewModel @Inject constructor(
    private val repo: InsurancePerson,
    private val getAllInsurances: GetAllInsurances
) : ViewModel() {

    private val _insurances =
        MutableStateFlow<List<InsuranceCostumer>>(emptyList())
    val insurances: StateFlow<List<InsuranceCostumer>> get() = _insurances


    private val _importStatus = MutableLiveData<String>()
    val importStatus: LiveData<String> get() = _importStatus

    init {
        // This should be used to fetch the list of inquiries when the ViewModel is initialized
        viewModelScope.launch {
            // Collect data from getInquiriesUseCase and update _inquiries
            getAllInsurances.invoke().collect { insurancessList ->
                _insurances.value = insurancessList
            }
        }
    }

    fun getAllInsurances() {
        viewModelScope.launch {
            getAllInsurances.invoke().collect { inquiriesList ->
                _insurances.value = inquiriesList // Update _inquiries state
            }
        }
    }
    fun getInsuranceForExport(): Flow<List<InsuranceCostumer>>{
        return getAllInsurances.invoke()
    }

    suspend fun insertInsurance(insurance: InsuranceCostumer) {
        repo.insertInsurance(insurance)
    }

    fun searchInsurance(query: String) {
        val filterQuery = "%$query%"
        viewModelScope.launch {
            repo.searchInsurance(filterQuery).collect() { insurances ->
                _insurances.value = insurances
            }
        }
    }
    fun deleteInsurance(id:Int){
        viewModelScope.launch(){
            repo.deleteInsurance(id)
        }
    }
    fun updateInsurance(insurance: InsuranceCostumer){
        try {

        viewModelScope.launch() {
            repo.updateInsurance(insurance)
            getAllInsurances()
        }}catch (e: Exception){
            Log.d("ViewModelError", "Not Updating: ${e.message}")

        }
    }
    // In your ViewModel



    fun addAllInsurance(insuranceList: List<InsuranceCostumer>) {
        viewModelScope.launch {
            _importStatus.value = "Importing..."
            repo.addAllInsurances(insuranceList)
            _importStatus.value = "Import Complete!"
        }
    }


}