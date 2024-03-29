package com.zaroslikov.myconstruction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapterMagazine(private val products: List<Product>, private var myRow: Int, val listener: Listener) :
    RecyclerView.Adapter<CustomAdapterMagazine.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(myRow, parent, false)
        return MyViewHolder(view, myRow)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.nameTxt.text = products[position].name
        holder.categoryTxt.text = products[position].category
        holder.countTxt.text = products[position].count.toString()
        holder.dateTxt.text = products[position].date
        if (R.layout.my_row_add == myRow) {
            holder.priceTxt.text = products[position].price.toString()
        }

        holder.mainLayout.setOnClickListener {
            listener.onClick(position, products[position])
        }

    }

    override fun getItemCount(): Int {
        return products.size
    }

    class MyViewHolder(itemView: View, myRow: Int) : RecyclerView.ViewHolder(itemView) {

        val nameTxt: TextView = itemView.findViewById(R.id.name_txt)
        val categoryTxt: TextView = itemView.findViewById(R.id.category_txt)
        val countTxt: TextView = itemView.findViewById(R.id.count_txt)
        val dateTxt: TextView = itemView.findViewById(R.id.date_txt)
        var priceTxt: TextView = itemView.findViewById(R.id.price_txt)
        val mainLayout: LinearLayout = itemView.findViewById(R.id.mainLayout)

        init {
            if (R.layout.my_row_add == myRow) {
                priceTxt = itemView.findViewById<TextView>(R.id.price_txt)
            }
        }
    }

    interface Listener {
        fun onClick(position: Int, product: Product)
    }

}

