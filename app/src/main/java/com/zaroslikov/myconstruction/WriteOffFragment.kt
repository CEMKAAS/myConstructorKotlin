package com.zaroslikov.myconstruction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.textfield.TextInputLayout
import com.zaroslikov.myconstruction.db.MyConstanta
import com.zaroslikov.myconstruction.db.MyDatabaseHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class WriteOffFragment : Fragment() {

    lateinit var myDB: MyDatabaseHelper

    private lateinit var add_edit: TextInputLayout
    lateinit var date: TextInputLayout
    lateinit var productNameMenu: TextInputLayout
    lateinit var suffixMenu: TextInputLayout
    lateinit var categoryMenu: TextInputLayout
    lateinit var productName: AutoCompleteTextView
    private lateinit var suffixSpiner: AutoCompleteTextView
    lateinit var category: AutoCompleteTextView
    private lateinit var nowUnit: TextView

    private var productNameList = mutableListOf<String>()
    private var categoryList = mutableListOf<String>()
    private var suffixList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val layout = inflater.inflate(R.layout.fragment_write_off, container, false)

        myDB = MyDatabaseHelper(requireContext())
        val idProject = MainActivity().projectNumer

        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.title = "Мои Списания"
        appBar.menu.findItem(R.id.filler).isVisible = false
        appBar.menu.findItem(R.id.moreAll).isVisible = true
        appBar.menu.findItem(R.id.magazine).isVisible = true
        appBar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.moreAll -> {
                    replaceFragment(InFragment())
                    appBar.title = "Информация"
                }

                R.id.magazine -> {
                    replaceFragment(MagazineManagerFragment())
                }
            }
            true
        }
        appBar.setNavigationOnClickListener { requireActivity().supportFragmentManager.popBackStack() }


        productName = layout.findViewById(R.id.productName_editText)
        add_edit = layout.findViewById(R.id.add_edit)
        suffixSpiner = layout.findViewById(R.id.suffixSpiner)
        category = layout.findViewById(R.id.category_edit)
        date = layout.findViewById(R.id.date)
        nowUnit = layout.findViewById(R.id.now_warehouse)

        productNameMenu = layout.findViewById(R.id.product_name_menu)
        suffixMenu = layout.findViewById(R.id.suffix_menu)
        categoryMenu = layout.findViewById(R.id.category_menu)

        addProduct(idProject)
        // Настройка календаря
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        date.editText!!.setText(calendar[Calendar.DAY_OF_MONTH].toString() + "." + (calendar[Calendar.MONTH] + 1) + "." + calendar[Calendar.YEAR])

        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraintsBuilder)
            .setTitleText("Выберите дату")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) //Todo выбирать дату из EditText
            .build()

        date.editText!!.setOnClickListener {
            datePicker.show(requireActivity().supportFragmentManager, "wer")
            datePicker.addOnPositiveButtonClickListener(MaterialPickerOnPositiveButtonClickListener<Any?> { selection ->
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = (selection as Long)
                val format = SimpleDateFormat("dd.MM.yyyy")
                val formattedDate = format.format(calendar.time)
                date.editText!!.setText(formattedDate)
            })
        }

        productName.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                val productClick = productNameList.get(position)
                val cursorProduct = myDB.seachProduct(productClick)
                suffixList.clear()
                while (cursorProduct.moveToNext()) {
                    suffixList.add(cursorProduct.getString(2))
                }
                cursorProduct.close()
                setArrayAdapter()
                addDB(productClick, 0.0, suffixList.get(0),idProject)
            }

        suffixSpiner.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                val productClick = productName.text.toString()
                addDB(productClick, 0.0, suffixSpiner.text.toString(), idProject)
            }

        val add = layout.findViewById<Button>(R.id.add_button)
        add.setOnClickListener {
            addInDB(idProject)
            setArrayAdapter()
        }

        return layout
    }


    //Добавляем продукцию в список
    private fun addProduct(idProject:Int) {
        val cursor = myDB.seachProductToProject(idProject)

        val categoryHashSet: MutableSet<String> = HashSet()
        val productHashSet: MutableSet<String> = HashSet()


        while (cursor.moveToNext()) {
            productHashSet.add(cursor.getString(0))
            categoryHashSet.add(cursor.getString(1))
        }
        productNameList = productHashSet.toMutableList()
        categoryList = categoryHashSet.toMutableList()
    }

    fun addInDB(idProject: Int) {
        add_edit.isErrorEnabled = false
        date.isErrorEnabled = false
        categoryMenu.isErrorEnabled = false
        suffixMenu.isErrorEnabled = false
        productNameMenu.isErrorEnabled = false
        if (productName.text.toString() == "" || suffixSpiner.text.toString() == "" || add_edit.editText!!
                .text.toString() == "" || category.text.toString() == "" || date.editText!!
                .text.toString() == ""
        ) {
            if (productName.text.toString() == "") {
                productNameMenu.error = "Выберите товар!"
                productNameMenu.error
            }
            if (suffixSpiner.text.toString() == "") {
                suffixMenu.error = "Выберите единицу!"
                suffixMenu.error
            }
            if (add_edit.editText!!.text.toString() == "") {
                add_edit.error = "Укажите кол-во товара!"
                add_edit.error
            }
            if (category.text.toString() == "") {
                categoryMenu.error = "Укажите категорию!"
                categoryMenu.error
            }
            if (date.editText!!.text.toString() == "") {
                date.error = "Укажите дату!"
                date.error
            }
        } else {
            //Достаем из андройда
            val name = productName.text.toString()
            val suffix = suffixSpiner.text.toString()
            val count = add_edit.editText!!
                .text.toString().replace(",".toRegex(), ".").replace("[^\\d.]".toRegex(), "")
                .toDouble()
            val categoryProduct = category.text.toString()
            val dateProduct = date.editText!!.text.toString()
            if (addDB(name, count, suffix, idProject)) {
                // проверяем продукт в БД
                val cursorProduct = myDB.seachProductAndSuffix(name, suffix)
                cursorProduct.moveToFirst()
                val idProduct = cursorProduct.getInt(0)
                cursorProduct.close()

                //проверяем связку продукт архив
                val cursorPP = myDB.seachPP(idProject, idProduct)
                cursorPP.moveToFirst()
                val idPP = cursorPP.getInt(0)
                cursorPP.close()

                //присвыаеиваем ид продукта и проекта в адд таблицу
                myDB.insertToDbProductWriteOff(count, categoryProduct, dateProduct, idPP)
                Toast.makeText(activity, "Списали $name $count $suffix", Toast.LENGTH_LONG).show()
                if (!categoryList.contains(categoryProduct)) {
                    categoryList.add(categoryProduct)
                }
            }
        }
    }

    //Формируем список из БД
    private fun addDB(product: String, count: Double, suffixName: String, idProject: Int): Boolean {
        val cursor = myDB.selectProductJoin(
            idProject,
            product, MyConstanta.Constanta.TABLE_NAME_ADD, suffixName
        )
        var productName: String? = null
        var productUnitAdd = 0.0
        var productUnitWriteOff = 0.0
        var suffix: String? = null
        if (cursor.count != 0) {
            cursor.moveToFirst()
            productName = cursor.getString(0)
            productUnitAdd = cursor.getDouble(1)
            suffix = cursor.getString(2)
        }
        cursor.close()
        val cursorWriteOff = myDB.selectProductJoin(
            idProject,
            product, MyConstanta.Constanta.TABLE_NAME_WRITEOFF, suffixName
        )
        if (cursorWriteOff.count != 0) {
            cursorWriteOff.moveToFirst()
            productUnitWriteOff = cursorWriteOff.getDouble(1)
        }
        cursorWriteOff.close()
        val nowUnitProduct = productUnitAdd - (productUnitWriteOff + count)
        val wareHouseUnitProduct = productUnitAdd - productUnitWriteOff
        return if (nowUnitProduct < 0) {
            add_edit.error =
                "Столько товара нет на складе!\nВы можете списать только $wareHouseUnitProduct"
            add_edit.error
            false
        } else {
            if (productName == null || suffixName == null) {
                nowUnit.text = " На складе  нет такого товара "
            } else {
                nowUnit.text = " На складе $productName $nowUnitProduct $suffixName"
            }
            true
        }
    }

    private fun setArrayAdapter() {
        //Товар
        val arrayAdapterProduct = ArrayAdapter(
            requireContext().applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            productNameList
        )
        productName.setAdapter(arrayAdapterProduct)

        //Категории
       val arrayAdapterCategory = ArrayAdapter(
            requireContext().applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            categoryList
        )
        category.setAdapter(arrayAdapterCategory)

        //Категории
       val arrayAdapterSuffix = ArrayAdapter(
            requireContext().applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            suffixList
        )
        suffixSpiner.setAdapter(arrayAdapterSuffix)
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