package com.zaroslikov.myconstruction.db

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    name, null, version
) {
    companion object {
        private const val name = MyConstanta.Constanta.DB_NAME
        private const val version = MyConstanta.Constanta.DB_VERSION
    }


    override fun onCreate(p0: SQLiteDatabase) {
        p0.execSQL(MyConstanta.TABLE_STRUCTURE_PROJECT)
        p0.execSQL(MyConstanta.TABLE_STRUCTURE_PRODUCT)
        p0.execSQL(MyConstanta.TABLE_STRUCTURE_PRODUCTPROJECT)
        p0.execSQL(MyConstanta.TABLE_STRUCTURE_ADD)
        p0.execSQL(MyConstanta.TABLE_STRUCTURE_WRITEOFF)
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
        Log.w(
            MyDatabaseHelper::class.java.name,
            "Upgrading database from version " + p1 + " to "
                    + p2 + ", which will destroy all old data"
        )
        p0.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_PROJECT")
        p0.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_PRODUCT")
        p0.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_PRODUCTPROJECT")
        p0.execSQL("DROP TABLE IF EXISTS  MyConstanta.DROP_TABLE_ADD")
        p0.execSQL("DROP TABLE IF EXISTS MyConstanta.DROP_TABLE_WRITEOFF")
        onCreate(p0)
    }

    fun readProduct(): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM ${MyConstanta.Constanta.TABLE_NAME_PRODUCT}", null)
    }

    fun seachProduct(productName: String): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM ${MyConstanta.Constanta.TABLE_NAME_PRODUCT} Where ${MyConstanta.Constanta.TITLEPRODUCT} =?",
            arrayOf(productName)
        )
    }

    fun selectProductJoin(propertyId: Int, productName: String, tableName: String, suffix: String): Cursor{
        val db = this.readableDatabase
        return db.rawQuery("SELECT ${MyConstanta.Constanta.TITLEPRODUCT}, sum(${MyConstanta.Constanta.QUANTITY}), ${MyConstanta.Constanta.SUFFIX}" +
                " FROM $tableName ad " +
                "JOIN ${MyConstanta.Constanta.TABLE_NAME_PROJECT_PRODUCT} pp" +
                " ON pp.${BaseColumns._ID}) = ad.${MyConstanta.Constanta.IDPP}" +
                " JOIN ${MyConstanta.Constanta.TABLE_NAME_PRODUCT} + prod ON" +
                "prod.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPRODUCT}" +
                " JOIN ${MyConstanta.Constanta.TABLE_NAME} + proj "+
                "ON proj.${BaseColumns._ID} = pp.${MyConstanta.Constanta.IDPROJECT} " +
                "WHERE proj.${BaseColumns._ID} = ? and ${MyConstanta.Constanta.TITLEPRODUCT} = ? and ${MyConstanta.Constanta.SUFFIX} =? "+
                "group by ${MyConstanta.Constanta.TITLEPRODUCT}, ${MyConstanta.Constanta.SUFFIX}", arrayOf(propertyId.toString(),productName,suffix))

    }


}

