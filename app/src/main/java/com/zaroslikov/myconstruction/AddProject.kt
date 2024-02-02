package com.zaroslikov.myconstruction

import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.zaroslikov.myconstruction.db.MyDatabaseHelper
import java.text.SimpleDateFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddProject.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddProject : Fragment() {

    private lateinit var myDB: MyDatabaseHelper
    private lateinit var nameProject: TextInputLayout
    private lateinit var dateProject: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myDB = MyDatabaseHelper(requireActivity())
        val layout = inflater.inflate(R.layout.fragment_add_project, container, false)
        val fab = requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.extended_fab)
        fab.visibility = View.GONE

        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.title = "Добавить проект"
        appBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        appBar.menu.findItem(R.id.filler).setVisible(false)
        appBar.menu.findItem(R.id.deleteAll).setVisible(false)
        appBar.menu.findItem(R.id.moreAll).setVisible(true)
        appBar.menu.findItem(R.id.magazine).setVisible(false)
        appBar.setOnMenuItemClickListener {
            when (it.itemId) {
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

        nameProject = layout.findViewById(R.id.name_project)
        dateProject = layout.findViewById(R.id.date)

        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
//        dateProject.editText?.text = "${calendar.get(Calendar.DAY_OF_MONTH)} . ${calendar.get(Calendar.MONTH+1)}. {"

        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())
            .build()

        val dataPicker = MaterialDatePicker.Builder.datePicker()
            .setCalendarConstraints(constraintsBuilder)
            .setTitleText("Выберите дату").setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        dateProject.setOnClickListener {
            dataPicker.show(requireActivity().supportFragmentManager, "wer")
            dataPicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = it
                val format = SimpleDateFormat("dd.MM.yyyy")
                val formatteDate: String = format.format(calendar.time)
                dateProject.editText?.setText(formatteDate)
            }
        }

        val add = layout.findViewById<Button>(R.id.begin)
        add.setOnClickListener {
            addPrject()
        }

        return layout
    }


    fun addPrject() {
        val name = nameProject.editText?.text.toString()
        val date = dateProject.editText?.text.toString()

        val cursor = myDB.readProduct()
        cursor.count

        if ("" in listOf(
                name, date
            )
        ) {
            if (name.equals("")){
                nameProject.error = "Укажите имя проекта!"
                nameProject.error
            }
            if (date.equals("")){
                dateProject.error = "Укажите дату!"
                dateProject.error
            }
        }else{
            myDB.insertToDbProject(name,date,0)
            replaceFragment(MenuProjectFragment)
        }
    }

    fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.container,fragment)
            .commit()

    }


}