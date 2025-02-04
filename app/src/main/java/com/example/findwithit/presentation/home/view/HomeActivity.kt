package com.example.findwithit.presentation.home.view

import ExpirationCheckWorker
import SwipeToDeleteCallback
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findwithit.R
import com.example.findwithit.presentation.adaptors.CostumerAdapter
import com.example.findwithit.presentation.home.viewmodel.InquiryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.findwithit.data.local.CustomerInquiry
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.VerticalAlignment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.findwithit.ImportFiles
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.processor.Context
import com.example.findwithit.presentation.insurancepresnt.AddInsuranceActivity
import com.example.findwithit.presentation.insurancepresnt.InsuranceHomeActivity
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument


@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    private lateinit var costumerAdapter: CostumerAdapter
    private val inquiryViewModel: InquiryViewModel by viewModels()
    private lateinit var expirationCheckWorker: ExpirationCheckWorker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)
        val isDarkMode = getSharedPreferences("app_preferences", MODE_PRIVATE)
            .getBoolean("dark_mode", false)

        // Apply the theme based on saved preference
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.AppTheme_Dark)  // Apply dark theme
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.AppTheme)  // Apply light theme
        }

        expirationCheckWorker = ExpirationCheckWorker(this, inquiryViewModel, this)
        checkNotificationPermission()

        val insurancBtn=findViewById<Button>(R.id.insuranceBtn)
        insurancBtn.setOnClickListener {
            val intent= Intent(this,InsuranceHomeActivity::class.java)
            startActivity(intent)
        }

        expirationCheckWorker.createNotificationChannel()
        recyclerView = findViewById(R.id.recyclerView)

        costumerAdapter = CostumerAdapter(mutableListOf(), inquiryViewModel)
        recyclerView.adapter = costumerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
//-------------------Theme----------------------------------
        val themeSwitch = findViewById<SwitchCompat>(R.id.theme_switch)
        themeSwitch.isChecked = isDarkMode

        // Set up the theme switch listener
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Enable Dark Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                setTheme(R.style.AppTheme_Dark)  // Apply dark theme
            } else {
                // Enable Light Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                setTheme(R.style.AppTheme)  // Apply light theme
            }

            // Save the user preference for next time
            val sharedPrefs = getSharedPreferences("app_preferences", MODE_PRIVATE)
            sharedPrefs.edit().putBoolean("dark_mode", isChecked).apply()

            // Recreate activity to apply changes
            recreate()
        }

        // Additional UI updates based on the theme (for example, buttons, text colors, etc.)
        updateUI(isDarkMode)

        //---------------------------/Theme-----------------------------------------

        val swipabledelete = SwipeToDeleteCallback(
            context = this,
            adapter = costumerAdapter,
            inquiryViewModel = inquiryViewModel,
            recyclerView = recyclerView
        )
        val itemTouchHelper = ItemTouchHelper(swipabledelete)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        // Collect the inquiries data from the ViewModel
        lifecycleScope.launchWhenStarted {
            inquiryViewModel.inquiries.collect { inquiries ->
                costumerAdapter.updateCustomers(inquiries.toMutableList()) // Update the adapter with the filtered list
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)

        // Find the Search item
        val searchItem = menu?.findItem(R.id.menu_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = "Search here"

        // Ensure the SearchView is not null before proceeding
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("Search", "Query submitted: $query")
                query?.let {
                    inquiryViewModel.searchInquiries(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("Search", "Query changed: $newText")

                newText?.let {
                    if (newText.isEmpty()) {
                        inquiryViewModel.getAllInqueries()
                    } else {
                        inquiryViewModel.searchInquiries(newText)
                    }
                }
                return true
            }
        })
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.add_costumer -> {
                val intent = Intent(this, AddPersonActivity::class.java)
                startActivityForResult(intent, 1)
            }

            R.id.exportData -> {
                checkHasExternalPermision()
                val inquiries = inquiryViewModel.inquiries.value
                if (inquiries.isNotEmpty()) {
                    showDailogue(inquiries)
                } else {
                    Toast.makeText(
                        this@HomeActivity,
                        "No customer data available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return true
            }

            R.id.importdata -> {
                importingBychoise()

            }

            R.id.menu_search -> {
                val searchView = item.actionView as? SearchView
                searchView?.isIconifiedByDefault = false
                return true
            }

            R.id.faverates -> {
                val intent = Intent(this, FaverateActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.addInsurance->{
                val intent= Intent(this, AddInsuranceActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.insuranceList->{
                true
            }


            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data
            uri?.let {
                Toast.makeText(this, "Uploaded SuccesFully", Toast.LENGTH_SHORT).show()
                val mintype = contentResolver.getType(it)
                when (mintype) {
                    "application/pdf" -> {
                        val importFiles = ImportFiles(this, inquiryViewModel)
                        importFiles.extractPdfContent(uri)
                    }

                    "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> {
                        val importFiles = ImportFiles(this, inquiryViewModel)
                        importFiles.extractExcelContent(uri)
                    }

                    else -> {
                        Toast.makeText(this, "Unsupported file type", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Uploaded failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun importingBychoise() {
        val option = arrayOf("1) Import pdf file", "2) Import Excel file")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose your type")
        builder.setIcon(R.drawable.ic_import)
        builder.setItems(option) { _, which ->
            when (which) {
                0 -> {
                    intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        type = "application/pdf"
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }
                    startActivityForResult(intent, 100)
                }

                1 -> {
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
            }
        }
        builder.show()
    }

    // Disable search view on start and reset
    override fun onStart() {
        super.onStart()
        //  searchView.isEnabled = true
    }

    override fun onResume() {
        super.onResume()
        expirationCheckWorker.checkCustomerExpiry()
    }

    override fun onRestart() {
        super.onRestart()
        //   searchView.isEnabled = false
    }


    fun exportToWatsApp(custumers: List<CustomerInquiry>) {
        val formatData = formatCustomerDataForExport(custumers)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, formatData)
        val packageManager = packageManager
        val watsapIntent = packageManager.getLaunchIntentForPackage("com.whatsapp")
        if (watsapIntent != null) {
            intent.setPackage("com.whatsapp")
            startActivity(Intent.createChooser(intent, "Send to WhatsApp"))
        } else {
            Toast.makeText(this, "Whatsup is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatCustomerDataForExport(customers: List<CustomerInquiry>): String {
        val sb = StringBuilder()
        sb.append("ID, Name, Phone, Area, propertyType, Category, facing,range,Remarks, Date\n")
        val inputDateFormat = SimpleDateFormat(
            "yyyy/MM/dd'T'HH:mm:ss",
            Locale.getDefault()
        ) // Assuming the input date format is like "2025-01-05T13:22:30"
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        for (customer in customers) {
            try {
                // Parse the createdDateTime string to a Date object
                val date = inputDateFormat.parse(customer.createdDateTime)
                val formattedDate = date?.let { outputDateFormat.format(it) } ?: "Invalid Date"

                // Append customer data to StringBuilder
                sb.append("${customer.id}, ${customer.name}, ${customer.contactNumber}, ${customer.area}, ${customer.propertyType}, ${customer.inquiryCategory}, ${customer.facing}, ${customer.range},${customer.remarks}, $formattedDate\n")
            } catch (e: Exception) {
                e.printStackTrace()
                sb.append("${customer.id}, ${customer.name}, ${customer.contactNumber}, ${customer.area}, ${customer.propertyType},${customer.inquiryCategory},  ${customer.facing}, ${customer.range},${customer.remarks}, Invalid Date\n")
            }
        }

        return sb.toString()
    }

    fun showDailogue(customers: List<CustomerInquiry>) {
        val excel = ExcelExportHelper(this)
        val options = arrayOf("1) Export To WhatsApp", "2) Save as PDF", "3) Save as Excel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Export Option")
        builder.setIcon(R.drawable.ic_exporting)
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> exportToWatsApp(customers)
                1 -> saveAsPDF(customers)
                2 -> excel.exportExcel(customers)
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

    fun checkHasExternalPermision() {
        if (hasExternalPermision()) {
            Toast.makeText(this, "Permision already Granted", Toast.LENGTH_SHORT).show()
        } else {
            requestPermision()
        }
    }

    fun requestPermision() {
        val permission = mutableListOf<String>()
        if (!hasExternalPermision()) {
            permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permission.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permission.toTypedArray(), 2)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    {
                        Toast.makeText(
                            this,
                            "Permision Granted Succesfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    Toast.makeText(this, "Permision  Denied", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    fun saveAsPDF(customers: List<CustomerInquiry>) {
        // Show dialog to get the custom PDF file name
        val dialogBuilder = AlertDialog.Builder(this)
        val input = EditText(this)
        input.hint = "Enter PDF file name"

        // Setup dialog
        dialogBuilder.setTitle("Enter PDF File Name")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val fileName = input.text.toString().trim()

                // If file name is not empty, save PDF
                if (fileName.isNotEmpty()) {
                    savePdfToStorage(fileName, customers)
                } else {
                    if (fileName.isEmpty()) {
                        Toast.makeText(this, "File name cannot be empty", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Invalid file name", Toast.LENGTH_SHORT).show()

                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun savePdfToStorage(fileName: String, customers: List<CustomerInquiry>) {
        // Ensure the download directory exists
        val downloadDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadDir.exists()) downloadDir.mkdirs()

        val pdfFile = File(downloadDir, "$fileName.pdf")

        try {
            // Create PDF writer and document
            val writer = PdfWriter(FileOutputStream(pdfFile))
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument, PageSize.A4.rotate())
            document.setMargins(20f, 20f, 20f, 20f)
            // Create table with customer data
            val table = Table(floatArrayOf(2f, 3f, 3f, 3f, 3f, 3f, 3f, 3f, 3f, 3f)).apply {
                addCell(Cell().add(Paragraph("Name").setBold())).setVerticalAlignment(
                    VerticalAlignment.MIDDLE
                ).setPadding(5f)
                addCell(Cell().add(Paragraph("Phone").setBold())).setVerticalAlignment(
                    VerticalAlignment.MIDDLE
                ).setPadding(5f)
                addCell(Cell().add(Paragraph("Area").setBold())).setVerticalAlignment(
                    VerticalAlignment.MIDDLE
                ).setPadding(5f)
                addCell(Cell().add(Paragraph("Property_Type").setBold())).setVerticalAlignment(
                    VerticalAlignment.MIDDLE
                ).setPadding(5f)
                addCell(Cell().add(Paragraph("Category").setBold())).setVerticalAlignment(
                    VerticalAlignment.MIDDLE
                ).setPadding(5f)
                addCell(Cell().add(Paragraph("Range").setBold())).setVerticalAlignment(
                    VerticalAlignment.MIDDLE
                ).setPadding(5f)
                addCell(Cell().add(Paragraph("Looking_For").setBold())).setVerticalAlignment(
                    VerticalAlignment.MIDDLE
                ).setPadding(5f)
                addCell(Cell().add(Paragraph("Facing").setBold())).setVerticalAlignment(
                    VerticalAlignment.MIDDLE
                ).setPadding(5f)
                addCell(Cell().add(Paragraph("Date").setBold())).setVerticalAlignment(
                    VerticalAlignment.MIDDLE
                ).setPadding(5f)
                addCell(Cell().add(Paragraph("Remarks").setBold())).setVerticalAlignment(
                    VerticalAlignment.MIDDLE
                ).setPadding(5f)

                // Populate table with customer data
                customers.forEach { customer ->
                    addCell(Cell().add(Paragraph(customer.name)))
                    addCell(Cell().add(Paragraph(customer.contactNumber.toString())))
                    addCell(Cell().add(Paragraph(customer.area)))
                    addCell(Cell().add(Paragraph(customer.propertyType)))
                    addCell(Cell().add(Paragraph(customer.inquiryCategory)))
                    addCell(Cell().add(Paragraph(customer.range)))
                    addCell(Cell().add(Paragraph(customer.lookingFor)))
                    addCell(Cell().add(Paragraph(customer.facing)))
                    addCell(Cell().add(Paragraph(customer.createdDateTime.toString())))
                    addCell(Cell().add(Paragraph(customer.remarks)))

                }
            }

            // Add table to the document
            document.add(table)

            // Close the document to save it
            document.close()

            // Notify the user that the PDF has been saved successfully
            Toast.makeText(
                this,
                "PDF created successfully! Path: ${pdfFile.absolutePath}",
                Toast.LENGTH_LONG
            ).show()

        } catch (e: IOException) {
            e.printStackTrace()
            // If an error occurs while saving the PDF, show a Toast
            Toast.makeText(this, "Error creating PDF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkNotificationPermission() {
        // For Android 13 and above (API level 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                // If notifications are not enabled, open the settings to enable them
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                }
                startActivity(intent)
            }
        }
    }

    private fun updateUI(isDarkMode: Boolean) {
        val rootLayout = findViewById<RelativeLayout>(R.id.homelinearLayout)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val themeSwitch = findViewById<SwitchCompat>(R.id.theme_switch)
        if (isDarkMode) {
            // Apply dark mode colors
            rootLayout.setBackgroundColor(resources.getColor(R.color.background_dark))
            recyclerView.setBackgroundColor(resources.getColor(R.color.background_dark))
            themeSwitch.setTextColor(resources.getColor(R.color.text_dark))
        } else {
            // Apply light mode colors
            rootLayout.setBackgroundColor(resources.getColor(R.color.background_light))
            recyclerView.setBackgroundColor(resources.getColor(R.color.background_light))
            themeSwitch.setTextColor(resources.getColor(R.color.text_light))

        }
    }
}



