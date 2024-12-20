package com.example.findwithit.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findwithit.R
import com.example.findwithit.presentation.adaptors.CostumerAdapter
import com.example.findwithit.utils.CostumerList
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllCostumers : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var costumerAdapter: CostumerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_all_list)

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerViewAllList)
        recyclerView.layoutManager= LinearLayoutManager(this)
        costumerAdapter = CostumerAdapter(CostumerList.customers)
        recyclerView.adapter = costumerAdapter
     // This can be triggered from other activities/fragments as well.
    }
}
