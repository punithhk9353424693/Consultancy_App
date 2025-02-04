package com.example.findwithit.presentation.home.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.findwithit.R
import com.example.findwithit.presentation.adaptors.CostumerAdapter
import com.example.findwithit.presentation.home.viewmodel.InquiryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import com.example.findwithit.data.local.CustomerInquiry
import kotlin.text.category
import kotlin.toString
import androidx.appcompat.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.PopupMenu
import java.util.Date

@AndroidEntryPoint
class AddPersonActivity() : AppCompatActivity() {
    lateinit var custumerAdapter: CostumerAdapter
    private val inquiryViewModel: InquiryViewModel by viewModels()
    val calendar = Calendar.getInstance()
    lateinit var lookinforEditText: EditText
    lateinit var facingEditText: EditText
    lateinit var rangeEdit: EditText
    lateinit var propertyTypeEditTxt: EditText
    lateinit var linearLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_person)

        val backwordbtn = findViewById<ImageButton>(R.id.backword)
        custumerAdapter = CostumerAdapter(mutableListOf(), inquiryViewModel)

        lifecycleScope.launchWhenStarted {
            inquiryViewModel.inquiries.collect { inquiries ->
                custumerAdapter.updateCustomers(inquiries.toMutableList())  // Update the adapter
            }
        }
        backwordbtn.setOnClickListener() {
            finish()
        }

        val name = findViewById<EditText>(R.id.name)
        val phono = findViewById<EditText>(R.id.phono)
        val area = findViewById<EditText>(R.id.area)
        val selectedCategoryText = findViewById<EditText>(R.id.SelectCategory)
        facingEditText = findViewById(R.id.facingedText)
        rangeEdit = findViewById(R.id.range)
        lookinforEditText = findViewById(R.id.lookingEdText)
        propertyTypeEditTxt = findViewById(R.id.propertyType)
        val remarks = findViewById<EditText>(R.id.remarks)
        val addbtn = findViewById<Button>(R.id.addCustomerbtn)

        selectedCategoryText.setOnClickListener()
        {
            hideKeyboard(selectedCategoryText)  // Hide keyboard
            showCategoryPopup(selectedCategoryText)  // Show category popup
        }
        propertyTypeEditTxt.setOnClickListener() {
            showPropertyTypePopump(propertyTypeEditTxt)
        }
        facingEditText.setOnClickListener() {
            showFacingPopup(facingEditText)
        }

        addbtn.setOnClickListener() {
            val name1 = name.text.toString()
            val phono1 = phono.text.toString()
            val area1 = area.text.toString()
            val category1 = selectedCategoryText.text.toString()
            val remarks1 = remarks.text.toString()
            val facingHold = facingEditText.text.toString()
            val presentAddres = rangeEdit.text.toString()
            val lookinFor = lookinforEditText.text.toString()
            val propertyTypeEDT = propertyTypeEditTxt.text.toString()
            if (name1.isNullOrEmpty() || phono1.isNullOrEmpty() || area1.isNullOrEmpty() ||
                category1.isNullOrEmpty() || remarks1.isNullOrEmpty() || propertyTypeEDT.isNullOrEmpty() ||
                facingHold.isNullOrEmpty() || presentAddres.isNullOrEmpty() || lookinFor.isNullOrEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Fill All the fields With Proper Inputs",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                try {
                    val phoneNumber = phono1.toLong()
                  //  val currentDate = Calendar.getInstance().time
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val currentDate = dateFormat.format(Date())
                    val customer =
                        CustomerInquiry(
                            name = name1,
                            contactNumber = phoneNumber,
                            inquiryCategory = category1,
                            createdDateTime = currentDate,  // assuming 'date' is a Date object
                            area = area1,
                            remarks = remarks1,
                            range = presentAddres,
                            lookingFor = lookinFor,
                            facing = facingHold,
                            propertyType = propertyTypeEDT
                        )
                    inquiryViewModel.addInquiry(customer)
                    Log.d("InquiryViewModel", "Added customer with id: ${customer.id}")

                    custumerAdapter.addCustomer(customer)


                    Toast.makeText(this, "Added SuccessFully", Toast.LENGTH_SHORT).show()

                    name.text.clear()
                    phono.text.clear()
                    area.text.clear()
                    remarks.text.clear()
                    facingEditText.text.clear()
                    propertyTypeEditTxt.text.clear()
                    rangeEdit.text.clear()
                    selectedCategoryText.text.clear()
                    lookinforEditText.text.clear()
                    setResult(RESULT_OK)
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this, "Invalid input: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }


    }


    fun hideKeyboard(editText: EditText) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    fun showCategoryPopup(SelectedCategoryText: EditText) {
        val popupMenu = PopupMenu(this, SelectedCategoryText)
        val menu = popupMenu.menu
        // Add items to the PopupMenu
        menu.add("Rent")
        menu.add("Sale")
        menu.add("Buy")
        menu.add("Lease")

        popupMenu.setOnMenuItemClickListener { item ->
            SelectedCategoryText.setText(item.title)
            true
        }
        // Show the PopupMenu
        popupMenu.show()
    }

    fun showPropertyTypePopump(propertyTypeText: EditText) {
        val popupMenu = PopupMenu(this, propertyTypeText)
        val menu = popupMenu.menu
        menu.add("House")
        menu.add("Flat")
        menu.add("Site")
        menu.add("Commercial Properties")
        menu.add("Agricultural_Land")
        popupMenu.setOnMenuItemClickListener { item ->
            propertyTypeText.setText(item.title)
            true
        }
        popupMenu.show()
    }

    fun showFacingPopup(facing: EditText) {
        val popupMenu = PopupMenu(this, facing)
        val menu = popupMenu.menu
        menu.add("East")
        menu.add("West")
        menu.add("North")
        menu.add("South")
        popupMenu.setOnMenuItemClickListener { item ->
            facing.setText(item.title)
            true
        }
        popupMenu.show()
    }
}


