package com.zaroslikov.myconstruction

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationItemView

class CustomAdapterMagazine(val products: List<Product>, val myRow: Int) :
    RecyclerView.Adapter<CustomAdapterMagazine.MyViewHolder>(), Listener {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomAdapterMagazine.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(myRow, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomAdapterMagazine.MyViewHolder, position: Int) {
        holder.nameTxt.setText(products.get(position).name)
        holder.categoryTxt.setText(products.get(position).category)
        holder.countTxt.setText(products.get(position).count.toString())
        holder.dateTxt.setText(products.get(position).date)
        if (R.layout.my_row_add == myRow) {
            holder.priceTxt.setText(products.get(position).price.toString())
        }

        holder.mainLayout.setOnClickListener {
            onClick(position, products.get(position))
        }

    }

    override fun getItemCount(): Int {
        return products.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // ToDO Конструктор

        val nameTxt = itemView.findViewById<TextView>(R.id.name_txt)
        val categoryTxt = itemView.findViewById<TextView>(R.id.category_txt)
        val countTxt = itemView.findViewById<TextView>(R.id.count_txt)
        val dateTxt = itemView.findViewById<TextView>(R.id.date_txt)
        val priceTxt = itemView.findViewById<TextView>(R.id.price_txt)

//            if (R.layout.my_row_add == myRow) {
//                val priceTxt = itemView.findViewById<TextView>(R.id.price_txt)
//            }

        val mainLayout = itemView.findViewById<LinearLayout>(R.id.mainLayout)


    }

    override fun onClick(position: Int, product: Product) {
        TODO("Not yet implemented")
    }

}

interface Listener{
    fun onClick(position:Int, product: Product)
}