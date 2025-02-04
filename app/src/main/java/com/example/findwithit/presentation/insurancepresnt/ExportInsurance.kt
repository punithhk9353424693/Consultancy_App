package com.example.findwithit.presentation.insurancepresnt

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.example.findwithit.insurance.model.InsuranceCostumer
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

fun exportFromHere(insuranceCostumers: List<InsuranceCostumer>, context: Context) {
    Log.d("ins", "exportFromHere is triggered ")

    val builder = AlertDialog.Builder(context)
    val input = EditText(context)
    input.hint = "Enter the Excel File Name"
    builder.setTitle("Enter the Excel File Name").setView(input)

    builder.setPositiveButton("Save") { _, _ ->
        val filename = input.text.toString().trim()
        if (filename.isNotEmpty() && isValidFileName(filename)) {
            saveExcelFiletoStorage(filename, insuranceCostumers, context)
        } else if (filename.isEmpty()) {
            Toast.makeText(context, "File name cannot be empty", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Invalid File name", Toast.LENGTH_SHORT).show()

        }
    }.setNegativeButton("cancel", null)
        .show()
}

fun saveExcelFiletoStorage(
    fileName: String,
    insurances: List<InsuranceCostumer>,
    context: Context
) {
    val executor = Executors.newSingleThreadExecutor()
    executor.submit {
        try {
            val workBook: Workbook = XSSFWorkbook()
            val sheet = workBook.createSheet("InsuranceList")
            val headerRow = sheet.createRow(0)

            // Set headers for the columns
            val headers = listOf(
                "STATE", "REG NO", "REG DATE", "OWNER NAME", "ADDRESS",
                "ENGINE NO", "CHASIS NO", "VEHICLE MAKE", "VEHICLE MODEL VARIANT",
                "VEHICLE CLASS", "FUEL", "SALE AMOUNT", "SEAT CAPACITY", "MOBILE"
            )

            headers.forEachIndexed { index, header ->
                headerRow.createCell(index).setCellValue(header)
            }

            // Fill in the data from the insurance list
            insurances.forEachIndexed { index, insurance ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(insurance.state)
                row.createCell(1).setCellValue(insurance.regNo)
                row.createCell(2).setCellValue(insurance.regDate)
                row.createCell(3).setCellValue(insurance.ownerName)
                row.createCell(4).setCellValue(insurance.address)
                row.createCell(5).setCellValue(insurance.enginNo)
                row.createCell(6).setCellValue(insurance.chasNo)
                row.createCell(7).setCellValue(insurance.vehicleMake)
                row.createCell(8).setCellValue(insurance.vehicleModel)
                row.createCell(9).setCellValue(insurance.vehicleClass)
                row.createCell(10).setCellValue(insurance.fuel)
                row.createCell(11).setCellValue(insurance.saleAmount)
                row.createCell(12).setCellValue(insurance.seatCapacity)
                row.createCell(13).setCellValue(insurance.mobile.toString())
            }
                val downloadDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (downloadDir == null || !downloadDir.exists()) {
                    downloadDir.mkdirs()
                }
                val excelFile = File(downloadDir, "$fileName.xlsx")
                val fileOut = FileOutputStream(excelFile)
                workBook.write(fileOut)
                fileOut.close()
                workBook.close()
                (context as? Activity)?.runOnUiThread {
                    Toast.makeText(
                        context,
                        "Excel file saved at: ${excelFile.absolutePath}",
                        Toast.LENGTH_LONG
                    ).show()


            }
        } catch (e: Exception) {
            e.printStackTrace()
            (context as? Activity)?.runOnUiThread {
                Toast.makeText(context, "Error exporting to Excel", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun isValidFileName(fileName: String): Boolean {
    val illegalFileName =
        listOf("/", "\\", "?", ">", "<", "|", "*", ":", ";", "@", "%", "^", "(", ")")
    return illegalFileName.none { fileName.contains(it) }
}