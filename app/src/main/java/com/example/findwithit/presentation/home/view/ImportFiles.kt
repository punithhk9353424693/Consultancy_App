package com.example.findwithit

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.findwithit.data.local.CustomerInquiry
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfDocument
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream
import com.example.findwithit.presentation.home.viewmodel.InquiryViewModel
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import java.text.SimpleDateFormat
import java.util.*

class ImportFiles(private val context: Context, private val inquiryViewModel: InquiryViewModel) {

    // PDF Import function using iText 7
    fun extractPdfContent(uri: Uri) {
        try {
            // Open the input stream to the PDF file
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)!!

            // Create PdfReader and PdfDocument objects using iText 7
            val pdfReader = PdfReader(inputStream)
            val pdfDocument = PdfDocument(pdfReader)

            // Get the number of pages using getNumberOfPages() (iText 7)
            val numberOfPages = pdfDocument.numberOfPages

            // Initialize a StringBuilder to hold the PDF content
            val pdfContent = StringBuilder()

            // Loop through each page to extract text
            for (i in 1..numberOfPages) {
                val page = pdfDocument.getPage(i)
                val pageContent = PdfTextExtractor.getTextFromPage(page)
                pdfContent.append(pageContent)
            }

            // Now process the PDF content and map it to data objects
            processPdfData(pdfContent.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error importing PDF", Toast.LENGTH_SHORT).show()
        }
    }
    private fun processPdfData(pdfContent: String) {
        val customerList = mutableListOf<CustomerInquiry>()
        val rows = pdfContent.split("\n")

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date()) // Using current date

        rows.forEach { row ->
            // Basic split by spaces (assuming a simple CSV-like structure in PDF)
            val fields = row.split("\\s+".toRegex())

            if (fields.size >= 10) {
                // Extracting fields from row, assuming each field's order is consistent
                val name = fields[0].trim()
                val phone = fields[1].trim().toLongOrNull()
                val area = fields[2].trim()
                val propertyType = fields[3].trim()
                val category = fields[4].trim()
                val range = fields[5].trim()
                val lookingFor = fields[6].trim()
                val facing = fields[7].trim()
                val date = fields[8].trim()
                var remarks = fields[9].trim()

                // **Fix**: If remarks contains month-like data, it's likely misclassified.
                // Check if the remarks field is likely a month name or part of the date and assign it correctly.
                val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

                if (remarks in monthNames) {
                    remarks = "No remarks" // If it looks like a month name, assign a default value
                }

                // Only process rows with valid phone numbers
                if (phone != null) {
                    val customer = CustomerInquiry(
                        name = name,
                        contactNumber = phone,
                        area = area,
                        propertyType = propertyType,
                        inquiryCategory = category,
                        range = range,
                        lookingFor = lookingFor,
                        facing = facing,
                        createdDateTime = currentDate,  // Use current date
                        remarks = remarks
                    )

                    customerList.add(customer)
                }
            }
        }

        if (customerList.isNotEmpty()) {
            // Insert the data into the database
            inquiryViewModel.addAllEnquiries(customerList)
            showToast("PDF Data Imported Successfully")
        } else {
            showToast("No valid data to import from PDF")
        }
    }

    // Function to extract content from Excel
    fun extractExcelContent(uri: Uri) {
        try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)!!
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0) // Assume the first sheet contains the data

            val customerList = mutableListOf<CustomerInquiry>()
            for (rowIndex in 1 until sheet.physicalNumberOfRows) { // Skip the header row
                val row = sheet.getRow(rowIndex)

                val name = row.getCell(0)?.toString()?.trim() ?: ""
                val contactNumber = row.getCell(1)?.toString()?.toLongOrNull() ?: 0L
                val area = row.getCell(2)?.toString()?.trim() ?: ""
                val propertyType = row.getCell(3)?.toString()?.trim() ?: ""
                val inquiryCategory = row.getCell(4)?.toString()?.trim() ?: ""
                val range = row.getCell(5)?.toString()?.trim() ?: ""
                val lookingFor = row.getCell(6)?.toString()?.trim() ?: ""
                val facing = row.getCell(7)?.toString()?.trim() ?: ""
                val createdDateTime = row.getCell(8)?.toString()?.trim() ?: ""
                var remarks = row.getCell(9)?.toString()?.trim() ?: ""
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val currentDate = dateFormat.format(Date()) // Using current date


                // **Fix**: If remarks contains month-like data, it's likely misclassified.
                // Check if the remarks field is likely a month name or part of the date and assign it correctly.
                val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                if (remarks in monthNames) {
                    remarks = "No remarks" // If it looks like a month name, assign a default value
                }

                // Only process rows with a valid contact number
                if (contactNumber != 0L) {
                    val customer = CustomerInquiry(
                        name = name,
                        contactNumber = contactNumber,
                        area = area,
                        propertyType = propertyType,
                        inquiryCategory = inquiryCategory,
                        range = range,
                        lookingFor = lookingFor,
                        facing = facing,
                        createdDateTime = currentDate,
                        remarks = remarks
                    )

                    customerList.add(customer)
                }
            }

            if (customerList.isNotEmpty()) {
                inquiryViewModel.addAllEnquiries(customerList)
                showToast("Excel Data Imported Successfully")
            } else {
                showToast("No valid data to import from Excel")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("Error importing Excel")
        }
    }

    // Helper function to show toast messages
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

