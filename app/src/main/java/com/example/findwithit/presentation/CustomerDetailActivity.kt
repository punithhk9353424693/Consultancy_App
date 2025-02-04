package com.example.findwithit.presentation

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.findwithit.R
import com.example.findwithit.data.local.CustomerInquiry
import com.example.findwithit.presentation.home.viewmodel.InquiryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class CustomerDetailActivity : AppCompatActivity() {

    // ViewModel initialization
    private val inquiryViewModel: InquiryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_detail)

        // Initialize Views
        val backbtn = findViewById<ImageButton>(R.id.backwordbtn)
        val nameTextView: TextView = findViewById(R.id.name)
        val phoneTextView: TextView = findViewById(R.id.phono)
        val areaTextView: TextView = findViewById(R.id.areatext)
        val propertyText = findViewById<TextView>(R.id.propertyTypeEditText)
        val range = findViewById<TextView>(R.id.rangeamEdText)
        val lookingFor = findViewById<TextView>(R.id.lookingEdText)
        val facing = findViewById<TextView>(R.id.facingedText)
        val categoryTextView: TextView = findViewById(R.id.categoryEditText)
        val remarksTextView: TextView = findViewById(R.id.remarks)
        val dateTextView: TextView = findViewById(R.id.date)
        val updateCustomerBtn = findViewById<Button>(R.id.updateCustumerBtn)

        // Retrieve the customer data passed from the previous activity
        val customer = intent.getParcelableExtra<CustomerInquiry>("customer_data")

        // Set up back button to finish the activity
        backbtn.setOnClickListener {
            finish()
        }

        // Set up property selection menu
        propertyText.setOnClickListener {
            showProperties(propertyText)
        }

        // Set up category selection menu
        categoryTextView.setOnClickListener {
            showCategoryPopup(categoryTextView)
        }
        facing.setOnClickListener(){
            showFacingPopup(facing)
        }

        // Set the initial customer data in the fields
        customer?.let {
            nameTextView.text = it.name
            phoneTextView.text = it.contactNumber.toString()
            areaTextView.text = it.area
            propertyText.text = it.propertyType
            categoryTextView.text = it.inquiryCategory
            range.text = it.range
            lookingFor.text = it.lookingFor
            facing.text = it.facing
            dateTextView.text = it.createdDateTime
            remarksTextView.text = it.remarks
        }

        // Set up DatePicker for selecting a date
        dateTextView.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    dateTextView.text = selectedDate
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        // Set up Update button to save changes
        updateCustomerBtn.setOnClickListener {
            customer?.let {
                val updatedCustomer = CustomerInquiry(
                    id = it.id, // Keep the original ID for updating
                    name = nameTextView.text.toString(),
                    contactNumber = phoneTextView.text.toString().toLongOrNull() ?: it.contactNumber,
                    area = areaTextView.text.toString(),
                    propertyType = propertyText.text.toString(),
                    inquiryCategory = categoryTextView.text.toString(),
                    range = range.text.toString(),
                    lookingFor = lookingFor.text.toString(),
                    facing = facing.text.toString(),
                    createdDateTime = dateTextView.text.toString(),
                    remarks = remarksTextView.text.toString()
                )

                inquiryViewModel.updateInquery(updatedCustomer)

                // Show a success message and close the activity
                Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    // Show a popup menu for selecting a property type
    private fun showProperties(property: TextView) {
        val popupMenu = PopupMenu(this, property)
        val menu = popupMenu.menu
        menu.add("House")
        menu.add("Flat")
        menu.add("Site")
        menu.add("Commercial Properties")
        menu.add("Agricultural_Land")
        popupMenu.setOnMenuItemClickListener { item ->
            property.setText(item.title)
            true
        }
        popupMenu.show()
    }

    // Show a popup menu for selecting a category (Rent, Sale, Buy, Lease)
    private fun showCategoryPopup(category: TextView) {
        val popup = PopupMenu(this, category)
        val menu = popup.menu
        menu.add("Rent")
        menu.add("Sale")
        menu.add("Buy")
        menu.add("Lease")
        popup.setOnMenuItemClickListener { item ->
            category.setText(item.title)
            true
        }
        popup.show()
    }
    fun showFacingPopup(facing: TextView) {
        val popupMenu = PopupMenu(this, facing)
        val menu = popupMenu.menu
        menu.add("East")
        menu.add("West")
        menu.add("North")
        menu.add("South")
        popupMenu.setOnMenuItemClickListener(){item->
            facing.setText(item.title)
            true
        }
        popupMenu.show()
    }
}
