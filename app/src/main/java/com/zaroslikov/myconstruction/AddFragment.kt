package com.zaroslikov.myconstruction

import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
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

    private lateinit var myDB: MyDatabaseHelper
    private var productNameList = mutableListOf<String>()
    private var productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_add, container, false)
        myDB = MyDatabaseHelper(requireActivity())
        var idProject = MainActivity().projectNumer


        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.title = "Мои Покупки"
        appBar.menu.findItem(R.id.filler).setVisible(false)
        appBar.menu.findItem(R.id.moreAll).setVisible(true)
        appBar.menu.findItem(R.id.magazine).setVisible(true)
        appBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.moreAll -> {

                    appBar.title = "Информация"
                }
                R.id.magazine -> {

                }
            }
            true

        }

        appBar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        val productName = layout.findViewById<AutoCompleteTextView>(R.id.productName_editText)
        val add_edit = layout.findViewById<TextInputLayout>(R.id.add_edit)
        val price_edit = layout.findViewById< TextInputLayout>(R.id.price_edit)
        val suffixSpiner = layout.findViewById<AutoCompleteTextView>(R.id.suffixSpiner)
        val category = layout.findViewById<AutoCompleteTextView>(R.id.category_edit)
        val date = layout.findViewById<TextInputLayout>(R.id.date)
        val nowUnit = layout.findViewById<>(R.id.now_warehouse)

        val productNameMenu = layout.findViewById< TextInputLayout>(R.id.product_name_add_menu)
        val suffixMenu = layout.findViewById< TextInputLayout>(R.id.suffixSpiner)
        val categoryMenu = layout.findViewById< TextInputLayout>(R.id.category_add_menu)

        addProduct()

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
//        date.editText.text = "${calendar.get(Calendar.DAY_OF_MONTH)} " + (calendar.get(Calendar.MONTH) + 1) + "." + calendar.get(Calendar.YEAR))

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
            val productClick = productList.get(i).name
            val suffixClick = productList.get(i).suffix

            if (suffixSpiner.text.toString().equals("")){

            }
        }

        return layout
    }


    fun addProduct() {
        val cursor = myDB.readProduct()

        while (cursor.moveToNext()) {
            productNameList.add(cursor.getString(1))
            productList.add(Product(cursor.getInt(0), cursor.getString(1), cursor.getString(2)))
        }
        cursor.close()

        val tempList = mutableSetOf<String>()
        val cursor1 = myDB.seachProduct(idProject)

        while (cursor1.moveToNext()) {
            tempList.add(cursor1.getString(0))
        }
        cursor1.close()

        //todo

    }

    fun addDB(product:String, suffix:String){
        val cursor = myDB.

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}