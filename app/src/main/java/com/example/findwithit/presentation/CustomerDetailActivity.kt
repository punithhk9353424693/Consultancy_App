package com.example.findwithit.presentation

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.findwithit.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_person)
        val backbtn = findViewById<ImageButton>(R.id.backword)
        val nameTextView: TextView = findViewById(R.id.name)
        val phoneTextView: TextView = findViewById(R.id.phono)
        val areaTextView: TextView = findViewById(R.id.area)
        val categoryTextView: TextView = findViewById(R.id.category)
        val remarksTextView: TextView = findViewById(R.id.remarks)
        val dateTextView: TextView = findViewById(R.id.date)

        backbtn.setOnClickListener() {
            finish()
        }
        // Retrieve the customer data from the Intent
        val customer = intent.getParcelableExtra<Costumer>("customer_data")

        // Set the customer data to the TextViews
        customer?.let {
            nameTextView.text = it.name
            phoneTextView.text = it.phono.toString()
            areaTextView.text = it.area
            categoryTextView.text = it.category
            remarksTextView.text = it.remarks
            dateTextView.text = it.date.toString()
        }

    }
}