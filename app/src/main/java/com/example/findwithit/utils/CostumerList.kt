package com.example.findwithit.utils

import Costumer
import java.text.SimpleDateFormat
import java.util.Locale

object CostumerList {
    val customers: MutableList<Costumer> = mutableListOf(
        Costumer("John Doe", 9234567890, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse("01/01/2020")!!, "Vijayanagara", "Rent", "ok"),
        Costumer("Jane Smith", 9876543210, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse("02/02/2021")!!, "Banashankari", "lease", "Pending"),
        Costumer("Michael Brown", 9111223345, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse("15/03/2022")!!, "Rajajinagara", "Rent", "Done"),
        Costumer("Emily Davis", 5556677889, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse("30/05/2023")!!, "hosuru", "lease", "Pending")

    )
}
