package com.zaroslikov.myconstruction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zaroslikov.myconstruction.db.MyDatabaseHelper

class WriteOffFragment : Fragment() {

    lateinit var myDB: MyDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        return inflater.inflate(R.layout.fragment_write_off, container, false)
    }

    companion object {

    }
}