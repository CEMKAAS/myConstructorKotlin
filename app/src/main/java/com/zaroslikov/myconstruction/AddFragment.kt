package com.zaroslikov.myconstruction

import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.material.textfield.TextInputLayout
import com.zaroslikov.myconstruction.db.MyConstanta
import com.zaroslikov.myconstruction.db.MyDatabaseHelper
import java.text.SimpleDateFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var myDB: MyDatabaseHelper
    private var categoryList = mutableListOf<String>()
    private var productNameList = mutableListOf<String>()
    private var productList = mutableListOf<Product>()
    private lateinit var nowUnit: TextView
    private lateinit var date: TextInputLayout
    private lateinit var category: AutoCompleteTextView
    private lateinit var suffixSpiner: AutoCompleteTextView
    private lateinit var productName: AutoCompleteTextView
    private lateinit var price_edit: TextInputLayout
    private lateinit var add_edit: TextInputLayout
    private lateinit var productNameMenu: TextInputLayout
    private lateinit var suffixMenu: TextInputLayout
    private lateinit var categoryMenu: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_add, container, false)

        myDB = MyDatabaseHelper(requireActivity())

        val idProject = MainActivity().projectNumer

        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.title = "Мои Покупки"
        appBar.menu.findItem(R.id.filler).isVisible = false
        appBar.menu.findItem(R.id.moreAll).isVisible = true
        appBar.menu.findItem(R.id.magazine).isVisible = true
        appBar.setOnMenuItemClickListener {
            when (it.itemId) {
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

        appBar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        productName = layout.findViewById(R.id.productName_editText)
        add_edit = layout.findViewById(R.id.add_edit)
        price_edit = layout.findViewById(R.id.price_edit)
        suffixSpiner = layout.findViewById(R.id.suffixSpiner)
        category = layout.findViewById(R.id.category_edit)
        date = layout.findViewById(R.id.date)
        nowUnit = layout.findViewById(R.id.now_warehouse)

        productNameMenu = layout.findViewById(R.id.product_name_add_menu)
        suffixMenu = layout.findViewById(R.id.suffix_add_menu)
        categoryMenu = layout.findViewById(R.id.category_add_menu)

        addProduct(idProject)

        // Настройка календаря
        val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        date.editText!!.setText(calendar[java.util.Calendar.DAY_OF_MONTH].toString() + "." + (calendar[java.util.Calendar.MONTH] + 1) + "." + calendar[java.util.Calendar.YEAR])

        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .build()

        val dataPicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraintsBuilder)
            .setTitleText("Выберите дату").setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        date.setOnClickListener {
            dataPicker.show(requireActivity().supportFragmentManager, "wer")
            dataPicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = it
                val format = SimpleDateFormat("dd.MM.yyyy")
                val formatteDate: String = format.format(calendar.time)
                date.editText?.setText(formatteDate)
            }
        }

        productName.setOnItemClickListener { adapterView, view, i, l ->
            val productClick = productList[i].name.toString()
            val suffixClick = productList[i].suffix.toString()

            if (suffixSpiner.text.toString() == "") {
                addDB(productClick, suffixClick, idProject)
                suffixSpiner.setText(suffixClick, false)
            } else {
                addDB(productClick, suffixSpiner.text.toString(), idProject)
            }
        }

        suffixSpiner.setOnItemClickListener { adapterView, view, i, l ->
            addDB(productName.text.toString(), suffixSpiner.text.toString(), idProject)
        }

        val add = layout.findViewById<Button>(R.id.add_button)
        add.setOnClickListener {
            addInDB(idProject)
            setArrayAdapter()
        }


        return layout
    }

    override fun onStart() {
        super.onStart()
        val view = view
        if (view != null) {
            setArrayAdapter()
        }
    }

    private fun addProduct(idProject: Int) {
        val cursor = myDB.readProduct()

        while (cursor.moveToNext()) {
            productNameList.add(cursor.getString(1))
            productList.add(Product(cursor.getInt(0), cursor.getString(1), cursor.getString(2)))
        }
        cursor.close()

        val tempList = mutableSetOf<String>()
        val cursor1 = myDB.seachProduct(idProject.toString())
        while (cursor1.moveToNext()) {
            tempList.add(cursor1.getString(0))
        }
        cursor1.close()

        categoryList = tempList.toMutableList()

    }

    private fun addDB(product: String, suffix: String, idProject: Int) {
        val cursor =
            myDB.selectProductJoin(idProject, product, MyConstanta.Constanta.TABLE_NAME_ADD, suffix)
        if (cursor.count != 0) {
            cursor.moveToFirst()
            val productName = cursor.getString(0)
            var productUnitAdd = cursor.getDouble(1)
            val suffixName = cursor.getString(2)
            cursor.close()

            val cursorWriteOff = myDB.selectProductJoin(
                idProject,
                product,
                MyConstanta.Constanta.TABLE_NAME_WRITEOFF,
                suffix
            )

            if (cursorWriteOff.count != 0) {
                cursorWriteOff.moveToFirst()
                val productUnitWriteOff = cursorWriteOff.getDouble(1)
                productUnitAdd -= productUnitWriteOff
            }
            cursorWriteOff.close()
            nowUnit.text = " На складе $productName $productUnitAdd  $suffixName"

        } else {
            nowUnit.text = " На складе  нет такого товара "
        }

    }

    private fun addInDB(idProject: Int) {
        add_edit.isErrorEnabled = false
        date.isErrorEnabled = false
        categoryMenu.isErrorEnabled = false
        suffixMenu.isErrorEnabled = false
        price_edit.isErrorEnabled = false
        productNameMenu.isErrorEnabled = false

        if ("" in listOf(
                productName.text.toString(),
                suffixSpiner.text.toString(),
                add_edit.editText?.text.toString(),
                category.text.toString(),
                date.editText?.text.toString(),
                price_edit.editText?.text.toString()
            )
        ) {
            if (productName.text.toString() == "") {
                productNameMenu.error = "Выберите товар!"
                productNameMenu.error
            }

            if (suffixSpiner.text.toString() == "") {
                suffixSpiner.error = "Выберите единицу!"
                suffixSpiner.error
            }
            if (add_edit.editText?.text.toString() == "") {
                add_edit.error = "Укажите кол-во товара!"
                add_edit.error
            }
            if (category.text.toString() == "") {
                categoryMenu.error = "Укажите категорию!"
                categoryMenu.error
            }
            if (date.editText?.text.toString() == "") {
                date.error = "Укажите дату!"
                date.error
            }
            if (price_edit.editText?.text.toString() == "") {
                price_edit.error = "Укажите цену!"
                price_edit.error
            }
        } else {
            val name = productName.text.toString()[0].uppercaseChar() + productName.text.toString()
                .substring(1)
            val suffix = suffixSpiner.text.toString()
            val price =
                price_edit.editText?.text.toString().replace(",", ".").replace("[^\\d.]", "")
            val count = add_edit.editText?.text.toString().replace(",", ".").replace("[^\\d.]", "")

            val categoryProduct =
                category.text.toString()[0].uppercaseChar() + category.text.toString().substring(1)
            val dateProduct = date.editText?.text.toString()

            var idProduct = 0

            val cursorProduct = myDB.seachProductAndSuffix(name, suffix)
            if (cursorProduct.count == 0) {
                idProduct = myDB.insertToDbProduct(name, suffix).toInt()
                productNameList.add(name)
            } else {
                cursorProduct.moveToFirst()
                idProduct = cursorProduct.getInt(0)
            }
            cursorProduct.close()

            val cursorPP = myDB.seachPP(idProject, idProduct)
            val idPP = if (cursorPP.count == 0) { // ToDO интересно
                myDB.insertToDbProjectProduct(idProject, idProduct).toInt()
            } else {
                cursorPP.moveToFirst()
                cursorPP.getInt(0)
            }
            cursorPP.close()

            myDB.insertToDbProductAdd(
                count.toDouble(),
                categoryProduct,
                price.toDouble(),
                dateProduct,
                idPP
            )
            Toast.makeText(
                requireActivity(),
                "Добавили $name $count $suffix за $price ₽",
                Toast.LENGTH_LONG
            ).show()

            if (!categoryList.contains(categoryProduct)) {
                categoryList.add(categoryProduct)
            }

            addDB(name, suffix, idProject)
        }


    }

    fun setArrayAdapter() {
        //Товар
        val arrayAdapterProduct = ArrayAdapter<String>(
            requireContext().applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            productNameList
        )
        productName.setAdapter<ArrayAdapter<String>>(arrayAdapterProduct)

        //Категории
        val arrayAdapterCategory = ArrayAdapter<String>(
            requireContext().applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            categoryList
        )
        category.setAdapter<ArrayAdapter<String>>(arrayAdapterCategory)
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