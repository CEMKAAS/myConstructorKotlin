package com.zaroslikov.myconstruction

import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.zaroslikov.myconstruction.db.MyDatabaseHelper
import com.zaroslikov.myconstruction.project.MenuProjectFragment
import java.text.SimpleDateFormat

class AddProjectFragment : Fragment() {

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
        appBar.menu.findItem(R.id.filler).isVisible = false
        appBar.menu.findItem(R.id.deleteAll).isVisible = false
        appBar.menu.findItem(R.id.moreAll).isVisible = true
        appBar.menu.findItem(R.id.magazine).isVisible = false
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


        // Настройка календаря
        val calendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
        dateProject.editText!!.setText(calendar[java.util.Calendar.DAY_OF_MONTH].toString() + "." + (calendar[java.util.Calendar.MONTH] + 1) + "." + calendar[java.util.Calendar.YEAR])

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


    private fun addPrject() {
        val name = nameProject.editText?.text.toString()
        val date = dateProject.editText?.text.toString()

        val cursor = myDB.readProduct()
        cursor.count

        if ("" in listOf(
                name, date
            )
        ) {
            if (name == ""){
                nameProject.error = "Укажите имя проекта!"
                nameProject.error
            }
            if (date == ""){
                dateProject.error = "Укажите дату!"
                dateProject.error
            }
        }else{
            myDB.insertToDbProject(name,date,0)
            replaceFragment(MenuProjectFragment())
        }
    }

    fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.container,fragment)
            .commit()

    }


}