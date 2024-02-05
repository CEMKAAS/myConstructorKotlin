package com.zaroslikov.myconstruction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class InFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fab = requireActivity().findViewById<ExtendedFloatingActionButton>(R.id.extended_fab)
        fab.visibility = View.GONE

        val appBar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.title = "Информация"
        appBar.menu.findItem(R.id.filler).setVisible(false)
        appBar.menu.findItem(R.id.deleteAll).setVisible(false)
        appBar.menu.findItem(R.id.moreAll).setVisible(false)
        appBar.menu.findItem(R.id.magazine).setVisible(false)
        appBar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return inflater.inflate(R.layout.fragment_in, container, false)
    }

}