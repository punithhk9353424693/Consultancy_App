package com.example.findwithit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class CustomerInquiry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val contactNumber: String,
    val email: String,
    val propertyLocation: String,
    val inquiryCategory: String, // Rent, Lease, Buy
    val createdDateTime: Long,
    val reminderDateTime: Long? = null,
    val notes: String? = null
)