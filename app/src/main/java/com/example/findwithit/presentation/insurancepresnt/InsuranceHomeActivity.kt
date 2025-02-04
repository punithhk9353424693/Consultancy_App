package com.example.findwithit.presentation.insurancepresnt

import ImportingInsurance
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.findwithit.ImportFiles
import com.example.findwithit.databinding.InsurancehomeBinding
import com.example.findwithit.presentation.home.viewmodel.InsuranceViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.findwithit.R
import com.example.findwithit.insurance.model.InsuranceCostumer
import com.example.findwithit.presentation.adaptors.InsurancAdapter
import com.example.findwithit.presentation.home.viewmodel.HandlerViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InsuranceHomeActivity() : AppCompatActivity() {
    private val viewModel: InsuranceViewModel by viewModels()
    private lateinit var binding: InsurancehomeBinding
    private lateinit var insuranceRecycler: RecyclerView
    private lateinit var insuranceAdapter: InsurancAdapter
    private val handlerViewModel: HandlerViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InsurancehomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addInsuranceBtn.setOnClickListener() {
            val intent = Intent(this, AddInsuranceActivity::class.java)
            startActivity(intent)
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        insuranceRecycler = binding.insuranceRecycler
        insuranceAdapter = InsurancAdapter(mutableListOf(), viewModel)
        // Collect data from the flow and update the adapter when the list changes
        lifecycleScope.launch {
            viewModel.insurances.collect { insuranceList ->
                // Update the adapter with the new list of insurance items
                insuranceAdapter.submitList(insuranceList)
            }
        }


        insuranceAdapter = InsurancAdapter(mutableListOf(), viewModel)
        insuranceRecycler.adapter = insuranceAdapter
        insuranceRecycler.layoutManager = LinearLayoutManager(this)

        val progressBar = binding.progressBar
        val progressTextView = binding.progressText
        handlerViewModel.importStatus.observe(this) { status ->
            binding.progressText.text = status
            binding.progressBar.visibility = if (status == "Importing...") View.VISIBLE else View.GONE
            binding.progressText.visibility = if (status == "Importing...") View.VISIBLE else View.GONE
        }

        handlerViewModel.progress.observe(this) { progress ->
            binding.progressBar.progress = progress
            binding.progressText.text = "$progress%"
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.insurancemenu, menu)
        val searchItem = menu?.findItem(R.id.insurance_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = "Search Insurance here"

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchInsurance(query)
                }
                return true
            }

            override fun onQueryTextChange(newtext: String?): Boolean {
                newtext?.let {
                    if (newtext.isNotEmpty()) {
                        viewModel.searchInsurance(newtext)
                    } else {
                        viewModel.getAllInsurances()
                    }
                }
                return true
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.insurance -> {
                true
            }

            R.id.importdatafrom -> {
                openFilePicker()
                true
            }

            R.id.exportfromhere -> {
                checkPermision()
                val insurances = viewModel.insurances.value
                if (insurances.isNotEmpty()) {
                    choiseForExport()
                } else {
                    Toast.makeText(this, "No insurance data available", Toast.LENGTH_SHORT).show()
                }
                true
            }


            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    fun choiseForExport() {
        val options = arrayOf("1) Save as Excel", "2) Save as Pdf")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Export Option")
        builder.setIcon(R.drawable.ic_exporting)
        builder.setItems(options) { _, which ->
            when (which) {
                0->viewModel.viewModelScope.launch(){
                    viewModel.getInsuranceForExport().collect{insuranceList->
                        exportFromHere(insuranceList,this@InsuranceHomeActivity)
                    }
                }
                1 -> viewModel.viewModelScope.launch(){
                    viewModel.getInsuranceForExport().collect{insuranceList->
                        exportPdfFromHere(insuranceList,this@InsuranceHomeActivity)
                    }
                }

            }
        }
        builder.show()

    }


    fun hasExternalPermision(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun checkPermision() {
        if (hasExternalPermision()) {
            Toast.makeText(this, "Permision Already Granted", Toast.LENGTH_SHORT).show()
        } else {
            requestPermison()
        }
    }

    private fun requestPermison() {
        var list = mutableListOf<String>()
        if (!hasExternalPermision()) {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (list.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, list.toTypedArray(), 2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permision Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permision Denied", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }


    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "application/vnd.ms-excel"  // Accept both .xls and .xlsx files
            addCategory(Intent.CATEGORY_OPENABLE)
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf(
                    "application/vnd.ms-excel",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
            )
        }
        startActivityForResult(intent, 100)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data
            uri?.let {
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val inputStream = contentResolver.openInputStream(uri)
                        inputStream?.let { stream ->
                            val importFiles =
                                ImportingInsurance(this@InsuranceHomeActivity, handlerViewModel)
                       val  insuranceUsers=importFiles.extractExcelContent(uri, this@InsuranceHomeActivity)
                            if (insuranceUsers.isNotEmpty()) {
                                startImport(insuranceUsers, this@InsuranceHomeActivity)  // CALL HERE
                            } else {
                                runOnUiThread {
                                    Toast.makeText(this@InsuranceHomeActivity, "No valid data found in file", Toast.LENGTH_SHORT).show()
                                }
                            }
                            // Import the data in batches


                        }
                    } catch (e: Exception) {
                            Toast.makeText(this@InsuranceHomeActivity, "Error reading the file", Toast.LENGTH_SHORT).show()
                    }
                    }
                }
            }
        }
    fun startImport(insuranceUsers: List<InsuranceCostumer>, context: Context) {
        val userJson = Gson().toJson(insuranceUsers)
        val inputData = workDataOf("user_data" to userJson)

        val workRequest = OneTimeWorkRequestBuilder<ExportWorker>()
            .setInputData(inputData)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)

    }
}