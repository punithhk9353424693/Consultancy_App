import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.example.findwithit.insurance.model.InsuranceCostumer
import com.example.findwithit.presentation.home.viewmodel.HandlerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

class ImportingInsurance(
    private val context: Context,
    private val viewModel: HandlerViewModel
) {

    suspend fun extractExcelContent(uri: Uri, applicationContext: Context): List<InsuranceCostumer> {
        return withContext(Dispatchers.IO) {
            val batchList = mutableListOf<InsuranceCostumer>()

            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                inputStream?.use { stream ->
                    val workbook = WorkbookFactory.create(stream)
                    val sheet = workbook.getSheetAt(0)

                    for (rowIndex in 1 until sheet.physicalNumberOfRows) {
                        val row = sheet.getRow(rowIndex)

                        val insurance = InsuranceCostumer(
                            state = row.getCell(0)?.toString()?.trim() ?: "",
                            regNo = row.getCell(1)?.toString()?.trim() ?: "",
                            regDate = row.getCell(2)?.toString()?.trim() ?: "",
                            ownerName = row.getCell(3)?.toString()?.trim() ?: "",
                            address = row.getCell(4)?.toString()?.trim() ?: "",
                            enginNo = row.getCell(5)?.toString()?.trim() ?: "",
                            chasNo = row.getCell(6)?.toString()?.trim() ?: "",
                            vehicleMake = row.getCell(7)?.toString()?.trim() ?: "",
                            vehicleModel = row.getCell(8)?.toString()?.trim() ?: "",
                            vehicleClass = row.getCell(9)?.toString()?.trim() ?: "",
                            fuel = row.getCell(10)?.toString()?.trim() ?: "",
                            saleAmount = row.getCell(11)?.toString()?.trim() ?: "",
                            seatCapacity = row.getCell(12)?.toString()?.trim() ?: "",
                            mobile = row.getCell(13)?.toString()?.toLongOrNull() ?: 0L
                        )

                        if (insurance.mobile != 0L) {
                            batchList.add(insurance)
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error importing Excel: ${e.message}")
                }
                e.printStackTrace()
            }

            batchList  // Return the extracted list
        }
    }

    private fun saveBatch(batch: List<InsuranceCostumer>) {
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.importUsers(batch)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
