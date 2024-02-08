package com.zaroslikov.myconstruction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.zaroslikov.myconstruction.db.MyConstanta
import com.zaroslikov.myconstruction.db.MyDatabaseHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class UpdateProductFragment : Fragment() {
    lateinit var myDB: MyDatabaseHelper
    var productNameList= mutableListOf<String>()
    var categoryList = mutableListOf<String>()
    val productList= mutableListOf<Product>()

    lateinit var productName: AutoCompleteTextView
    lateinit var add_edit: TextInputLayout
    lateinit var price_edit: TextInputLayout
    lateinit var suffixSpiner: AutoCompleteTextView
    lateinit var category: AutoCompleteTextView
    lateinit var date: TextInputLayout
    lateinit var nowUnit: TextView
    lateinit var nowWarehouse: TextView
    lateinit var productNameMenu: TextInputLayout
    lateinit var suffixMenu: TextInputLayout
    lateinit var categoryMenu: TextInputLayout
    lateinit var productUpDate: Product

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_update_product, container, false)
        myDB = MyDatabaseHelper(requireContext())
        val idProject = MainActivity().projectNumer

        val bundle = this.arguments
        var nameMagazine ="  "

        if (bundle!=null){
            productUpDate = bundleProduct(bundle)!!
            nameMagazine = bundle.getString("id").toString()
        }


        //Подключаем фронт
        productName = layout.findViewById<AutoCompleteTextView>(R.id.productName_editText)
        add_edit = layout.findViewById<TextInputLayout>(R.id.add_edit)
        price_edit = layout.findViewById<TextInputLayout>(R.id.price_edit)
        suffixSpiner = layout.findViewById<AutoCompleteTextView>(R.id.suffixSpiner)
        category = layout.findViewById<AutoCompleteTextView>(R.id.category_edit)
        date = layout.findViewById<TextInputLayout>(R.id.date)
        nowUnit = layout.findViewById<TextView>(R.id.now_warehouse)
        nowWarehouse = layout.findViewById<TextView>(R.id.now_unit)

        //Подключаем Фронт для спинеров
        productNameMenu = layout.findViewById<TextInputLayout>(R.id.product_name_add_menu)
        suffixMenu = layout.findViewById<TextInputLayout>(R.id.suffix_add_menu)
        categoryMenu = layout.findViewById<TextInputLayout>(R.id.category_add_menu)

        //Настройка верхней строки
        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.menu.findItem(R.id.filler).isVisible = false
        appBar.menu.findItem(R.id.magazine).isVisible = false
        appBar.menu.findItem(R.id.moreAll).isVisible = true
        appBar.setOnMenuItemClickListener { item: MenuItem ->
            val position = item.itemId
            if (position == R.id.moreAll) {
                replaceFragment(InFragment())
                appBar.title = "Информация"
            }
            true
        }

        appBar.setNavigationOnClickListener { requireActivity().supportFragmentManager.popBackStack() }

        // Настройка календаря
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .build()

        val datePicker = datePicker()
            .setCalendarConstraints(constraintsBuilder)
            .setTitleText("Выберите дату")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) //Todo выбирать дату из EditText
            .build()

        date.editText!!.setOnClickListener {
            datePicker.show(requireActivity().supportFragmentManager, "wer")
            datePicker.addOnPositiveButtonClickListener(MaterialPickerOnPositiveButtonClickListener<Any?> { selection ->
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = selection as Long
                val format = SimpleDateFormat("dd.MM.yyyy")
                val formattedDate: String = format.format(calendar.getTime())
                date.editText!!.setText(formattedDate)
            })
        }

        //Назначае каждой строке

        //Назначае каждой строке
        productName.setText(productUpDate.name.toString())
        add_edit.editText!!.setText(productUpDate.count.toString())
        suffixSpiner.setText(productUpDate.suffix, false)
        price_edit.editText!!.setText(productUpDate.price.toString())
        category.setText(productUpDate.category)
        date.editText!!.setText(productUpDate.date)

        //Все зависит от раздела
        if (nameMagazine.equals("Мои Покупки")) {
            price_edit.setVisibility(View.VISIBLE);
            nowUnit.setVisibility(View.GONE);
        } else if (nameMagazine.equals("Мои Списания")) {
            //суффикс, цену и ввод имени убираем
            nowUnit.setText(
                productUpDate.name?.toUpperCase() + " c ед. изм. " + productUpDate.suffix
                    ?.toUpperCase()
            );
            suffixMenu.setVisibility(View.GONE);
            productNameMenu.setVisibility(View.GONE);
            price_edit.setVisibility(View.GONE);
        }

        addDB(productUpDate.name.toString(), productUpDate.count, productUpDate.suffix.toString(), idProject, productUpDate, nameMagazine)

        //Берем из бд товары и добавляем в список
        addProduct(idProject)

        productName.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id -> //Назначаем суффикс из продукта
                val productClick = productNameList[position]
                val cursorProduct = myDB.seachProduct(productClick)
                while (cursorProduct.moveToNext()) {
                    suffixMenu.editText?.setText(cursorProduct.getString(2))
                }
                cursorProduct.close()
                setArrayAdapter()
            }

        //Кнопка обновления
        val updateButton = layout.findViewById<Button>(R.id.update_button)
        updateButton.setOnClickListener(View.OnClickListener {
            if (nameMagazine == "Мои Покупки") {
                upDateProductADD(idProject, productUpDate, nameMagazine)
            } else if (nameMagazine == "Мои Списания") {
                upDateProductWriteOff(idProject, productUpDate, nameMagazine)
            }
        })

        //Кнопка удаления
        val deleteButton = layout.findViewById<Button>(R.id.delete_button)
        deleteButton.setOnClickListener(View.OnClickListener { deleteProduct(productUpDate,
            nameMagazine.toString()
        ) })

        return layout
    }

fun bundleProduct(bundle: Bundle): Product? {
    return bundle.getParcelable<Product>("product")
}

    //Добавляем продукцию в список из БД
    fun addProduct(idProject:Int) {
        val cursor = myDB.readProduct()
        while (cursor.moveToNext()) {
            productNameList.add(cursor.getString(1))
        }
        cursor.close()

        //Через сет, чтобы не было повторов
        val tempList: MutableSet<String> = HashSet()
        val cursor1 = myDB.seachCategory(idProject)

        while (cursor1.moveToNext()) {
            tempList.add(cursor1.getString(0))
        }
        cursor1.close()

        categoryList = tempList.toMutableList()

    }


    fun upDateProductADD(idProject: Int, productUpDate:Product, nameMagazine: String) {
        //Очищаем ошибки
        add_edit.isErrorEnabled = false
        date.isErrorEnabled = false
        categoryMenu.isErrorEnabled = false
        suffixMenu.isErrorEnabled = false
        price_edit.isErrorEnabled = false
        productNameMenu.isErrorEnabled = false

        //проверяем есть ли ошибки
        if (productName.text.toString() == "" || suffixSpiner.text.toString() == "" || add_edit.editText!!
                .text.toString() == "" || category.text.toString() == "" || date.editText!!
                .text.toString() == "" || price_edit.editText!!.text.toString() == ""
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
            if (price_edit.editText!!.text.toString() == "") {
                price_edit.error = "Укажите цену!"
                price_edit.error
            }
        } else {
            //Достаем из Фронта все данные
            val name = productName.text.toString().substring(0, 1)
                .uppercase(Locale.getDefault()) + productName.text.toString().substring(1)
            val suffix = suffixSpiner.text.toString()
            val price = price_edit.editText!!
                .text.toString().replace(",".toRegex(), ".").replace("[^\\d.]".toRegex(), "")
                .toDouble()
            val count = add_edit.editText!!
                .text.toString().replace(",".toRegex(), ".").replace("[^\\d.]".toRegex(), "")
                .toDouble()
            val categoryProduct = category.text.toString().substring(0, 1)
                .uppercase(Locale.getDefault()) + category.text.toString().substring(1)
            val dateProduct = date.editText!!.text.toString()
            //Константы
            val idProduct = intArrayOf(0)
            val idPP = 0

            //Проверяем продукт в БД
            val cursorProduct = myDB.seachProductAndSuffix(name, suffix)

            //Если нет товара, то тогда
            if (cursorProduct.count === 0) {
                cursorProduct.close()
                val builder = MaterialAlertDialogBuilder(requireContext())
                builder.setTitle(
                    "Товара " + name.uppercase(Locale.getDefault()) + " c ед.изм " + suffix.uppercase(
                        Locale.getDefault()
                    ) + " нет!"
                )
                builder.setMessage("""Вы хотите ИЗМЕНИТЬ ВСЕ записи с ${
                    productUpDate.name?.toUpperCase()
                    } c ед.изм ${
                    productUpDate.suffix?.toUpperCase()
                    } на ${name.uppercase(Locale.getDefault())} c ед.изм ${
                        suffix.uppercase(
                            Locale.getDefault()
                        )
                    }
Или  ДОБАВИТЬ c ЗАМЕНОЙ на новый товар ${name.uppercase(Locale.getDefault())} c ед.изм ${
                        suffix.uppercase(
                            Locale.getDefault()
                        )
                    } с данными значениями?"""
                )
                builder.setPositiveButton(
                    "Добавить"
                ) { dialogInterface, i ->
                    Toast.makeText(activity, "Вы добавили товар ", Toast.LENGTH_SHORT).show()
                    idProduct[0] = Math.toIntExact(myDB.insertToDbProduct(name, suffix))
                    productNameList.add(name)
                    cursorUpdate(idProduct, idPP, count, categoryProduct, price, dateProduct, idProject, productUpDate, nameMagazine)
                }
                builder.setNegativeButton(
                    "Изменить"
                ) { dialogInterface, i ->
                    idProduct[0] = Math.toIntExact(
                        myDB.updateToDbProduct(
                            productUpDate.name.toString(),
                            name,
                            suffix
                        )
                    )
                    if (addDB(productUpDate.name.toString(), count, productUpDate.suffix.toString(), idProject, productUpDate, nameMagazine)) {
                        cursorUpdate(idProduct, idPP, count, categoryProduct, price, dateProduct, idProject, productUpDate, nameMagazine)
                    }
                }
                builder.setNeutralButton(
                    "Отмена"
                ) { dialogInterface, i -> }
                builder.show()
            } else {
                cursorProduct.moveToFirst()
                idProduct[0] = cursorProduct.getInt(0)
                cursorProduct.close()
                if (addDB(name, count, suffix, idProject, productUpDate, nameMagazine)) {
                    cursorUpdate(idProduct, idPP, count, categoryProduct, price, dateProduct, idProject, productUpDate, nameMagazine)
                }
            }
        }
    }

    //Обновляем Списание
    private fun upDateProductWriteOff(idProject: Int, productUpDate: Product, nameMagazine: String) {
        add_edit.isErrorEnabled = false
        date.isErrorEnabled = false
        categoryMenu.isErrorEnabled = false
        if (add_edit.editText!!.text.toString() == "" || category.text.toString() == "" || date.editText!!
                .text.toString() == ""
        ) {
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
            //Достаем из андройда имяПродукта и суффикс
            val count = add_edit.editText!!
                .text.toString().replace(",".toRegex(), ".").replace("[^\\d.]".toRegex(), "")
                .toDouble()
            val categoryProduct = category.text.toString().substring(0, 1)
                .uppercase(Locale.getDefault()) + category.text.toString().substring(1)
            val dateProduct = date.editText!!.text.toString()
            val idProduct = intArrayOf(0)
            val idPP = 0

            // проверяем продукт в БД
            val cursorProduct = myDB.seachProductAndSuffix(productUpDate.name.toString(),
                productUpDate.suffix.toString()
            )
            cursorProduct.moveToFirst()
            idProduct[0] = cursorProduct.getInt(0)
            cursorProduct.close()
            if (addDB(productUpDate.name.toString(), count, productUpDate.suffix.toString(), idProject, productUpDate, nameMagazine)) {
                cursorUpdate(idProduct, idPP, count, categoryProduct, 0.0, dateProduct, idProject, productUpDate, nameMagazine)
            }
        }
    }

    //Проверяем уходим ли в минус или нет
    private fun addDB(name: String, count: Double, suffix: String, idProject: Int, productUpDate: Product, nameMagazine:String): Boolean {
        val cursor = myDB.selectProductJoin(
            idProject, name, MyConstanta.Constanta.TABLE_NAME_ADD,
            suffix
        )
        var productName: String? = null
        var productUnitAdd = 0.0
        var productUnitWriteOff = 0.0
        var suffixName: String? = null
        if (cursor.count !== 0) {
            cursor.moveToFirst()
            productName = cursor.getString(0)
            productUnitAdd = cursor.getDouble(1)
            suffixName = cursor.getString(2)
        }
        cursor.close()
        val cursorWriteOff = myDB.selectProductJoin(
            idProject, name, MyConstanta.Constanta.TABLE_NAME_WRITEOFF,
            suffix
        )
        if (cursorWriteOff.getCount() !== 0) {
            cursorWriteOff.moveToFirst()
            productUnitWriteOff = cursorWriteOff.getDouble(1)
        }
        cursorWriteOff.close()
        val diff: Double = productUpDate.count - count
        var nowUnitProduct = 0.0
        val wareHouseUnitProduct = productUnitAdd - productUnitWriteOff
        if (nameMagazine.equals("Мои Покупки")) {
            nowUnitProduct = productUnitAdd - diff - productUnitWriteOff
            if (nowUnitProduct < 0) {
                add_edit.error = "Столько товара нет на складе!\nу Вас списано $productUnitWriteOff"
                add_edit.error
                return false
            }
        } else if (nameMagazine.equals("Мои Списания")) {
            nowUnitProduct = productUnitAdd - (productUnitWriteOff - diff)
            if (nowUnitProduct < 0) {
                add_edit.error = "Столько товара нет на складе!\nу Вас добавленно $productUnitAdd"
                add_edit.error
                return false
            }
        }
        nowWarehouse.text = "Cейчас на складе $wareHouseUnitProduct $name"
        return true
    }

    fun cursorUpdate(
        idProduct: IntArray, idPP: Int, count: Double, categoryProduct: String,
        price: Double, dateProduct: String, idProject: Int, productUpDate: Product, nameMagazine: String
    ) {

        //проверяем связку продукт архив
        var idPP = idPP
        val cursorPP = myDB.seachPP(idProject, idProduct[0])
        idPP = if (cursorPP.getCount() === 0) {
            Math.toIntExact(myDB.insertToDbProjectProduct(idProject, idProduct[0]))
        } else {
            cursorPP.moveToFirst()
            cursorPP.getInt(0)
        }
        cursorPP.close()
        if (nameMagazine.equals("Мои Покупки")) {
            myDB.updateToDbAdd(
                count,
                categoryProduct,
                price,
                dateProduct,
                idPP,
                productUpDate.id
            )
        } else if (nameMagazine.equals("Мои Списания")) {
            myDB.updateToDbWriteOff(
                count,
                categoryProduct,
                dateProduct,
                idPP,
                productUpDate.id
            )
        }
        Toast.makeText(activity, "Обновленно", Toast.LENGTH_LONG).show()
        replaceFragment(MagazineManagerFragment())
        if (!categoryList.contains(categoryProduct)) {
            categoryList.add(categoryProduct)
        }
    }

    private fun deleteProduct(productUpDate: Product, nameMagazine: String) {
        if (nameMagazine.equals("Мои Покупки")) {
            myDB.deleteOneRowAdd(productUpDate.id, MyConstanta.Constanta.TABLE_NAME_ADD)
        } else if (nameMagazine.equals("Мои Списания")) {
            myDB.deleteOneRowAdd(productUpDate.id, MyConstanta.Constanta.TABLE_NAME_WRITEOFF)
        }
        replaceFragment(MagazineManagerFragment())
    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.conteiner, fragment, "visible_fragment")
            .addToBackStack(null)
            .commit()
    }

    private fun setArrayAdapter() {
        //Товар
       val arrayAdapterProduct = ArrayAdapter<String>(
            requireContext().applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            productNameList
        )
        productName.setAdapter(arrayAdapterProduct)

        //Категории
        val arrayAdapterCategory = ArrayAdapter<String>(
            requireContext().applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            categoryList
        )
        category.setAdapter(arrayAdapterCategory)
    }

}
