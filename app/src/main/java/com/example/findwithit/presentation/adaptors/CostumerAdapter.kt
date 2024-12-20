package com.example.findwithit.presentation.adaptors

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.findwithit.R
import com.example.findwithit.presentation.CustomerDetailActivity

class CostumerAdapter(
    private var costumers: MutableList<Costumer>
) : RecyclerView.Adapter<CostumerAdapter.CostumerViewHolder>() {

    // ViewHolder for binding the customer data to the RecyclerView
    inner class CostumerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.nametext)
        val phono: TextView = itemView.findViewById(R.id.phontext)
        val area: TextView = itemView.findViewById(R.id.areatext)
        val category: TextView = itemView.findViewById(R.id.categorytext)
        val remarks: TextView = itemView.findViewById(R.id.remarkstext)
        val date: TextView = itemView.findViewById(R.id.datetext)
    }

    // Create a new ViewHolder and inflate the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostumerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.allcostomers, parent, false)
        return CostumerViewHolder(view)
    }

    // Bind the customer data to the views
    override fun onBindViewHolder(holder: CostumerViewHolder, position: Int) {
        val costumer = costumers[position]
        holder.name.text = costumer.name
        holder.phono.text = costumer.phono.toString()
        holder.date.text = costumer.date.toString()
        holder.area.text = costumer.area
        holder.category.text = costumer.category
        holder.remarks.text = costumer.remarks
        holder.itemView.setOnClickListener(){
            val intent= Intent(holder.itemView.context, CustomerDetailActivity::class.java)
            intent.putExtra("customer_data", costumer) // Pass the customer object
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return costumers.size
    }

    fun updateCustomers(newCustomers: MutableList<Costumer>) {
        this.costumers = newCustomers
        notifyDataSetChanged()  // Refresh the entire list
    }

    // Method to add a new customer
    fun addCustomer(newCustomer: Costumer) {
        this.costumers.add(newCustomer)
        notifyItemInserted(costumers.size - 1)  // Notify the adapter that a new item has been added
    }
}
