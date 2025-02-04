package com.example.findwithit.presentation.adaptors

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.findwithit.insurance.model.InsuranceCostumer
import com.example.findwithit.R
import com.example.findwithit.data.local.CustomerInquiry
import com.example.findwithit.presentation.home.viewmodel.InsuranceViewModel
import com.example.findwithit.presentation.insurancepresnt.InsuranceDetailsActivity
import com.example.findwithit.presentation.insurancepresnt.UpdateInsuranceActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class InsurancAdapter(
    private var insurances: MutableList<InsuranceCostumer>,
    val viewModel: InsuranceViewModel
) : RecyclerView.Adapter<InsurancAdapter.InsuranceViewHolder>() {
    inner class InsuranceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ownerName: TextView = itemView.findViewById(R.id.ownerName)
        val mobileNo: TextView = itemView.findViewById(R.id.mobileNo)
        val vehicleType: TextView = itemView.findViewById(R.id.Vehicletype)
        val phonecal: ImageButton = itemView.findViewById(R.id.phonecallInsurance)
        val edit: ImageButton = itemView.findViewById(R.id.editInsurance)
        val delete: ImageButton = itemView.findViewById(R.id.deleteinsurance)
        val viewEye: ImageButton = itemView.findViewById(R.id.viewEyebtnInsurance)
        val messageInsurance: ImageButton = itemView.findViewById(R.id.messageInsurance)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InsuranceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.insuranceadapterlayout, parent, false)
        return InsuranceViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: InsuranceViewHolder,
        position: Int
    ) {
        val insurance = insurances[position]
        holder.ownerName.text = insurance.ownerName
        holder.mobileNo.text = insurance.mobile.toString()
        holder.vehicleType.text = insurance.vehicleClass
        val context = holder.itemView.context

        holder.phonecal.setOnClickListener() {
            callInsurancePerson(holder.itemView.context, insurance.mobile.toString())
        }

        holder.messageInsurance.setOnClickListener {
            val phono = holder.mobileNo.text.toString()
            if (phono.isNotEmpty()) {
                openWhatsAppChat(holder.itemView.context, phono) // Pass the phone number
            } else {
                Toast.makeText(context, "Phone number is empty", Toast.LENGTH_SHORT).show()
            }
        }
        holder.delete.setOnClickListener {
            val deletedCustomer = insurances[position]
            val deletePosition = position

            // Remove the item from the list
            insurances.removeAt(deletePosition)
            notifyItemRemoved(deletePosition)

            // Show Snackbar with Undo option
            val snackbar = Snackbar.make(holder.itemView, "Customer deleted", Snackbar.LENGTH_LONG)
            snackbar.setAction("UNDO") {
                // Restore the deleted item
                insurances.add(deletePosition, deletedCustomer)
                notifyItemInserted(deletePosition)
                viewModel.viewModelScope.launch() {
                    viewModel.insertInsurance(deletedCustomer) // Reinsert into the database
                }
            }
            snackbar.show()

            // Delete from the database
            viewModel.deleteInsurance(deletedCustomer.id)
        }
        holder.viewEye.setOnClickListener {
            val intent = Intent(context, InsuranceDetailsActivity::class.java).apply {
                putExtra("insuranceDetails", insurance) // Pass the InsuranceCostumer object
            }
            context.startActivity(intent)
        }
        holder.edit.setOnClickListener {
            val intent = Intent(context, UpdateInsuranceActivity::class.java).apply {
                putExtra("insuranceDetails", insurance) // Pass the InsuranceCostumer object
            }
            context.startActivity(intent)
        }

    }


    override fun getItemCount(): Int {
        return insurances.size
    }

    fun addInsurance(newInsurance: InsuranceCostumer) {
        this.insurances.add(newInsurance)
        notifyItemInserted(insurances.size - 1)  // Notify the adapter that a new item has been added
    }

    fun submitList(insuranceList: List<InsuranceCostumer>) {
        this.insurances = insuranceList.toMutableList()
        notifyDataSetChanged()

    }

    fun callInsurancePerson(context: Context, mobile: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$mobile"))
        context.startActivity(intent)

    }
    fun openWhatsAppChat(context: Context, phono: String) {
        try {
            // Ensure the phone number starts with a '+' and includes the country code
            val cleanedPhoneNumber = if (phono.startsWith("+")) {
                phono.replace("[^0-9+]".toRegex(), "") // Remove unwanted characters
            } else {
                "+$phono".replace("[^0-9+]".toRegex(), "") // Add '+' if missing
            }

            // Check if the phone number is valid (at least 10 digits including country code)
            if (cleanedPhoneNumber.length >= 10) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/$cleanedPhoneNumber")
                    setPackage("com.whatsapp")
                }
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Invalid phone number", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp Not Installed", Toast.LENGTH_SHORT).show()
        }

    }

}