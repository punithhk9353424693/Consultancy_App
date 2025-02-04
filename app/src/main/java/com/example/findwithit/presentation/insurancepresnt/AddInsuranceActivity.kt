package com.example.findwithit.presentation.insurancepresnt

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import com.example.findwithit.R
import com.example.findwithit.databinding.AddinginsuranceBinding
import com.example.findwithit.insurance.model.InsuranceCostumer
import com.example.findwithit.presentation.adaptors.InsurancAdapter
import com.example.findwithit.presentation.home.viewmodel.InsuranceViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddInsuranceActivity : AppCompatActivity() {
    private lateinit var binding: AddinginsuranceBinding
    private val viewModel: InsuranceViewModel by viewModels()
    private lateinit var insuranceAdapter: InsurancAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddinginsuranceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        insuranceAdapter = InsurancAdapter(mutableListOf(), viewModel)
        binding.regDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.regDateEditText.setText(selectedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }
        binding.backwordadd.setOnClickListener() {
            finish()
        }

        binding.submitButton.setOnClickListener() { view ->
            val state = binding.stateEditText.text.toString()
            val regNumber = binding.regNoEditText.text.toString()
            val regDate = binding.regDateEditText.text.toString()
            val ownerName = binding.ownerNameEditText.text.toString()
            val address = binding.addressEditText.text.toString()
            val engineNo = binding.enginNoEditText.text.toString()
            val chassisNo = binding.chasNoEditText.text.toString()
            val vehicleMake = binding.vehicleMakeEditText.text.toString()
            val vehicleModel = binding.vehicleModelEditText.text.toString()
            val vehicleClass = binding.vehicleClassEditText.text.toString()
            val fuel = binding.fuelEditText.text.toString()
            val saleAmount = binding.saleAmountEditText.text.toString()
            val seatCapacity = binding.seatCapacityEditText.text.toString()
            val mobileNo = binding.mobileEditText.text.toString()
            if (state.isNotEmpty() && regNumber.isNotEmpty() && regDate.isNotEmpty()
                && ownerName.isNotEmpty() && address.isNotEmpty() && engineNo.isNotEmpty() && chassisNo.isNotEmpty() &&
                vehicleMake.isNotEmpty() && vehicleModel.isNotEmpty() && vehicleClass.isNotEmpty() &&
                fuel.isNotEmpty() && saleAmount.isNotEmpty() && seatCapacity.isNotEmpty() && mobileNo.isNotEmpty()
            ) {
                val insurance = InsuranceCostumer(
                    state = state,
                    regNo = regNumber,
                    regDate = regDate,
                    ownerName = ownerName,
                    address = address,
                    enginNo = engineNo,
                    chasNo = chassisNo,
                    vehicleMake = vehicleMake,
                    vehicleModel = vehicleModel,
                    vehicleClass = vehicleClass,
                    fuel = fuel,
                    saleAmount = saleAmount,
                    seatCapacity = seatCapacity,
                    mobile = mobileNo.toLong()
                )
                viewModel.viewModelScope.launch {
                    viewModel.insertInsurance(insurance)
                    insuranceAdapter.addInsurance(insurance)

                }

                finish()

                Snackbar.make(view, "Insurance added Successfully ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            } else {
                Snackbar.make(view, "Fill all the fields with proper inputs ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }
    }




