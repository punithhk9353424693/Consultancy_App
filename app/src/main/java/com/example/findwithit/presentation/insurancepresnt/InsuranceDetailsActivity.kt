package com.example.findwithit.presentation.insurancepresnt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.findwithit.databinding.InsurancecostumerdetailsBinding
import com.example.findwithit.insurance.model.InsuranceCostumer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsuranceDetailsActivity(): AppCompatActivity() {

    private lateinit var binding: InsurancecostumerdetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = InsurancecostumerdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the InsuranceCostumer object from the intent
        val insurance = intent.getParcelableExtra<InsuranceCostumer>("insuranceDetails")

        if (insurance != null) {
            // Bind the data to the views
            bindInsuranceDetails(insurance)
        }
    }

    private fun bindInsuranceDetails(insurance: InsuranceCostumer) {
        // Set the header details
        binding.tvOwnerName.text = insurance.ownerName
        binding.tvMobile.text = "Mobile: ${insurance.mobile}"
        binding.tvVehicleDetails.text = "Vehicle: ${insurance.vehicleMake} - ${insurance.vehicleModel} (${insurance.vehicleClass})"

        // Set the detailed information
        binding.tvState.text = "State: ${insurance.state}"
        binding.tvRegNo.text = "Registration No: ${insurance.regNo}"
        binding.tvRegDate.text = "Registration Date: ${insurance.regDate}"
        binding.tvAddress.text = "Address: ${insurance.address}"
        binding.tvEngineNo.text = "Engine No: ${insurance.enginNo}"
        binding.tvChassisNo.text = "Chassis No: ${insurance.chasNo}"
        binding.tvVehicleMake.text = "Vehicle Make: ${insurance.vehicleMake}"
        binding.tvVehicleModel.text = "Vehicle Model: ${insurance.vehicleModel}"
        binding.tvFuel.text = "Fuel Type: ${insurance.fuel}"
        binding.tvSaleAmount.text = "Sale Amount: ${insurance.saleAmount}"
        binding.tvSeatCapacity.text = "Seat Capacity: ${insurance.seatCapacity}"
    }
}