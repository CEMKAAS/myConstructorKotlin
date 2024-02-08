package com.zaroslikov.myconstruction

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import kotlinx.android.parcel.Parcelize

@Parcelize
class Product() : Parcelable {
    var name: String? = null
    var category: String? = null
    var count = 0.0
    var price = 0.0
    var suffix: String? = null
    var date: String? = null
    var id = 0

//    protected constructor(`in`: Parcel) {
//        name = `in`.readString()
//        category = `in`.readString()
//        count = `in`.readDouble()
//        price = `in`.readDouble()
//        suffix = `in`.readString()
//        date = `in`.readString()
//        id = `in`.readInt()
//    }

    constructor(
        id: Int,
        name: String?,
        category: String?,
        count: Double,
        price: Double,
        date: String?,
        suffix: String?
    ) : this() {
        this.id = id
        this.name = name
        this.category = category
        this.count = count
        this.price = price
        this.date = date
        this.suffix = suffix
    }

    constructor(name: String?, count: Double, suffix: String?) : this() {
        this.name = name
        this.count = count
        this.suffix = suffix
    }

    constructor(name: String?, suffix: String?, price: Double, date: String?) : this() {
        this.name = name
        this.price = price
        this.suffix = suffix
        this.date = date
    }

    constructor(name: String?, suffix: String?, price: Double, date: String?, count: Double) : this() {
        this.name = name
        this.price = price
        this.suffix = suffix
        this.date = date
        this.count = count
    }

    constructor(id: Int, name: String?, suffix: String?) : this() {
        this.id = id
        this.name = name
        this.suffix = suffix
    }

    override fun describeContents(): Int {
        return 0
    }

//    override fun writeToParcel(dest: Parcel, flags: Int) {
//        dest.writeString(name)
//        dest.writeString(category)
//        dest.writeDouble(count)
//        dest.writeDouble(price)
//        dest.writeString(suffix)
//        dest.writeString(date)
//        dest.writeInt(id)
//    }

    companion object {
    }

//    companion object CREATOR : Creator<Product> {
//        override fun createFromParcel(parcel: Parcel): Product {
//            return Product(parcel)
//        }
//
//        override fun newArray(size: Int): Array<Product?> {
//            return arrayOfNulls(size)
//        }
//    }
}