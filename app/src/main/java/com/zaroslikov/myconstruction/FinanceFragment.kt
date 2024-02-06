package com.zaroslikov.myconstruction

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.zaroslikov.myconstruction.db.MyDatabaseHelper
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FinanceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FinanceFragment : Fragment() {

    private lateinit var myDb: MyDatabaseHelper
    private lateinit var allSumText: TextView
    private lateinit var categoryText: TextView
    private lateinit var productText: TextView

    private lateinit var dataSheet: TextInputLayout
    private lateinit var buttonSheet : Button

    private var productSumList = mutableListOf<Product>()
    private var categorySumList = mutableListOf<Product>()
    private var productSumListNow = mutableListOf<Product>()
    private var categorySumListNow = mutableListOf<Product>()

    private var productNameList = mutableListOf<String>()
    private var categoryList = mutableListOf<String>()

    private lateinit var dateFirst : Date
    private lateinit var dateEnd : Date

    lateinit var bottomSheetDialog : BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_finance, container, false)

        myDb = MyDatabaseHelper(requireActivity())
        val fab = requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.extended_fab)
        fab.visibility = View.GONE

        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.title = "Мои Финансы"
        appBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        appBar.menu.findItem(R.id.filler).isVisible = false
        appBar.menu.findItem(R.id.deleteAll).isVisible = false
        appBar.menu.findItem(R.id.moreAll).isVisible = true
        appBar.menu.findItem(R.id.magazine).isVisible = true
        appBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.filler -> {
                    bottomSheetDialog.show()
                }
                R.id.moreAll -> {
                    replaceFragment(InFragment())
                    appBar.title = "Информация"
                }
            }
            true
        }

        appBar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val idProject = MainActivity().projectNumer

        allSumText = layout.findViewById(R.id.all_sum)
        categoryText = layout.findViewById(R.id.category_txt)
        productText = layout.findViewById(R.id.product_txt)

        add(idProject)

        //Создание модального bottomSheet
        showBottomSheetDialog()

        // Настраиваем адаптер
        val recyclerViewCategory = layout.findViewById<RecyclerView>(R.id.recyclerView)
        val recyclerViewProduct = layout.findViewById<RecyclerView>(R.id.recyclerViewAll)

        val productAdapterCategory = ProductAdapter(categorySumListNow, true)
        recyclerViewCategory.adapter = productAdapterCategory
        recyclerViewCategory.layoutManager = LinearLayoutManager(
            activity
        )

        val productAdapterProduct = ProductAdapter(productSumListNow, true)
        recyclerViewProduct.adapter = productAdapterProduct
        recyclerViewProduct.layoutManager = LinearLayoutManager(
            activity
        )

        // Настройка календаря на период
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .build()

       val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setCalendarConstraints(constraintsBuilder)
            .setTitleText("Выберите даты")
            .setSelection(
                Pair.create<Long, Long>(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()

        dataSheet.editText?.setOnClickListener(View.OnClickListener {
            datePicker.show(requireActivity().supportFragmentManager, "wer")
            datePicker.addOnPositiveButtonClickListener(MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>> { selection ->
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                val calendar2 = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                val startDate = selection.first
                val endDate = selection.second
                calendar.timeInMillis = startDate
                calendar2.timeInMillis = endDate
                val format = SimpleDateFormat("dd.MM.yyyy")
                val formattedDate1 = format.format(calendar.time)
                val formattedDate2 = format.format(calendar2.time)
                try {
                    dateFirst = format.parse(formattedDate1)
                    dateEnd = format.parse(formattedDate2)
                } catch (e: ParseException) {
                    throw RuntimeException(e)
                }
                dataSheet.editText?.setText("$formattedDate1-$formattedDate2")
                productText.text = "По продукции за\n$formattedDate1-$formattedDate2"
                categoryText.text = "По категориям за\n$formattedDate1-$formattedDate2"
            })
        })


        // Настройка кнопки в bottomSheet
        buttonSheet.setOnClickListener(View.OnClickListener {
            try {
                filter()
                val productAdapterCategory = ProductAdapter(categorySumListNow, true)
                recyclerViewCategory.adapter = productAdapterCategory
                recyclerViewCategory.layoutManager = LinearLayoutManager(
                    activity
                )
                val productAdapterProduct = ProductAdapter(productSumListNow, true)
                recyclerViewProduct.adapter = productAdapterProduct
                recyclerViewProduct.layoutManager = LinearLayoutManager(
                    activity
                )
                bottomSheetDialog.dismiss()
            } catch (e: ParseException) {
                throw RuntimeException(e)
            }
        })

        return layout
    }

    fun add(idProject: Int) {
        val cursor = myDb.selectProjectAllProductAndCategoryAdd(idProject)
        val productHashSet = mutableSetOf<String>()
        val categoryHashSet = mutableSetOf<String>()

        while (cursor.moveToNext()) {
            productHashSet.add(cursor.getString(0))
            categoryHashSet.add(cursor.getString(2))
        }

        cursor.close()

        productNameList = productHashSet.toMutableList()
        categoryList = categoryHashSet.toMutableList()

        val cursorAllSum = myDb.selectProjectAllSum(idProject)
        cursorAllSum.moveToFirst()
        allSumText.text = "Общая сумма: ${cursorAllSum.getDouble(0)} ₽"
        cursorAllSum.close()

        for (category in categoryList) {
            val cursorCategory = myDb.selectProjectAllSumCategory(idProject, category)
            while (cursorCategory.moveToNext()) {

                categorySumList.add(
                    Product(
                        cursorCategory.getString(0),
                        "₽",
                        cursorCategory.getDouble(1),
                        cursorCategory.getString(2)
                    )
                )
            }
            cursorCategory.close()
        }

        for (product in productNameList) {
            val cursorProduct: Cursor = myDb.selectProjectAllSumProduct(idProject, product)
            while (cursorProduct.moveToNext()) {
                productSumList.add(
                    Product(
                        cursorProduct.getString(0),
                        "₽",
                        cursorProduct.getDouble(2),
                        cursorProduct.getString(3)
                    )
                )
            }
            cursorProduct.close()
        }
        categorySumListNow.addAll(categorySumList)
        productSumListNow.addAll(productSumList)
    }

    //    @Throws(ParseException::class)
    fun filter() {
        productSumListNow.clear()
        categorySumListNow.clear()

        val format = SimpleDateFormat("dd.MM.yyyy")
        var sumAll = 0.0

        if (dataSheet.editText?.text.toString() != "") {
            for (productSum in productSumList) {
                val dateNow = format.parse(productSum.date)
                if (dateFirst.before(dateNow) && dateEnd.after(dateNow) || dateFirst == dateNow || dateEnd == dateNow) {
                    productSumListNow.add(productSum)
                    sumAll += productSum.price
                }
            }
            for (productCategory in categorySumList) {
                val dateNow = format.parse(productCategory.date)
                if (dateFirst.before(dateNow) && dateEnd.after(dateNow) || dateFirst == dateNow || dateEnd == dateNow) {
                    categorySumListNow.add(productCategory)
                }
            }
        }
        allSumText.text = "Общая сумма: $sumAll ₽"
    }


    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.conteiner, fragment, "visible_fragment")
            .addToBackStack(null)
            .commit()
    }

    fun showBottomSheetDialog() {
        bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomSheetDialog.setContentView(R.layout.fragment_bottom)
        val animalsSpinerSheet = bottomSheetDialog.findViewById<TextInputLayout>(R.id.menu)
        val categorySpinerSheet = bottomSheetDialog.findViewById<TextInputLayout>(R.id.menu2)
        animalsSpinerSheet?.visibility = View.GONE
        categorySpinerSheet?.visibility = View.GONE
        dataSheet = bottomSheetDialog.findViewById<TextInputLayout>(R.id.data_sheet)!!
        buttonSheet = bottomSheetDialog.findViewById<Button>(R.id.button_sheet)!!
    }


    companion object
}