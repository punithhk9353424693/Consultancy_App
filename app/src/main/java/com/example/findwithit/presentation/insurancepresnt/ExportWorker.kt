import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.findwithit.data.local.AppDatabase
import com.example.findwithit.domain.usecase.GetAllInsurances
import com.example.findwithit.insurance.model.InsuranceCostumer
import com.example.findwithit.insurance.repository.InsurancePerson
import com.example.findwithit.presentation.home.viewmodel.InsuranceViewModel
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class ExportWorker( context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val userDao = AppDatabase.getInstance(applicationContext).insuranceDao()

        val usersJson = inputData.getString("user_data") ?: return Result.failure()
        val userList = Gson().fromJson(usersJson, Array<InsuranceCostumer>::class.java).toList()

        userDao.addAllInsurance(userList)

        return Result.success()
    }
}
