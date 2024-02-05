package com.zaroslikov.myconstruction

import android.R
import android.database.Cursor
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.zaroslikov.myconstruction.db.MyDatabaseHelper
import java.text.SimpleDateFormat


class MagazineManagerFragment : Fragment() {

    lateinit var myDB: MyDatabaseHelper
    lateinit var empty_imageview: ImageView
    lateinit var no_data: TextView
    private val products = mutableListOf<Product>()
    private val productNow = mutableListOf<Product>()
    private var productNameList = mutableListOf<String>()
    private var categoryList = mutableListOf<String>()
    private lateinit var recyclerView :RecyclerView
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var dataSheet:

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_magazine_manager, container, false)

        myDB = MyDatabaseHelper(requireActivity())
        var idProject = MainActivity().projectNumer

        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.menu.findItem(R.id.filler).isVisible = true
        appBar.menu.findItem(R.id.deleteAll).setVisible(false)
        appBar.menu.findItem(R.id.magazine).isVisible = false
        appBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.filler -> {
                    bottomSheetDialog.show
                }
                R.id.moreAll -> {
                    replaceFragment(InFragment)
                    appBar.title = "Информация"
                }
            }
            true
        }
        appBar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val sixColumn = layout.findViewById<TextView>(R.id.six_column)
        recyclerView = layout.findViewById<RecyclerView>(R.id.recyclerView)
        empty_imageview = layout.findViewById(R.id.empty_imageview)
        no_data = layout.findViewById(R.id.no_data)

        var myRow = 0
        when (appBar.title) {
            "Мои Покупки" -> {

                storeDataInArraysClass(myDB.readAddMagazine(idProject), true)
                addProduct()
                sixColumn.visibility = View.VISIBLE

                clickBool = true
                myRow = R.layout.my_row_add
            }
            "Мои Списания" -> {
                storeDataInArraysClass(myDB.readWriteOffMagazine(idProject), false)
                addProduct()
                sixColumn.visibility = View.GONE


                clickBool = true
                myRow = R.layout.my_row_write_off
            }
        }



        showBottomSheetDialog()

        var customAdapterMagazine = CustomAdapterMagazine(productNow, myRow)
        recyclerView.adapter = customAdapterMagazine
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        if(clickBool){
            customAdapterMagazine.onClick( )
        }

        val constraintsBuilder: CalendarConstraints = Builder()
            .setValidator(DateValidatorPointBackward.now())
            .build()

        datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setCalendarConstraints(constraintsBuilder)
            .setTitleText("Выберите даты")
            .setSelection(
                Pair.create(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()

        dataSheet.getEditText().setOnClickListener(View.OnClickListener {
            datePicker.show(requireActivity().supportFragmentManager, "wer")
            datePicker.addOnPositiveButtonClickListener(object :
                MaterialPickerOnPositiveButtonClickListener<Pair<Long?, Long?>?>() {
                fun onPositiveButtonClick(selection: Pair<Long, Long>) {
                    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    val calendar2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    val startDate = selection.first
                    val endDate = selection.second
                    calendar.setTimeInMillis(startDate)
                    calendar2.setTimeInMillis(endDate)
                    val format = SimpleDateFormat("dd.MM.yyyy")
                    val formattedDate1: String = format.format(calendar.getTime())
                    val formattedDate2: String = format.format(calendar2.getTime())
                    try {
                        dateFirst = format.parse(formattedDate1)
                        dateEnd = format.parse(formattedDate2)
                    } catch (e: ParseException) {
                        throw RuntimeException(e)
                    }
                    dataSheet.getEditText().setText("$formattedDate1-$formattedDate2")
                }
            })
        })
        // Настройка кнопки в bottomSheet
        // Настройка кнопки в bottomSheet
        buttonSheet.setOnClickListener(View.OnClickListener {
            try {
                filter()
                customAdapterMagazine = CustomAdapterMagazine(productNow, myRow)
                recyclerView.adapter = customAdapterMagazine
                recyclerView.layoutManager = LinearLayoutManager(activity)
                bottomSheetDialog.dismiss()
            } catch (e: ParseException) {
                throw RuntimeException(e)
            }
        })





        return layout
    }

    fun storeDataInArraysClass(cursor: Cursor, magazineAddBool: Boolean) {
        if (cursor.count == 0) {
            empty_imageview.visibility = View.VISIBLE
            no_data.visibility = View.VISIBLE
        } else if (magazineAddBool) {
            storeDataInArraysClassLogicAdd(cursor)
        } else {
            storeDataInArraysClassLogicWriteOff(cursor)
        }
    }

    fun storeDataInArraysClassLogicAdd(cursor: Cursor) {
        cursor.moveToLast()
        products.add(
            Product(
                cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getDouble(3), cursor.getDouble(4), cursor.getString(5), cursor.getString(6)
            )
        )
        while (cursor.moveToPrevious()) {
            products.add(
                Product(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getDouble(4),
                    cursor.getString(5),
                    cursor.getString(6)
                )
            )
        }
        cursor.close()
        empty_imageview.visibility = View.GONE
        no_data.visibility = View.GONE
    }

    fun storeDataInArraysClassLogicWriteOff(cursor: Cursor) {
        cursor.moveToLast()
        products.add(
            Product(
                cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getDouble(3), 0, cursor.getString(4), cursor.getString(5)
            )
        )
        while (cursor.moveToPrevious()) {
            products.add(
                Product(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getDouble(3), 0, cursor.getString(4), cursor.getString(5)
                )
            )
        }
        cursor.close()
        empty_imageview.visibility = View.GONE
        no_data.visibility = View.GONE
    }

    fun addProduct() {
        val categoryHashSet: MutableSet<String> = HashSet()
        val productHashSet: MutableSet<String> = HashSet()

        for (product in products) {
            productHashSet.add(product.name)
            categoryHashSet.add(product.category)
        }

        productNameList = productHashSet.toMutableList()
        categoryList = categoryHashSet.toMutableList()

        productNow.addAll(products)
        productNameList.add("Все")
        categoryList.add("Все")
    }

    //Добавляем bottobSheet
    fun showBottomSheetDialog() {
        bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomSheetDialog.setContentView(R.layout.fragment_bottom)
        val animalsSpinerSheet = bottomSheetDialog.findViewById(R.id.product_spiner_sheet)
        val categorySpinerSheet = bottomSheetDialog.findViewById(R.id.categiry_spiner_sheet)
        animalsSpinerSheet.setText("Все", false)
        categorySpinerSheet.setText("Все", false)
        dataSheet = bottomSheetDialog.findViewById(R.id.data_sheet)
        val buttonSheet = bottomSheetDialog.findViewById(R.id.button_sheet)
    }



    fun filter() {

        productNow.clear()
        val animalsSpinerSheetText: String = animalsSpinerSheet.getText().toString()
        val categorySpinerSheetText: String = categorySpinerSheet.getText().toString()
        val format = SimpleDateFormat("dd.MM.yyyy")

        if (animalsSpinerSheetText == "Все" && categorySpinerSheetText == "Все" && dataSheet.getEditText()
                .getText().toString().equals(""))
        { productNow.addAll(products) }

        else if (animalsSpinerSheetText == "Все" && categorySpinerSheetText == "Все" && !dataSheet.getEditText()
                .getText().toString().equals(""))

        {
            for (product in products) {
                val dateNow: Date = format.parse(product.date)
                if (dateFirst.before(dateNow) && dateEnd.after(dateNow) || dateFirst.equals(dateNow) || dateEnd.equals(
                        dateNow)) {
                    productNow.add(product)
                }
            }
        }

        else if (animalsSpinerSheetText == "Все" && categorySpinerSheetText != "Все" && dataSheet.getEditText()
                .getText().toString().equals("")
        ) {
            for (product in products) {
                if (categorySpinerSheetText == product.category) {
                    productNow.add(product)
                }
            }
        } else if (animalsSpinerSheetText != "Все" && categorySpinerSheetText == "Все" && dataSheet.getEditText()
                .getText().toString().equals("")
        ) {
            for (product in products) {
                if (animalsSpinerSheetText == product.name) {
                    productNow.add(product)
                }
            }
        } else if (animalsSpinerSheetText == "Все" && categorySpinerSheetText != "Все" && !dataSheet.getEditText()
                .getText().toString().equals("")
        ) {
            for (product in products) {
                val dateNow: Date = format.parse(product.date)
                if (categorySpinerSheetText == product.category &&
                    (dateFirst.before(dateNow) && dateEnd.after(dateNow) || dateFirst.equals(dateNow) || dateEnd.equals(
                        dateNow
                    ))
                ) {
                    productNow.add(product)
                }
            }
        } else if (animalsSpinerSheetText != "Все" && categorySpinerSheetText == "Все" && !dataSheet.getEditText()
                .getText().toString().equals("")
        ) {
            for (product in products) {
                val dateNow: Date = format.parse(product.date)
                if (animalsSpinerSheetText == product.name &&
                    (dateFirst.before(dateNow) && dateEnd.after(dateNow) || dateFirst.equals(dateNow) || dateEnd.equals(
                        dateNow
                    ))
                ) {
                    productNow.add(product)
                }
            }
        } else if (animalsSpinerSheetText != "Все" && categorySpinerSheetText != "Все" && dataSheet.getEditText()
                .getText().toString().equals("")
        ) {
            for (product in products) {
                if (animalsSpinerSheetText == product.name && categorySpinerSheetText == product.category) {
                    productNow.add(product)
                }
            }
        } else if (animalsSpinerSheetText != "Все" && categorySpinerSheetText != "Все" && !dataSheet.getEditText()
                .getText().toString().equals("")
        ) {
            for (product in products) {
                val dateNow: Date = format.parse(product.date)
                if (animalsSpinerSheetText == product.name && categorySpinerSheetText == product.category &&
                    (dateFirst.before(dateNow) && dateEnd.after(dateNow) || dateFirst.equals(dateNow) || dateEnd.equals(
                        dateNow
                    ))
                ) {
                    productNow.add(product)
                }
            }
        }
    }


    companion object {

    }
}