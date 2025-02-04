package com.example.findwithit.presentation.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.findwithit.data.local.AppDatabase
import com.example.findwithit.insurance.model.InsuranceCostumer
import com.example.findwithit.insurance.repository.InsurancePersonImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HandlerViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getInstance(application).insuranceDao()
    private val repository = InsurancePersonImpl(userDao)

    private val _importStatus = MutableLiveData<String>()
    val importStatus: LiveData<String> get() = _importStatus

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> get() = _progress

    fun importUsers(users: List<InsuranceCostumer>) {
        viewModelScope.launch(Dispatchers.IO) {
            _importStatus.value = "Importing..."
            _progress.value = 0

            users.chunked(100).forEachIndexed { index, chunk ->
                repository.importUsers(chunk)
                _progress.value = ((index + 1) * 100) / (users.size / 100)
            }

            _importStatus.value = "Import Complete!"
            _progress.value = 100
        }
    }
}
