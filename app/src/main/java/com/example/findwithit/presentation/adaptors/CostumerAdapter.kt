package com.example.findwithit.presentation.adaptors

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.findwithit.R
import com.example.findwithit.data.local.CustomerInquiry
import com.example.findwithit.presentation.CustomerDetailActivity
import com.example.findwithit.presentation.home.viewmodel.InquiryViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CostumerAdapter(
    private var costumers: MutableList<CustomerInquiry>,
    private val inquiryViewModel: InquiryViewModel
) : RecyclerView.Adapter<CostumerAdapter.CostumerViewHolder>() {
    // ViewHolder for binding the customer data to the RecyclerView

    inner class CostumerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nametext)
        val phono: TextView = itemView.findViewById(R.id.phontext)
        val area: TextView = itemView.findViewById(R.id.areatext)
        val category: TextView = itemView.findViewById(R.id.categorytext)
        val remarks: TextView = itemView.findViewById(R.id.remarkstext)
        val date: TextView = itemView.findViewById(R.id.datetext)
        val prsentAddres = itemView.findViewById<TextView>(R.id.rangeTextView)
        val facing = itemView.findViewById<TextView>(R.id.facingTextView)
        val lookingFor = itemView.findViewById<TextView>(R.id.lookinforTextView)
        val propertyTYpe = itemView.findViewById<TextView>(R.id.PropertypeEdit)
        val editbtn = itemView.findViewById<ImageButton>(R.id.editBtn)
        val deleteBtn = itemView.findViewById<ImageButton>(R.id.deleteImagebtn)
        val faverateBtn = itemView.findViewById<ImageButton>(R.id.faverateImagebtn)
        val phonecall = itemView.findViewById<ImageButton>(R.id.phonecall)
        val message = itemView.findViewById<ImageButton>(R.id.message)
        val card = itemView.findViewById<LinearLayout>(R.id.LinearlayoutColor)
        val linear = itemView.findViewById<LinearLayout>(R.id.linearhome)
    }

    // Create a new ViewHolder and inflate the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostumerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.allcostomers, parent, false)
        return CostumerViewHolder(view)
    }

    // Bind the customer data to the views
    override fun onBindViewHolder(holder: CostumerViewHolder, position: Int) {
        val costumer = costumers[position]
        val isExpired = isCustomerExpired(costumer.createdDateTime)

        holder.name.text = costumer.name
        holder.phono.text = costumer.contactNumber.toString()
        holder.date.text = costumer.createdDateTime.toString()
        holder.area.text = costumer.area
        holder.category.text = costumer.inquiryCategory
        holder.remarks.text = costumer.remarks
        holder.prsentAddres.text = costumer.range
        holder.lookingFor.text = costumer.lookingFor
        holder.facing.text = costumer.facing
        holder.propertyTYpe.text = costumer.propertyType
        val context = holder.itemView.context

        val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        val textColor = if (isDarkMode) context.getColor(R.color.white) else context.getColor(R.color.black)

        holder.name.setTextColor(textColor)
        holder.phono.setTextColor(textColor)
        holder.date.setTextColor(textColor)
        holder.area.setTextColor(textColor)
        holder.category.setTextColor(textColor)
        holder.remarks.setTextColor(textColor)
        holder.prsentAddres.setTextColor(textColor)
        holder.lookingFor.setTextColor(textColor)
        holder.facing.setTextColor(textColor)
        holder.propertyTYpe.setTextColor(textColor)

        val cardBackgroundColor = if (isDarkMode) {
            R.color.background_dark
        } else {
            R.color.background_light
        }
        holder.card.setBackgroundColor(context.getColor(cardBackgroundColor))

        val layoutBackgroundColor = if (isDarkMode) {
            R.color.background_dark
        } else {
            R.color.background_light
        }
        holder.linear.setBackgroundColor(context.getColor(layoutBackgroundColor))



        val borderColor = if (isDarkMode) context.getColor(R.color.white) else context.getColor(R.color.black)
        val cardView = holder.card
        val strokeWidth = context.resources.getDimensionPixelSize(com.google.android.material.R.dimen.design_bottom_navigation_shadow_height)
        val borderDrawable = GradientDrawable()

        borderDrawable.setColor(Color.TRANSPARENT) // Set transparent background
        borderDrawable.setStroke(strokeWidth, borderColor) // Set the border color and stroke width
        borderDrawable.cornerRadius = 16f // Set rounded corners (you can adjust the value)

        // Set the rounded border drawable as the background of the card
        holder.card.background = borderDrawable





        val cardDrawable = cardView.background

        if (cardDrawable is GradientDrawable) {
            cardDrawable.setStroke(strokeWidth, borderColor)
        } else if (cardDrawable is ColorDrawable) {
            // If the background is a ColorDrawable, convert to GradientDrawable
            val newDrawable = GradientDrawable()
            newDrawable.setColor((cardDrawable as ColorDrawable).color)  // Set the color from ColorDrawable
            newDrawable.setStroke(strokeWidth, borderColor)
            cardView.background = newDrawable
        }



        if (isExpired) {
            val expiredColor = context.getColor(R.color.grey)
            holder.card.setBackgroundColor(expiredColor)
        }


        if (isExpired) {
            holder.editbtn.isEnabled = false
            holder.faverateBtn.isEnabled = false
            holder.phonecall.isEnabled = false
            holder.message.isEnabled = false

            holder.editbtn.alpha = 0.5f
            holder.faverateBtn.alpha = 0.5f
            holder.phonecall.alpha = 0.5f
            holder.message.alpha = 0.5f
        } else {
            holder.editbtn.isEnabled = true
            holder.faverateBtn.isEnabled = true
            holder.phonecall.isEnabled = true
            holder.message.isEnabled = true

            holder.editbtn.alpha = 1f
            holder.faverateBtn.alpha = 1f
            holder.phonecall.alpha = 1f
            holder.message.alpha = 1f
        }

        holder.phonecall.tooltipText = "Call"
        holder.message.tooltipText = "message"
        holder.editbtn.tooltipText = "Edit Costumer"
        holder.deleteBtn.tooltipText = "Delete"
        holder.faverateBtn.tooltipText = "Favorites"
        holder.phonecall.setOnClickListener() {
            callPhoneNumber(holder.itemView.context, costumer.contactNumber.toString())
        }
        holder.message.setOnClickListener() {
            openWhatsAppChat(holder.itemView.context, costumer.contactNumber.toString())
        }
        if (costumer.isFavorite) {
            holder.faverateBtn.setImageResource(R.drawable.ic_favtfill)
            val redcolor = holder.itemView.context.getColor(R.color.red)
            holder.faverateBtn.setColorFilter(redcolor)
        } else {
            holder.faverateBtn.setImageResource(R.drawable.ic_favoriteonly)
        }


        holder.faverateBtn.setOnClickListener {
            // Toggle the favorite status
            inquiryViewModel.toggleFavorites(costumer)

            // Update the UI to reflect the favorite status change
            notifyItemChanged(position)

            // Show the toast message depending on the favorite status
            if (costumer.isFavorite) {
                holder.faverateBtn.setImageResource(R.drawable.ic_favtfill)
                val redcolor = holder.itemView.context.getColor(R.color.red)
                holder.faverateBtn.setColorFilter(redcolor)
                Toast.makeText(
                    holder.itemView.context,
                    "Removed from Favorite List",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                holder.faverateBtn.setImageResource(R.drawable.ic_favoriteonly)
                Toast.makeText(
                    holder.itemView.context,
                    "Added to Favorite List",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }



        holder.deleteBtn.setOnClickListener() {
            val deletedcostumer = costumers[position]
            val deletePosition = position
            inquiryViewModel.deleteInquiry(deletedcostumer.id)
            costumers.removeAt(position)
            notifyItemRemoved(position)
            val snackbar = Snackbar.make(holder.itemView, "Costumer deleted", Snackbar.LENGTH_LONG)
            snackbar.setAction("UNDO") {
                inquiryViewModel.addInquiry(deletedcostumer)
                costumers.add(deletePosition, deletedcostumer)
                notifyItemInserted(deletePosition)
                Toast.makeText(holder.itemView.context, "Customer restored", Toast.LENGTH_SHORT)
                    .show()
            }
            snackbar.show()
        }
        holder.editbtn.setOnClickListener() {
            val context = it.context
            val intent = Intent(holder.itemView.context, CustomerDetailActivity::class.java)
            intent.putExtra("customer_data", costumer) // Pass the customer object
            holder.itemView.context.startActivity(intent)
        }

    }

    private fun isCustomerExpired(createdDateTime: String): Boolean {
        // Define the possible date formats
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
                Log.e("CustomerAdapter", "Error parsing date: $createdDateTime", e2)
            }
        }

        // If parsing failed for both formats, return false
        if (createdDate == null) {
            Log.e("CustomerAdapter", "Failed to parse date: $createdDateTime")
            return false
        }

        // Get the current date and add 30 days to the creation date
        val currentDate = Calendar.getInstance().time
        val calendar = Calendar.getInstance()
        calendar.time = createdDate
        calendar.add(Calendar.DAY_OF_YEAR, 60) // Adding 30 days to the creation date
        val expiryDate = calendar.time

        // Check if the current date is after the expiry date
        return currentDate.after(expiryDate)
    }





    override fun getItemCount(): Int {
        return costumers.size
    }

    fun updateCustomers(newCustomers: List<CustomerInquiry>) {
        this.costumers = newCustomers.toMutableList()
        notifyDataSetChanged()  // Refresh the entire list
    }

    fun updateCustomersFav(newFavorites: List<CustomerInquiry>) {
        costumers = newFavorites.toMutableList()
        notifyDataSetChanged()

        if (costumers.isEmpty()) {
            Log.d("CostumerAdapter", "No favorite customers")
        }
    }


    // Method to add a new customer
    fun addCustomer(newCustomer: CustomerInquiry) {
        this.costumers.add(newCustomer)
        notifyItemInserted(costumers.size - 1)  // Notify the adapter that a new item has been added
    }


    fun callPhoneNumber(context: Context, phono: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phono"))
        context.startActivity(intent)
    }

    fun openWhatsAppChat(context: Context, phono: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$phono")
                setPackage("com.whatsapp")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Whatsapp Not Installed", Toast.LENGTH_SHORT).show()
        }
    }

    fun removeItem(position: Int) {
        costumers.removeAt(position)
        notifyItemRemoved(position)

    }

    fun getItemAtPosition(position: Int): CustomerInquiry {
        return costumers[position]
    }

    fun restoreItem(customer: CustomerInquiry, position: Int) {
        costumers.add(position, customer)
        notifyItemInserted(position)

    }
}
