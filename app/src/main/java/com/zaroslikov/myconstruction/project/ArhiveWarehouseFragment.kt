package com.zaroslikov.myconstruction.project

import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.zaroslikov.myconstruction.InFragment
import com.zaroslikov.myconstruction.MagazineManagerFragment
import com.zaroslikov.myconstruction.MainActivity
import com.zaroslikov.myconstruction.Product
import com.zaroslikov.myconstruction.ProductAdapter
import com.zaroslikov.myconstruction.ProductArhiveAdapter
import com.zaroslikov.myconstruction.R
import com.zaroslikov.myconstruction.db.MyConstanta
import com.zaroslikov.myconstruction.db.MyDatabaseHelper

class ArhiveWarehouseFragment : Fragment() {

    private lateinit var dataTxt: TextView
    private lateinit var allSumText: TextView
    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var recyclerViewProduct: RecyclerView
    private lateinit var myDB: MyDatabaseHelper
    private var name = mutableListOf<String>()
    private var data = mutableListOf<String>()
    private var id = mutableListOf<Int>()
    private var productSumList = mutableListOf<Product>()
    private var categorySumList = mutableListOf<Product>()
    private var productSumListNow = mutableListOf<Product>()
    private var categorySumListNow = mutableListOf<Product>()
    private var productNameList = mutableListOf<String>()
    private var categoryList = mutableListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_arhive_warehouse, container, false)


        //убириаем фаб кнопку
        myDB = MyDatabaseHelper(requireContext())
        val bundle = this.arguments


        var nameProject = ""
        var dateProject = ""

        if (bundle != null) {
            nameProject = bundle.getString("name").toString()
            dateProject = bundle.getString("date").toString()
        }

        //убириаем фаб кнопку
        val fab =
            requireActivity().findViewById<View>(R.id.extended_fab) as ExtendedFloatingActionButton
        fab.visibility = View.GONE

        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.title = nameProject
        appBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
        appBar.menu.findItem(R.id.deleteAll).isVisible = false
        appBar.menu.findItem(R.id.filler).isVisible = false
        appBar.menu.findItem(R.id.magazine).isVisible = true
        appBar.setOnMenuItemClickListener { item: MenuItem ->
            val position = item.itemId
            if (position == R.id.moreAll) {
                replaceFragment(InFragment())
                appBar.title = "Информация"
            } else if (position == R.id.magazine) {
                replaceFragment(MagazineManagerFragment())
            }
            true
        }

        appBar.setNavigationOnClickListener { replaceFragment(MenuProjectFragment()) }

        val idProject = MainActivity().projectNumer

        dataTxt = layout.findViewById<TextView>(R.id.data_txt)
        allSumText = layout.findViewById<TextView>(R.id.all_sum)

        dataTxt.text = dateProject

        add(idProject)

        // Настраиваем адаптер
        recyclerViewCategory = layout.findViewById(R.id.recyclerView)
        recyclerViewProduct = layout.findViewById(R.id.recyclerViewAll)

        val productAdapterCategory = ProductAdapter(categorySumListNow, true)
        recyclerViewCategory.adapter = productAdapterCategory
        recyclerViewCategory.layoutManager = LinearLayoutManager(
            activity
        )

        val productAdapterProduct = ProductArhiveAdapter(productSumListNow)
        recyclerViewProduct.adapter = productAdapterProduct
        recyclerViewProduct.layoutManager = LinearLayoutManager(
            activity
        )


        val returnButton = layout.findViewById<Button>(R.id.return_button)
        returnButton.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Вернуть проект?")
            builder.setMessage(
                "Возможно вы еще не доконца закончили проект." +
                        " Его можно будет вернуть потом обратно в архив!"
            )
            builder.setPositiveButton(
                "Да"
            ) { dialogInterface, i ->
                myDB.updateToDbProject(idProject, 0, "")
                replaceFragment(MenuProjectFragment())
            }
            builder.setNegativeButton(
                "Нет"
            ) { dialog, which -> }
            builder.show()
        }

        val deleteButton = layout.findViewById<Button>(R.id.end_button)
        deleteButton.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(requireContext())
            builder.setTitle("Удалить проект?")
            builder.setMessage("Ваш проект удалиться со всеми данными, вы уверенны? ")
            builder.setPositiveButton(
                "Да"
            ) { dialogInterface, i ->
                myDB.deleteOneRowAdd(idProject, MyConstanta.Constanta.TABLE_NAME)
                replaceFragment(MenuProjectFragment())
            }
            builder.setNegativeButton(
                "Нет"
            ) { dialog, which -> }
            builder.show()
        }

        return layout
    }

    private fun add (idProject: Int) {
        val cursorProductAdd = myDB.selectProjectAllProductAndCategoryAdd(idProject)

        //Добавляем в списки продукты и категории
        val productHashSet: MutableSet<String> = HashSet()
        val categoryHashSet: MutableSet<String> = HashSet()

        while (cursorProductAdd.moveToNext()) {
            productHashSet.add(cursorProductAdd.getString(0))
            categoryHashSet.add(cursorProductAdd.getString(2))
        }
        cursorProductAdd.close()

        productNameList = productHashSet.toMutableList()
        categoryList = productHashSet.toMutableList()

        val cursorAllSum = myDB.selectProjectAllSum(idProject)
        cursorAllSum.moveToFirst()
        val allSum = cursorAllSum.getDouble(0)
        allSumText.text = "Общая сумма: $allSum ₽"
        cursorAllSum.close()


        for (category in categoryList) {
            val cursorCategory = myDB.selectProjectAllSumCategory(
                idProject,
                category
            )
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
            val cursorProduct = myDB.selectProjectAllSumProductAndCount(idProject, product)
            while (cursorProduct.moveToNext()) {
                productSumList.add(
                    Product(
                        cursorProduct.getString(0),
                        cursorProduct.getString(1),
                        cursorProduct.getDouble(2),
                        cursorProduct.getString(3),
                        cursorProduct.getDouble(4)
                    )
                )
            }
            cursorProduct.close()
        }

        categorySumListNow.addAll(categorySumList)
        productSumListNow.addAll(productSumList)
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