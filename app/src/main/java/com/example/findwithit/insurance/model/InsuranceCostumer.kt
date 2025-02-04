package com.example.findwithit.insurance.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class InsuranceCostumer(
    @PrimaryKey(autoGenerate = true)val id:Int=0,
    var state:String,
    var regNo:String,
    var regDate:String,
    var ownerName:String,
    var address:String,
    var enginNo:String,
    var chasNo:String,
    var vehicleMake:String,
    var vehicleModel:String,
    var vehicleClass:String,
    var fuel: String,
    var saleAmount:String,
    var seatCapacity:String,
    var mobile:Long

): Parcelable {

}