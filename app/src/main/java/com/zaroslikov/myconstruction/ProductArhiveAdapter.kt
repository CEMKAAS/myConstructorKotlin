package com.zaroslikov.myconstruction


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ProductArhiveAdapter(private var productsList: List<Product>) :
    RecyclerView.Adapter<ProductArhiveAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.my_row_product_arhive, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductArhiveAdapter.MyViewHolder, position: Int) {
        holder.products.setText(productsList[position].name)
        holder.count.setText("${productsList[position].count}")
        holder.unit.setText(productsList[position].suffix)
        holder.price.setText("${productsList[position].price} ₽")
    }

    override fun getItemCount(): Int {
        return productsList.size;
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val products = itemView.findViewById<TextView>(R.id.products_text)
        val count = itemView.findViewById<TextView>(R.id.count_text)
        val unit = itemView.findViewById<TextView>(R.id.unit_text)
        val price = itemView.findViewById<TextView>(R.id.price_txt)
    }
}