package com.example.findwithit.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date


@Entity
@Parcelize
data class CustomerInquiry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val contactNumber: Long,
    val area: String,
    val propertyType: String,
    val inquiryCategory: String,
    val range: String,
    val lookingFor:String,
    val facing:String,
    val createdDateTime: String,
    val remarks: String,
    val isFavorite:Boolean=false

    ): Parcelable

