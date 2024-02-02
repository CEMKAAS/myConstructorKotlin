package com.zaroslikov.myconstruction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.zaroslikov.myconstruction.db.MyDatabaseHelper

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

    lateinit var myDb:MyDatabaseHelper
    lateinit var allSumText: TextView
    lateinit var categoryText: TextView
    lateinit var productText:TextView

    private var productSumList = mutableListOf<Product>()
    private var categorySumList = mutableListOf<Product>()
    private var productSumListNow = mutableListOf<Product>()
    private var categorySumListNow = mutableListOf<Product>()

    private var productNameList = mutableListOf<String>()
    private var categoryList = mutableListOf<String>()





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
        appBar.menu.findItem(R.id.filler).setVisible(false)
        appBar.menu.findItem(R.id.deleteAll).setVisible(false)
        appBar.menu.findItem(R.id.moreAll).setVisible(true)
        appBar.menu.findItem(R.id.magazine).setVisible(true)
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

        add()



        return layout
    }

    fun add(idPoject:Int){
        val cursor = myDb.selectProjectAllProductAndCategoryAdd(idPoject)
        val productHashSet = mutableSetOf<String>()
        val categoryHashSet = mutableSetOf<String>()

        while (cursor.moveToNext()){
            productHashSet.add(cursor.getString(0))
            categoryHashSet.add(cursor.getString(2))
        }

        cursor.close()

        productNameList = productHashSet.toMutableList()
        categoryList = categoryHashSet.toMutableList()

        val cursorAllSum = myDb.selectProjectAllSum(idPoject)
        cursorAllSum.moveToFirst()
        allSumText.setText("Общая сумма: ${cursorAllSum.getDouble(0)} ₽")
        cursorAllSum.close()






    }




    companion object {

    }
}