package com.example.findwithit.presentation.insurancepresnt

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import com.example.findwithit.databinding.UpdateinsuranceBinding
import com.example.findwithit.insurance.model.InsuranceCostumer
import com.example.findwithit.presentation.home.viewmodel.InsuranceViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

@AndroidEntryPoint
class UpdateInsuranceActivity : AppCompatActivity() {
    private val viewModel: InsuranceViewModel by viewModels()
    private lateinit var binding: UpdateinsuranceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UpdateinsuranceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val insurance = intent.getParcelableExtra<InsuranceCostumer>("insuranceDetails")
        insurance?.let {
            binding.etState.setText(insurance.state)
            binding.etRegNo.setText(insurance.regNo)
            binding.etRegDate.setText(insurance.regDate)
            binding.etOwnerName.setText(insurance.ownerName)
            binding.etAddress.setText(insurance.address)
            binding.etEngineNo.setText(insurance.enginNo)
            binding.etChassisNo.setText(insurance.chasNo)
            binding.etVehicleMake.setText(insurance.vehicleMake)
            binding.etVehicleModel.setText(insurance.vehicleModel)
            binding.etVehicleClass.setText(insurance.vehicleClass)
            binding.etFuel.setText(insurance.fuel)
            binding.etSaleAmount.setText(insurance.saleAmount)
            binding.etSeatCapacity.setText(insurance.seatCapacity)
            binding.etMobile.setText(insurance.mobile.toString())
        }
        binding.backupdate.setOnClickListener() {
            finish()
        }
        binding.etRegDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.etRegDate.setText(selectedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }
        binding.btnUpdate.setOnClickListener { view ->
            insurance?.let {
                // Update the insurance object with new values from the UI
                it.state = binding.etState.text.toString()
                it.regNo = binding.etRegNo.text.toString()
                it.regDate = binding.etRegDate.text.toString()
                it.ownerName = binding.etOwnerName.text.toString()
                it.address = binding.etAddress.text.toString()
                it.enginNo = binding.etEngineNo.text.toString()
                it.chasNo = binding.etChassisNo.text.toString()
                it.vehicleMake = binding.etVehicleMake.text.toString()
                it.vehicleModel = binding.etVehicleModel.text.toString()
                it.vehicleClass = binding.etVehicleClass.text.toString()
                it.fuel = binding.etFuel.text.toString()
                it.saleAmount = binding.etSaleAmount.text.toString()
                it.seatCapacity = binding.etSeatCapacity.text.toString()
                it.mobile = binding.etMobile.text.toString().toLong()

                // Call the ViewModel to update the insurance
                viewModel.updateInsurance(it)
                Snackbar.make(view, "Details Updated Successfully", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

                finish()
            }
        }
    }
}