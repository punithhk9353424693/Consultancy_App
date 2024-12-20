package com.example.findwithit.presentation.home.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findwithit.R
import com.example.findwithit.presentation.adaptors.CostumerAdapter
import com.example.findwithit.presentation.home.viewmodel.InquiryViewModel
import com.example.findwithit.utils.CostumerList
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    private lateinit var costumerAdapter: CostumerAdapter
    private val inquiryViewModel: InquiryViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)

        recyclerView = findViewById(R.id.recyclerView)
        costumerAdapter = CostumerAdapter(CostumerList.customers)
        recyclerView.adapter = costumerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val render = findViewById<ImageButton>(R.id.renderToAnother)
        render.setOnClickListener {
            val intent = Intent(this, AddPersonActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            costumerAdapter.updateCustomers(CostumerList.customers)
            costumerAdapter.notifyDataSetChanged()
        }
    }
}
