import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.findwithit.insurance.model.InsuranceCostumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

// Function to extract content from Excel and convert it to InsuranceCostumer list
fun extractExcelContent(uri: Uri, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
    try {
        // Get input stream from the URI
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)!!
        val workbook = WorkbookFactory.create(inputStream)
        val sheet = workbook.getSheetAt(0) // Assume the first sheet contains the data

        // List to store the InsuranceCostumer objects
        val insuranceList = mutableListOf<InsuranceCostumer>()

        // Loop through the rows of the sheet (start from 1 to skip header)
        for (rowIndex in 1 until sheet.physicalNumberOfRows) {
            val row = sheet.getRow(rowIndex)

            // Extract data from each cell based on your InsuranceCostumer fields
            val state = row.getCell(0)?.toString()?.trim() ?: ""
            val regNo = row.getCell(1)?.toString()?.trim() ?: ""
            val regDate = row.getCell(2)?.toString()?.trim() ?: ""
            val ownerName = row.getCell(3)?.toString()?.trim() ?: ""
            val address = row.getCell(4)?.toString()?.trim() ?: ""
            val enginNo = row.getCell(5)?.toString()?.trim() ?: ""
            val chasNo = row.getCell(6)?.toString()?.trim() ?: ""
            val vehicleMake = row.getCell(7)?.toString()?.trim() ?: ""
            val vehicleModel = row.getCell(8)?.toString()?.trim() ?: ""
            val vehicleClass = row.getCell(9)?.toString()?.trim() ?: ""
            val fuel = row.getCell(10)?.toString()?.trim() ?: ""
            val saleAmount = row.getCell(11)?.toString()?.trim() ?: ""
            val seatCapacity = row.getCell(12)?.toString()?.trim() ?: ""
            val mobile = row.getCell(13)?.toString()?.toLongOrNull() ?: 0L

            // Skip rows with invalid data
            if (mobile == 0L) continue

            // Create InsuranceCostumer object from row data
            val insurance = InsuranceCostumer(
                state = state,
                regNo = regNo,
                regDate = regDate,
                ownerName = ownerName,
                address = address,
                enginNo = enginNo,
                chasNo = chasNo,
                vehicleMake = vehicleMake,
                vehicleModel = vehicleModel,
                vehicleClass = vehicleClass,
                fuel = fuel,
                saleAmount = saleAmount,
                seatCapacity = seatCapacity,
                mobile = mobile
            )

            // Add to the insurance list
            insuranceList.add(insurance)
        }

        // If valid insurance data exists, update the ViewModel or UI
        if (insuranceList.isNotEmpty()) {
            // Assuming `viewModel` is available in the context, you can call:
          //  viewModel.addAllInsurance(insuranceList)
            showToast(context, "Excel Data Imported Successfully")
        } else {
            showToast(context, "No valid data to import from Excel")
        }

    } catch (e: Exception) {
        e.printStackTrace()
        showToast(context, "Error importing Excel")
    }
}}

// Helper function to show toast messages
private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
