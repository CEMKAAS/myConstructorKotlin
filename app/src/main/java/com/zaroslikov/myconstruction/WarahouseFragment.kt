package com.zaroslikov.myconstruction

import android.R
import android.app.ActionBar
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.*
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.zaroslikov.myconstruction.db.MyConstanta
import com.zaroslikov.myconstruction.db.MyDatabaseHelper


class WarahouseFragment : Fragment() {

    lateinit var myDB : MyDatabaseHelper

    var productAllList = mutableListOf<Product>()
    var productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout = inflater.inflate(R.layout.fragment_warahouse, container, false)
        myDB = MyDatabaseHelper(requireContext())

        val fab = requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.extended_fab)
        fab.visibility = View.GONE

        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.title = "Мой Склад"
        appBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        appBar.menu.findItem(R.id.deleteAll).isVisible = false
        appBar.menu.findItem(R.id.magazine).isVisible = false
        appBar.menu.findItem(R.id.filler).isVisible = false
        appBar.menu.findItem(R.id.moreAll).isVisible = true
        appBar.setOnMenuItemClickListener { item: MenuItem ->
            val position = item.itemId
            if (position == R.id.moreAll) {
                replaceFragment(InFragment())
                appBar.title = "Информация"
            }
            true
        }
        appBar.setNavigationOnClickListener { replaceFragment(MenuProjectFragment()) }

        val bundle = this.arguments
        if (bundle != null) {
            nameProject = bundle.getString("name")
            dateProject = bundle.getString("date")
            idProject = bundle.getInt("id")
        }

        var idProject = MainActivity().projectNumer

        add()
        onBackPressed()

        val add = layout.findViewById(R.id.end_button)
        add.setOnClickListener(View.OnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Завершить проект?")
            builder.setMessage("Ваш проект попадет в архив со всеми данными, в случаи необходимости его можно будет востановить")
            builder.setPositiveButton(
                "Да"
            ) { dialogInterface, i ->
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                val date: String =
                    (calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1)).toString() + "." + calendar.get(
                        Calendar.YEAR
                    )
                myDB.updateToDbProject(idProject, 1, date)
                replaceFragment(MenuProjectFragment())
            }
            builder.setNegativeButton(
                "Нет"
            ) { dialog, which -> }
            builder.show()
        })



        return layout
    }

    //Формируем список из БД
    fun add() {
        val cursor = myDB.selectProjectAllProductAndCategoryAdd(idProject)
        while (cursor.moveToNext()) {
            productAllList.add(Product(0, cursor.getString(0), cursor.getString(1)))
        }
        cursor.close()
        for (product in productAllList) {
            var productName: String? = null
            var productUnitAdd = 0.0
            var productUnitWriteOff = 0.0
            var suffix: String? = null
            val cursorAdd = myDB.selectProductJoin(
                idProject,
                product.name,
                MyConstanta.TABLE_NAME_ADD,
                product.suffix
            )
            if (cursorAdd.getCount() !== 0) {
                cursorAdd.moveToFirst()
                productName = cursorAdd.getString(0)
                productUnitAdd = cursorAdd.getDouble(1)
                suffix = cursorAdd.getString(2)
            }
            cursor.close()
            val cursorWriteOff = myDB.selectProductJoin(
                idProject,
                product.name,
                MyConstanta.TABLE_NAME_WRITEOFF,
                product.suffix
            )
            if (cursorWriteOff.getCount() !== 0) {
                cursorWriteOff.moveToFirst()
                productUnitWriteOff = cursorWriteOff.getDouble(1)
            }
            cursorWriteOff.close()
            val nowUnitProduct = productUnitAdd - productUnitWriteOff
            productList.add(Product(productName!!, nowUnitProduct, suffix))
        }
    }


    // Настраиваем программно EditText
    fun onBackPressed() {
        val tableLayout = layout.findViewById(R.id.tableLayout) as TableLayout
        var rowI = 0
        for (product in productList) {
            val tableRow = TableRow(activity)
            tableRow.layoutParams = ActionBar.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            tableRow.setGravity(Gravity.CENTER_HORIZONTAL)
            for (i in 0..2) {
                val til = TextView(activity)
                when (i) {
                    0 -> {
                        til.setText(product.name + "  ")
                        tableRow.addView(til, i)
                    }
                    1 -> {
                        til.setText(product.count.toString())
                        tableRow.addView(til, i)
                    }
                    2 -> {
                        til.setText("  " + product.suffix)
                        tableRow.addView(til, i)
                    }
                }
            }
            tableLayout.addView(tableRow, rowI)
            rowI++
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.conteiner, fragment, "visible_fragment")
            .addToBackStack(null)
            .commit()
    }



    companion object {

    }
}