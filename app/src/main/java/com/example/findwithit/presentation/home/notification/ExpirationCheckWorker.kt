import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.findwithit.R
import com.example.findwithit.data.local.CustomerInquiry
import com.example.findwithit.presentation.home.view.HomeActivity
import com.example.findwithit.presentation.home.viewmodel.InquiryViewModel
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ExpirationCheckWorker(
    private val context: Context,
    private val inquiryViewModel: InquiryViewModel,
    private val lifecycleOwner: LifecycleOwner
) {

    // Create notification channel
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "customer_notifications_channel"
            val channelName = "Customer Expiry Notifications"
            val channelDescription = "Notifications for expiring customer details"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Send notifications
    fun sendExpiryNotification(customerName: String, customerId: Int, message: String) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val notificationId = customerId // Unique ID for the notification based on customer ID

        // Create an intent to open the HomeActivity
        val intent = Intent(context, HomeActivity::class.java).apply {
            putExtra("customer_id", customerId)  // Pass customer ID
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Create a PendingIntent to launch the activity when the notification is clicked
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create the notification with a custom message
        val notification = NotificationCompat.Builder(context, "customer_notifications_channel")
            .setSmallIcon(R.drawable.ic_notification)  // Replace with your icon
            .setContentTitle("Customer Notification")
            .setContentText(message)  // Display the custom message
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)  // Notification is cleared once clicked
            .setContentIntent(pendingIntent)  // Set the PendingIntent for redirection
            .build()

        // Send the notification
        notificationManager.notify(notificationId, notification)
        Log.d("Notification", "Notification sent for $customerName with message: $message")
    }

    fun calculateExpiryDate(createdDateTime: String): Date? {
        // Define multiple possible date formats
        val shortFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)  // Format: "19/01/2025"
        val longFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)  // Format: "Sun Jan 19 15:31:28 GMT+05:30 2025"

        var createdDate: Date? = null

        try {
            // Try parsing as a short date
            createdDate = shortFormat.parse(createdDateTime.trim())
        } catch (e: ParseException) {
            // If short format fails, try long format
            try {
                createdDate = longFormat.parse(createdDateTime.trim())
            } catch (e2: ParseException) {
                Log.e("ExpirationCheckWorker", "Failed to parse date: $createdDateTime", e2)
            }
        }

        // If parsing failed for both formats, return null
        if (createdDate == null) {
            Log.e("ExpirationCheckWorker", "Failed to parse date: $createdDateTime")
            return null
        }

        // Now calculate expiry date (30 days after the created date)
        val calendar = Calendar.getInstance().apply {
            time = createdDate
            add(Calendar.DAY_OF_YEAR, 60) // Add 30 days to calculate expiry
            set(Calendar.HOUR_OF_DAY, 0)  // Set to start of day
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return calendar.time
    }





    // Check whether customers have expired or are expiring soon
    fun checkCustomerExpiry() {
        val currentDate = System.currentTimeMillis() // Current system time in milliseconds
        val warningPeriod = 3 * 24 * 60 * 60 * 1000 // 3 days in milliseconds

        lifecycleOwner.lifecycleScope.launch {
            inquiryViewModel.inquiries.collect { customers ->
                val expiredCustomers = mutableListOf<CustomerInquiry>()
                val expiringSoonCustomers = mutableListOf<CustomerInquiry>()

                customers.forEach { customer ->
                    val expiryDate = calculateExpiryDate(customer.createdDateTime)

                    if (expiryDate != null) {
                        val timeToExpire = expiryDate.time - currentDate

                        Log.d("Expiry", "Time to expiry for ${customer.name}: $timeToExpire")

                        // Case 1: Customer is expired
                        if (timeToExpire <= 0) {
                            expiredCustomers.add(customer)
                        }
                        // Case 2: Customer is expiring soon (within the next 3 days)
                        else if (timeToExpire in 0..warningPeriod) {
                            expiringSoonCustomers.add(customer)
                        }
                    }
                }

                // Send notifications for expired customers
                if (expiredCustomers.isNotEmpty()) {
                    val customerNames = expiredCustomers.joinToString(", ") { it.name }
                    sendExpiryNotification(
                        "Customers Expired",
                        -1, // Optional: Use a special ID for a group notification
                        "The following customers have expired: $customerNames"
                    )
                }

                // Send notifications for customers expiring soon
                if (expiringSoonCustomers.isNotEmpty()) {
                    val customerNames = expiringSoonCustomers.joinToString(", ") { it.name }
                    sendExpiryNotification(
                        "Customers Expiring Soon",
                        -1, // Optional: Use a special ID for a group notification
                        "The following customers are expiring soon: $customerNames"
                    )
                }
            }
        }
    }
}
