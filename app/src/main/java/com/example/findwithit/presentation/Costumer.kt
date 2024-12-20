import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class Costumer(
    val name: String,
    val phono: Long,
    val date: Date,
    val area: String,
    val category: String,
    val remarks: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readLong(),
        Date(parcel.readLong()),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeLong(phono)
        parcel.writeLong(date.time)  // Convert Date to timestamp
        parcel.writeString(area)
        parcel.writeString(category)
        parcel.writeString(remarks)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Costumer> = object : Parcelable.Creator<Costumer> {
            override fun createFromParcel(parcel: Parcel): Costumer {
                return Costumer(parcel)
            }

            override fun newArray(size: Int): Array<Costumer?> {
                return arrayOfNulls(size)
            }
        }
    }
}
