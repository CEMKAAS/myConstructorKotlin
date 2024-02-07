package com.zaroslikov.myconstruction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(var productsList:List<Product>, var fragment: Boolean): RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.my_row_product, parent,false)
        return ProductAdapter.MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductAdapter.MyViewHolder, position: Int) {

        if (fragment){
            holder.count.text  = productsList[position].price.toString()
       }else{
            holder.count.text  = productsList[position].count.toString()
       }
        holder.products.text = productsList[position].name
        holder.unit.text = productsList[position].suffix
    }

    override fun getItemCount(): Int {
      return productsList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val products = itemView.findViewById<TextView>(R.id.products_text)
        val count = itemView.findViewById<TextView>(R.id.count_text)
        val unit = itemView.findViewById<TextView>(R.id.unit_text)

    }

}