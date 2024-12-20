package com.example.findwithit.presentation.home.view

import Costumer
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.findwithit.R
import com.example.findwithit.presentation.AllCostumers
import com.example.findwithit.presentation.adaptors.CostumerAdapter
import com.example.findwithit.presentation.home.viewmodel.InquiryViewModel
import com.example.findwithit.utils.CostumerList
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class AddPersonActivity() : ComponentActivity() {
    lateinit var custumerAdapter: CostumerAdapter

    private val inquiryViewModel: InquiryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_person)
        val backwordbtn = findViewById<ImageButton>(R.id.backword)
        custumerAdapter = CostumerAdapter(CostumerList.customers)
        backwordbtn.setOnClickListener() {
            finish()
        }
        val name = findViewById<EditText>(R.id.name)
        val phono = findViewById<EditText>(R.id.phono)
        val date21 = findViewById<EditText>(R.id.date)
        val area = findViewById<EditText>(R.id.area)
        val category = findViewById<EditText>(R.id.category)
        val remarks = findViewById<EditText>(R.id.remarks)
        val addbtn = findViewById<Button>(R.id.addCustomerbtn)
        val viewCostumers = findViewById<Button>(R.id.viewCostumersbtn)

        date21.setOnClickListener() {
            showDatePickerDialog()
        }
        addbtn.setOnClickListener() {
            val name1 = name.text.toString()
            val phono1 = phono.text.toString()
            val date1 = date21.text.toString()
            val area1 = area.text.toString()
            val category1 = category.text.toString()
            val remarks1 = remarks.text.toString()
//            val viewCostumers = findViewById<Button>(R.id.viewCostomers)
            if (name1.isNullOrEmpty() || phono1.isNullOrEmpty() || date1.isNullOrEmpty() || area1.isNullOrEmpty() ||
                category1.isNullOrEmpty() || remarks1.isNullOrEmpty()
            ) {
                Toast.makeText(this, "Fill All the fields With Proper Inputs", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    val phoneNumber = phono1.toLong()
                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(date1)
                    if (date != null) {
                        val customer =
                            Costumer(
                                name1, phoneNumber,
                                date, area1, category1, remarks1
                            )
//
                        CostumerList.customers.add(customer)
                        custumerAdapter.addCustomer(customer)
                        custumerAdapter.notifyItemInserted(CostumerList.customers.size - 1)
                        Log.d(
                            "AddPersonActivity",
                            "Customer added: ${CostumerList.customers.size} customers"
                        )

                        //   Toast.makeText(this, "Customer:$customJson", Toast.LENGTH_SHORT).show()
                        Toast.makeText(this, "Added SuccessFully", Toast.LENGTH_SHORT).show()

                        name.text.clear()
                        phono.text.clear()
                        area.text.clear()
                        category.text.clear()
                        remarks.text.clear()
                        date21.text.clear()
                        setResult(RESULT_OK)

                    } else {
                        Toast.makeText(
                            this,
                            "Invalid date format. Use yyyy/MM/dd",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Invalid input: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewCostumers.setOnClickListener() {
            val intent = Intent(this, AllCostumers::class.java)
            startActivity(intent)
        }
    }


    fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // DatePickerDialog constructor expects: Context, listener, year, month, day
        val datePickerDialog = DatePickerDialog(
            this,  // Context (Activity)
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date and set it to EditText
                val formattedDate =
                    String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                val dateEditText: EditText = findViewById(R.id.date)
                dateEditText.setText(formattedDate)
            },
            year,  // Year
            month, // Month
            day    // Day
        )

        // Show the dialog
        datePickerDialog.show()

    }

}


