package com.zaroslikov.myconstruction.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.zaroslikov.myconstruction.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit

class AdapterProject(
    var id: List<Int>,
    var name: List<String>,
    private var data: List<String>,
    var fragment: Boolean, var listener: Listener
) :
    RecyclerView.Adapter<AdapterProject.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterProject.ViewHolder {
        val cv = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_captioned_image, parent, false) as MaterialCardView
        return AdapterProject.ViewHolder(cv)
    }

    override fun onBindViewHolder(holder: AdapterProject.ViewHolder, position: Int) {
        val cardView: MaterialCardView = holder.cardView

        val imageView = cardView.findViewById<View>(R.id.info_image) as ImageView

        //Установка картинки в карту
        val drawable = cardView.resources.getDrawable(R.drawable.baseline_home_work_24)

        imageView.setImageDrawable(drawable)
        imageView.contentDescription = "22"
        val diff: Long
        val textCard: String

        if (fragment) {
            val calendar = Calendar.getInstance()
            val dateBefore22: String = data[position].toString()
            val dateBefore222 =
                (calendar[Calendar.DAY_OF_MONTH] + 1).toString() + "." + (calendar[Calendar.MONTH] + 1) + "." + calendar[Calendar.YEAR]

            val myFormat = SimpleDateFormat("dd.MM.yyyy")

            try {
                val date1 = myFormat.parse(dateBefore22)
                val date2 = myFormat.parse(dateBefore222)
                diff = date2.time - date1.time

            } catch (e: ParseException) {
                throw RuntimeException(e)
            }

            textCard =
                "Идет " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toString() + " день "

        } else {

            val date: List<String> =
                data[position].split(" - ")
            val dateBegin = date[0]
            val dateEnd = date[1]

            val myFormat = SimpleDateFormat("dd.MM.yyyy")

            try {
                val date1 = myFormat.parse(dateBegin)
                val date2 = myFormat.parse(dateEnd)
                diff = date2.time - date1.time

            } catch (e: ParseException) {
                throw RuntimeException(e)
            }

            textCard = "Закончилось за " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
                .toString() + " день "
        }

        val textView = cardView.findViewById<View>(R.id.name) as TextView
        textView.text = name[position] // имя проекта

        val textView1 = cardView.findViewById<View>(R.id.dayEnd) as TextView
        textView1.text = textCard //Какой день

        cardView.setOnClickListener {
            listener.onClick(position,
                name[position],
                data[position],
                id[position])
        }
    }

    override fun getItemCount(): Int {
        return id.size
    }


    class ViewHolder(var cardView: MaterialCardView) : RecyclerView.ViewHolder(
        cardView
    )

    interface Listener {
        fun onClick(position: Int, name: String, data: String, id: Int)
    }

}

